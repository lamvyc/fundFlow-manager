# 🎓 发钱管理系统 - 学习指南

## 📋 当前进度

- ✅ Step 0: 项目结构创建完成
- ✅ Step 1: 数据库搭建完成
- ✅ Step 2: 实体类创建完成
- ✅ Step 3: 用户管理（CRUD）完成
- ✅ Step 4: 账户管理（事务处理）完成
- ✅ Step 5: 发钱功能（复杂事务）完成
- ✅ Step 6: 统计功能完成
- ✅ Step 7: 完整系统完成

**当前完成度：100% ✅ 项目已全部完成！**

---

## 🎉 已完成的学习内容

### ✅ Step 1: 数据库搭建
**学到了什么：**
- 创建数据库和表
- 主键、外键、索引的使用
- 数据库设计三范式
- 测试数据的插入

**关键文件：**
- `sql/init.sql` - 数据库初始化脚本

---

### ✅ Step 2: 实体类
**学到了什么：**
- 实体类的封装（private 字段）
- getter/setter 方法的作用
- 构造函数的使用
- toString() 方法的重写
- 为什么金额用 BigDecimal 而不是 double

**关键文件：**
- `src/moneyTransfer/entity/User.java`
- `src/moneyTransfer/entity/Account.java`
- `src/moneyTransfer/entity/PaymentRecord.java`
- `src/moneyTransfer/entity/RechargeRecord.java`

**核心知识点：**
```java
// BigDecimal 精确计算
BigDecimal balance = new BigDecimal("100.00");
BigDecimal amount = new BigDecimal("50.00");
BigDecimal newBalance = balance.add(amount);  // 150.00

// double 有精度误差
double d = 0.1 + 0.2;  // 0.30000000000000004 ❌
```

---

### ✅ Step 3: 用户管理（CRUD）
**学到了什么：**
- JDBC 连接管理
- PreparedStatement 的使用（防 SQL 注入）
- ResultSet 结果集处理
- DAO 设计模式
- 软删除 vs 硬删除

**关键文件：**
- `src/moneyTransfer/dao/UserDao.java`
- `src/moneyTransfer/Step3_UserDaoTest.java`

**核心知识点：**
```java
// JDBC 标准流程
Connection conn = DBUtil.getConnection();
PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
pstmt.setInt(1, userId);
ResultSet rs = pstmt.executeQuery();
while (rs.next()) {
    // 处理结果
}
rs.close();
pstmt.close();
conn.close();
```

---

### ✅ Step 4: 账户管理（事务处理）⭐
**学到了什么：**
- 数据库事务（ACID 特性）
- setAutoCommit(false) / commit() / rollback()
- SELECT ... FOR UPDATE 悲观锁
- 充值流水记录
- 事务的原子性保证

**关键文件：**
- `src/moneyTransfer/dao/AccountDao.java`
- `src/moneyTransfer/Step4_AccountDaoTest.java`
- `docs/TRANSACTION_GUIDE.md` ⭐（必读）

**核心知识点：**
```java
// 事务处理三步走
conn.setAutoCommit(false);  // 1. 开启事务
try {
    // 2. 执行多个操作
    // 更新余额
    // 插入流水记录
    conn.commit();  // 3. 提交事务
} catch (Exception e) {
    conn.rollback();  // 失败则回滚
}
```

---

### ✅ Step 5: 发钱功能（复杂事务）⭐⭐
**学到了什么：**
- 复杂事务处理（多表操作的原子性）
- 业务逻辑层（Service 层）设计
- 自定义异常（InsufficientBalanceException 等）
- FOR UPDATE 悲观锁防止并发问题
- 异常分层处理（业务异常 vs 系统异常）

**关键文件：**
- `src/moneyTransfer/dao/PaymentRecordDao.java`
- `src/moneyTransfer/service/PaymentService.java`
- `src/moneyTransfer/exception/InsufficientBalanceException.java`
- `src/moneyTransfer/exception/AccountFrozenException.java`
- `src/moneyTransfer/exception/InvalidAmountException.java`
- `src/moneyTransfer/Step5_PaymentServiceTest.java`

**核心知识点：**
```java
// 发钱 = 扣款 + 创建记录，两步必须在同一事务内
conn.setAutoCommit(false);
try {
    // 1. 查询并锁定账户（FOR UPDATE）
    // 2. 检查余额/状态（业务逻辑验证）
    // 3. 扣减余额
    // 4. 创建发钱记录
    conn.commit();
} catch (InsufficientBalanceException e) {
    conn.rollback();
    throw e;  // 重新抛出业务异常
}
```

---

### ✅ Step 6: 统计功能（JOIN + 聚合）⭐⭐
**学到了什么：**
- INNER JOIN / LEFT JOIN 多表关联
- 聚合函数：SUM、COUNT、AVG、MAX、MIN
- GROUP BY 分组统计
- HAVING 过滤分组结果（与 WHERE 的区别）
- ORDER BY + LIMIT 实现 TOP N 查询
- 子查询（SELECT 嵌套 SELECT）
- COALESCE 处理 NULL 值

**关键文件：**
- `src/moneyTransfer/dao/StatisticsDao.java`
- `src/moneyTransfer/service/StatisticsService.java`
- `src/moneyTransfer/Step6_StatisticsTest.java`

**核心知识点：**
```sql
-- LEFT JOIN + GROUP BY + 聚合函数
SELECT a.name, COUNT(p.id) AS payment_count, SUM(p.amount) AS total_paid
FROM accounts a
LEFT JOIN payment_records p ON a.id = p.account_id AND p.status = 'COMPLETED'
GROUP BY a.id, a.name
HAVING payment_count >= 1      -- HAVING 过滤聚合结果
ORDER BY total_paid DESC
LIMIT 3;                        -- TOP 3
```

---

### ✅ Step 7: 完整系统整合⭐⭐⭐
**学到了什么：**
- 完整的三层架构：界面层 → Service 层 → DAO 层
- 交互式命令行菜单系统
- 各模块协同工作（用户/账户/发钱/统计）
- 系统级别的异常处理
- 端到端集成测试

**关键文件：**
- `src/moneyTransfer/Main.java` - 交互式命令行完整系统
- `src/moneyTransfer/Step7_SystemTest.java` - 集成测试

**核心知识点：**
```
系统架构
  Main.java（界面层：菜单交互）
      ↓ 调用
  Service 层（业务逻辑：PaymentService, StatisticsService）
      ↓ 调用
  DAO 层（数据访问：UserDao, AccountDao, PaymentRecordDao, StatisticsDao）
      ↓ 操作
  MySQL（money_transfer_db）
```

---

## 📚 推荐阅读顺序

### 新手必读文档

1. **docs/LEARNING_GUIDE.md**（本文件）- 学习路线图
2. **docs/JDBC_CHEATSHEET.md** - JDBC 常用方法速查
3. **docs/TRANSACTION_GUIDE.md** - 事务详解（Step 4 必读）

### 问题排查文档

4. **docs/IDEA_SETUP.md** - IDEA 配置指南
5. **README.md** - 常见问题解答

### 进阶文档

6. **docs/PROJECT_OVERVIEW.md** - 项目全貌和统计

---

## 🎯 回顾所有 Step

```bash
# 运行任意一个 Step 的测试
./run.sh

# 选项说明：
# 1) 测试数据库连接
# 2) Step1: 数据库搭建
# 3) Step2: 实体类测试
# 4) Step3: 用户管理测试
# 5) Step4: 账户管理测试
# 6) Step5: 发钱功能测试
# 7) Step6: 统计功能测试
# 8) Step7: 完整系统集成测试
# 9) Main: 交互式完整系统
```

---

## 🗄️ 数据库表结构说明

### 1. users（用户表）- 存储收款人信息
```
id          用户ID（自动增长）
name        姓名
phone       手机号（唯一）
bank_card   银行卡号（唯一）
bank_name   开户行
status      状态（ACTIVE=正常, DELETED=已删除）
create_time 创建时间
update_time 更新时间
```

### 2. accounts（账户表）- 存储发钱账户信息
```
id          账户ID
name        账户名称
balance     余额（不能为负）
status      状态（ACTIVE=正常, FROZEN=冻结）
create_time 创建时间
update_time 更新时间
```

### 3. payment_records（发钱记录表）
```
id          记录ID
account_id  发钱账户ID（外键 → accounts.id）
user_id     收款用户ID（外键 → users.id）
amount      金额
status      状态（PENDING=待处理, COMPLETED=已完成, CANCELLED=已取消）
remark      备注
create_time 创建时间
update_time 更新时间
```

### 4. recharge_records（充值记录表）
```
id             记录ID
account_id     账户ID（外键 → accounts.id）
amount         充值金额
before_balance 充值前余额
after_balance  充值后余额
create_time    充值时间
```

---

## 🔑 关键概念解释

### 什么是主键（PRIMARY KEY）？
- 唯一标识一行数据的字段
- 例如：`id INT PRIMARY KEY AUTO_INCREMENT`
- `AUTO_INCREMENT` 表示自动增长（插入数据时不用指定 id，数据库自动分配）

### 什么是外键（FOREIGN KEY）？
- 关联两张表的字段
- 例如：`payment_records.account_id` → `accounts.id`
- 作用：保证数据一致性（不能发钱给不存在的账户）

### 什么是索引（INDEX）？
- 加速查询的数据结构（类似书的目录）
- 例如：`INDEX idx_phone (phone)` 让按手机号查询更快
- 但会占用额外空间，且影响插入速度

### 什么是约束（CHECK）？
- 限制字段的取值范围
- 例如：`CHECK (balance >= 0)` 保证余额不能为负数

---

## ❓ 常见问题

### Q1: 执行 init.sql 时报错 "Access denied"
**A:** 密码错误，检查 config/db.properties 中的密码是否正确

### Q2: 执行 init.sql 时报错 "database exists"
**A:** 数据库已存在，这是正常的（脚本会先删除再创建）

### Q3: Java 程序连接失败
**A:** 检查以下几点：
1. MySQL 服务是否启动
2. 数据库名称是否正确（money_transfer_db）
3. 用户名密码是否正确
4. 端口号是否正确（默认 3306）
5. 连接 URL 中是否加了 `allowPublicKeyRetrieval=true`

### Q4: 找不到 MySQL 驱动
**A:** 需要下载 MySQL JDBC 驱动（mysql-connector-java.jar）
- 下载地址: https://dev.mysql.com/downloads/connector/j/
- 解压后将 jar 文件放到项目根目录
- 编译时加上 `-cp mysql-connector-java.jar`

---

## 📚 学习建议

1. **动手实践**：不要只看代码，一定要自己运行
2. **理解原理**：不要死记硬背，理解为什么这样做
3. **循序渐进**：每个 Step 都测试通过后再进行下一步
4. **勤于提问**：遇到不懂的概念立即问我

项目已全部完成，继续加油！💪
