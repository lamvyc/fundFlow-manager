# 🎓 发钱管理系统 - 学习指南

## 📋 当前进度

- ✅ Step 0: 项目结构创建完成
- ✅ 工具类创建完成 (DBUtil, PrintUtil)
- 🔄 **你现在在这里 → Step 1: 数据库搭建**
- ⏳ Step 2: 实体类
- ⏳ Step 3: 用户管理
- ⏳ Step 4: 账户管理
- ⏳ Step 5: 发钱功能
- ⏳ Step 6: 统计功能
- ⏳ Step 7: 完整系统

---

## 🎯 Step 1: 数据库搭建（你的第一个任务）

### 需要做什么？

1. **修改数据库配置文件**
   - 打开 `config/db.properties`
   - 把 `db.password=your_password` 改成你的 MySQL 密码

2. **执行数据库初始化脚本**
   
   **方式1：命令行执行（推荐）**
   ```bash
   # 打开终端，执行：
   mysql -u root -p
   # 输入密码后，执行：
   source /Users/unravel/fundFlow-manager/sql/init.sql
   
   # 验证是否成功：
   SHOW TABLES;
   # 应该看到 4 张表：users, accounts, payment_records, recharge_records
   
   # 查看测试数据：
   SELECT * FROM users;
   SELECT * FROM accounts;
   ```

   **方式2：使用 Java 程序（可选）**
   ```bash
   cd /Users/unravel/fundFlow-manager
   
   # 编译
   javac -d . -encoding UTF-8 src/moneyTransfer/Step1_DatabaseSetup.java src/moneyTransfer/util/*.java
   
   # 运行
   java moneyTransfer.Step1_DatabaseSetup
   ```

3. **测试数据库连接**
   ```bash
   javac -d . -encoding UTF-8 src/moneyTransfer/util/DBUtil.java src/moneyTransfer/util/PrintUtil.java
   java moneyTransfer.util.DBUtil
   
   # 如果看到 "✓ 数据库连接成功！" 就说明配置正确
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

**测试数据：**
- 张三：13800138000，工商银行
- 李四：13900139000，建设银行
- 王五：13700137000，农业银行

### 2. accounts（账户表）- 存储发钱账户信息
```
id          账户ID
name        账户名称
balance     余额（不能为负）
status      状态（ACTIVE=正常, FROZEN=冻结）
create_time 创建时间
update_time 更新时间
```

**测试数据：**
- 公司发薪账户：余额 100,000 元
- 奖金发放账户：余额 50,000 元

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

## 📝 完成 Step 1 后的检查清单

执行以下 SQL 验证是否成功：

```sql
-- 1. 切换到数据库
USE money_transfer_db;

-- 2. 查看所有表（应该有 4 张表）
SHOW TABLES;

-- 3. 查看用户表数据（应该有 3 个用户）
SELECT * FROM users;

-- 4. 查看账户表数据（应该有 2 个账户）
SELECT * FROM accounts;

-- 5. 查看发钱记录表（应该是空的）
SELECT * FROM payment_records;

-- 6. 查看充值记录表（应该是空的）
SELECT * FROM recharge_records;

-- 7. 查看表结构
DESC users;
DESC accounts;
```

---

## ❓ 常见问题

### Q1: 执行 init.sql 时报错 "Access denied"
**A:** 密码错误，检查 config/db.properties 中的密码是否正确

### Q2: 执行 init.sql 时报错 "database exists"
**A:** 数据库已存在，这是正常的（脚本会先删除再创建）

### Q3: Java 程序连接失败
**A:** 检查以下几点：
1. MySQL 服务是否启动（`sudo systemctl status mysql` 或打开系统偏好设置查看）
2. 数据库名称是否正确（money_transfer_db）
3. 用户名密码是否正确
4. 端口号是否正确（默认 3306）

### Q4: 找不到 MySQL 驱动
**A:** 需要下载 MySQL JDBC 驱动（mysql-connector-java.jar）
- 下载地址: https://dev.mysql.com/downloads/connector/j/
- 解压后将 jar 文件放到项目根目录
- 编译时加上 `-cp mysql-connector-java.jar`

---

## 🎉 完成后做什么？

当你看到数据库中有 4 张表，并且有测试数据时，说明 Step 1 完成！

**下一步：Step 2 - 创建实体类**
- 学习如何用 Java 类表示数据库表
- 理解 getter/setter 方法
- 掌握对象封装

**如果遇到问题，随时告诉我！我会帮你解决。**

---

## 📚 学习建议

1. **动手实践**：不要只看代码，一定要自己运行
2. **理解原理**：不要死记硬背，理解为什么这样做
3. **循序渐进**：每个 Step 都测试通过后再进行下一步
4. **勤于提问**：遇到不懂的概念立即问我

加油！💪
