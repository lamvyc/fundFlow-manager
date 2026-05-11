# 💰 发钱管理系统

一个用于学习 Java + JDBC + MySQL 的渐进式项目

---

## 📁 项目结构

```
fundFlow-manager/
├── config/                      # 配置文件
│   └── db.properties           # 数据库配置（需要修改密码）
│
├── sql/                         # SQL 脚本
│   └── init.sql                # 数据库初始化脚本
│
├── src/
│   └── moneyTransfer/
│       ├── Step1_DatabaseSetup.java      # Step 1: 数据库搭建
│       ├── Step2_EntityTest.java         # Step 2: 实体类测试（待完成）
│       ├── Step3_UserDaoTest.java        # Step 3: 用户管理（待完成）
│       ├── Step4_AccountDaoTest.java     # Step 4: 账户管理（待完成）
│       ├── Step5_PaymentTest.java        # Step 5: 发钱功能（待完成）
│       ├── Step6_StatisticsTest.java     # Step 6: 统计功能（待完成）
│       ├── Main.java                     # 完整系统（待完成）
│       │
│       ├── entity/              # 实体类（对应数据库表）
│       │   ├── User.java       # 用户实体（待完成）
│       │   ├── Account.java    # 账户实体（待完成）
│       │   ├── PaymentRecord.java  # 发钱记录实体（待完成）
│       │   └── RechargeRecord.java # 充值记录实体（待完成）
│       │
│       ├── dao/                 # 数据访问层
│       │   ├── UserDao.java    # 用户DAO（待完成）
│       │   ├── AccountDao.java # 账户DAO（待完成）
│       │   └── PaymentRecordDao.java  # 发钱记录DAO（待完成）
│       │
│       ├── service/             # 业务逻辑层
│       │   ├── UserService.java       # 用户服务（待完成）
│       │   ├── AccountService.java    # 账户服务（待完成）
│       │   ├── PaymentService.java    # 发钱服务（待完成）
│       │   └── StatisticsService.java # 统计服务（待完成）
│       │
│       ├── util/                # 工具类
│       │   ├── DBUtil.java     # 数据库连接工具 ✅
│       │   └── PrintUtil.java  # 打印工具 ✅
│       │
│       └── exception/           # 自定义异常（待完成）
│           ├── InsufficientBalanceException.java  # 余额不足异常
│           ├── AccountFrozenException.java        # 账户冻结异常
│           └── BusinessException.java             # 通用业务异常
│
├── run.sh                       # 快速运行脚本 ✅
├── LEARNING_GUIDE.md           # 学习指南 ✅
└── README.md                    # 本文件
```

---

## 🚀 快速开始

### 1️⃣ 修改数据库配置

编辑 `config/db.properties`，修改你的 MySQL 密码：

```properties
db.password=你的MySQL密码
```

### 2️⃣ 初始化数据库

**方式1：命令行执行（推荐）**
```bash
mysql -u root -p
source /Users/unravel/fundFlow-manager/sql/init.sql
```

**方式2：使用 Java 程序**
```bash
./run.sh
# 选择 2) Step1: 数据库搭建
```

### 3️⃣ 测试数据库连接

```bash
./run.sh
# 选择 1) 测试数据库连接
```

如果看到 "✓ 数据库连接成功！"，说明配置正确！

---

## 📚 学习路线

按照以下顺序学习，每个 Step 都可以独立运行：

| Step | 内容 | 学习重点 | 状态 |
|------|------|---------|------|
| Step 0 | 项目结构搭建 | 项目组织、配置管理 | ✅ 已完成 |
| Step 1 | 数据库搭建 | 表设计、主键外键、索引 | ✅ 已完成 |
| Step 2 | 实体类 | 类的封装、getter/setter | ⏳ 待完成 |
| Step 3 | 用户管理 | JDBC、PreparedStatement、CRUD | ⏳ 待完成 |
| Step 4 | 账户管理 | 事务处理、充值流水 | ⏳ 待完成 |
| Step 5 | 发钱功能 | 复杂事务、回滚、异常处理 | ⏳ 待完成 |
| Step 6 | 统计功能 | JOIN、GROUP BY、聚合函数 | ⏳ 待完成 |
| Step 7 | 完整系统 | 菜单系统、整合所有功能 | ⏳ 待完成 |

---

## 🎯 项目功能

### 1. 用户管理
- 添加收款人（姓名、手机号、银行卡）
- 查询用户（按ID、姓名、手机号）
- 更新用户信息
- 删除用户（软删除）

### 2. 账户管理
- 创建发钱账户
- 充值（记录充值流水）
- 查询余额
- 账户冻结/解冻

### 3. 发钱功能（核心）
- 发钱（扣款 + 创建记录，事务保证）
- 取消发钱（退款）
- 查询发钱记录（多条件查询）

### 4. 统计报表
- 统计账户发钱总额
- 统计用户收款排行
- 按时间段统计
- 余额预警

---

## 🛠️ 使用方法

### 运行脚本（推荐）

```bash
./run.sh
```

会显示菜单，选择要运行的程序。

### 手动编译运行

```bash
# 编译
javac -d . -encoding UTF-8 src/moneyTransfer/**/*.java src/moneyTransfer/*.java

# 运行（例如 Step1）
java moneyTransfer.Step1_DatabaseSetup
```

---

## 💡 为什么不用 Spring？

这个项目使用原生 JDBC，是为了：

1. **理解底层原理**：Spring 是自动化工具，但你需要先理解它自动化了什么
2. **掌握基础知识**：JDBC 是面试必考，也是理解 ORM 框架的基础
3. **提升调试能力**：出错时能快速定位到底层原因
4. **循序渐进**：先学"手动挡"，再学"自动挡"

**学习路径：**
```
原生 JDBC (本项目) → Spring + MyBatis → Spring Cloud 微服务
     ↑
   你在这里
```

---

## 📖 数据库表

### users（用户表）
```sql
id, name, phone, bank_card, bank_name, status, create_time, update_time
```

### accounts（账户表）
```sql
id, name, balance, status, create_time, update_time
```

### payment_records（发钱记录表）
```sql
id, account_id, user_id, amount, status, remark, create_time, update_time
```

### recharge_records（充值记录表）
```sql
id, account_id, amount, before_balance, after_balance, create_time
```

---

## ❓ 常见问题

### 编译报错找不到类？
确保在项目根目录下执行命令：
```bash
cd /Users/unravel/fundFlow-manager
```

### 连接数据库失败？
1. 检查 MySQL 服务是否启动
2. 检查 `config/db.properties` 中的密码是否正确
3. 检查数据库 `money_transfer_db` 是否创建成功

### 中文显示乱码？
编译时加上 `-encoding UTF-8`：
```bash
javac -d . -encoding UTF-8 src/moneyTransfer/*.java
```

---

## 📚 学习资源

- **详细学习指南**：`LEARNING_GUIDE.md`
- **需求文档**：查看用户提供的原始需求
- **SQL 脚本**：`sql/init.sql`（包含详细注释）
- **代码注释**：每个文件都有详细的代码注释和原理说明

---

## 🎉 项目完成后你会掌握

1. ✅ 数据库设计（主键、外键、索引）
2. ✅ JDBC 操作（CRUD）
3. ✅ 事务处理（ACID）
4. ✅ SQL 查询（JOIN、GROUP BY、聚合函数）
5. ✅ 代码分层（DAO、Service、Entity）
6. ✅ 异常处理
7. ✅ 并发控制（悲观锁）

---

## 📞 获取帮助

遇到问题随时问我：
- 不理解某个概念
- 代码运行报错
- 不知道下一步怎么做

我会一步步带你完成！💪

---

**开始学习吧！打开 `LEARNING_GUIDE.md` 查看详细指导。**
