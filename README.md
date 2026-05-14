# 💰 发钱管理系统

一个用于学习 Java + JDBC + MySQL 的渐进式项目

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-blue.svg)](https://www.mysql.com/)

---

## 📋 目录

- [环境要求](#-环境要求)
- [快速开始](#-快速开始)
- [项目结构](#-项目结构)
- [学习路线](#-学习路线)
- [依赖说明](#-依赖说明)
- [常见问题](#-常见问题)

---

## 💻 环境要求

在开始之前，请确保你的系统已安装以下软件：

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| **JDK** | 8 或更高 | Java 开发工具包 |
| **MySQL** | 8.0 或更高 | 数据库服务器 |
| **Git** | 任意版本 | 版本控制（可选） |

### 检查环境

```bash
# 检查 Java 版本
java -version

# 检查 MySQL 版本
mysql --version

# 检查 MySQL 是否运行
# macOS/Linux:
sudo systemctl status mysql
# 或
ps aux | grep mysql
```

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

### 方式一：克隆项目（推荐）

```bash
# 1. 克隆项目
git clone <your-repo-url>
cd fundFlow-manager

# 2. 安装依赖（自动下载 MySQL 驱动）
./install.sh

# 3. 配置数据库
# 编辑 config/db.properties，修改密码
vim config/db.properties

# 4. 初始化数据库
mysql -u root -p < sql/init.sql

# 5. 测试运行
./run.sh
```

### 方式二：从零搭建

如果你想从零开始搭建项目（学习用），按照以下步骤：

#### 1️⃣ 安装 MySQL JDBC 驱动

**自动安装（推荐）：**

```bash
# 运行安装脚本（会自动下载驱动）
./install.sh
```

**手动安装：**

```bash
# 下载 MySQL JDBC 驱动
curl -L -o mysql-connector-java-8.0.33.jar \
  https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar

# 或者从官网下载：
# https://dev.mysql.com/downloads/connector/j/
```

**驱动说明：**
- ✅ 本项目已包含 `mysql-connector-java-8.0.33.jar`（2.4MB）
- ✅ 克隆项目后**无需额外下载**
- ✅ 如果文件丢失，运行 `./install.sh` 自动下载

#### 2️⃣ 配置数据库

编辑 `config/db.properties`，修改你的 MySQL 配置：

```properties
# 数据库地址（默认 localhost）
db.url=jdbc:mysql://localhost:3306/money_transfer_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8

# 数据库用户名（默认 root）
db.username=root

# 数据库密码（⚠️ 必须修改！）
db.password=你的MySQL密码

# 数据库驱动类（无需修改）
db.driver=com.mysql.cj.jdbc.Driver
```

#### 3️⃣ 初始化数据库

**方式1：命令行执行（推荐）**

```bash
# 连接到 MySQL
mysql -u root -p

# 执行初始化脚本
source /path/to/fundFlow-manager/sql/init.sql

# 或者直接执行
mysql -u root -p < sql/init.sql

# 验证是否成功
mysql -u root -p -e "USE money_transfer_db; SHOW TABLES;"
```

**方式2：使用 Java 程序**

```bash
./run.sh
# 选择 2) Step1: 数据库搭建
```

**初始化脚本会自动：**
- ✅ 创建数据库 `money_transfer_db`
- ✅ 创建 4 张表（users, accounts, payment_records, recharge_records）
- ✅ 插入测试数据（3 个用户，2 个账户）

#### 4️⃣ 测试数据库连接

```bash
./run.sh
# 选择 1) 测试数据库连接
```

**期望输出：**

```
✓ 数据库驱动加载成功
========================================
  测试数据库连接
========================================

正在连接数据库...
✓ 数据库连接成功！
  数据库地址: jdbc:mysql://localhost:3306/money_transfer_db...
  用户名: root

连接已关闭
```

如果看到 "✓ 数据库连接成功！"，说明配置正确！🎉

---

## 📦 依赖说明

### 必需依赖

本项目**只有 1 个外部依赖**：

| 依赖 | 版本 | 大小 | 用途 | 是否包含 |
|------|------|------|------|---------|
| **mysql-connector-java** | 8.0.33 | 2.4MB | JDBC 驱动，连接 MySQL 数据库 | ✅ 已包含 |

### 依赖位置

```
fundFlow-manager/
└── mysql-connector-java-8.0.33.jar  ← JDBC 驱动（已包含在项目中）
```

### 为什么不用 Maven/Gradle？

本项目是学习项目，为了：

1. **理解依赖管理**：手动下载 jar 让你知道依赖是什么
2. **简化环境**：不需要安装 Maven/Gradle
3. **专注核心**：重点学习 JDBC，而不是构建工具
4. **循序渐进**：先学基础，再学工具

**学习路径：**
```
原生 JDBC（本项目） → Maven/Gradle → Spring Boot
      ↑
    你在这里
```

### 依赖安装

如果 `mysql-connector-java-8.0.33.jar` 文件丢失：

```bash
# 方式1：运行安装脚本
./install.sh

# 方式2：手动下载
curl -L -o mysql-connector-java-8.0.33.jar \
  https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
```

---

## 📚 学习路线

按照以下顺序学习，每个 Step 都可以独立运行：

| Step | 内容 | 学习重点 | 状态 |
|------|------|---------|------|
| Step 0 | 项目结构搭建 | 项目组织、配置管理 | ✅ 已完成 |
| Step 1 | 数据库搭建 | 表设计、主键外键、索引 | ✅ 已完成 |
| Step 2 | 实体类 | 类的封装、getter/setter、BigDecimal | ✅ 已完成 |
| Step 3 | 用户管理 | JDBC、PreparedStatement、CRUD | ✅ 已完成 |
| Step 4 | 账户管理 | 事务处理、commit/rollback、充值流水 | ✅ 已完成 |
| Step 5 | 发钱功能 | 复杂事务、业务逻辑、异常处理 | ⏳ 进行中 |
| Step 6 | 统计功能 | JOIN、GROUP BY、聚合函数 | ⏳ 待完成 |
| Step 7 | 完整系统 | 菜单系统、整合所有功能 | ⏳ 待完成 |

**当前进度：** 66% 完成 🎯

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

会显示菜单，选择要运行的程序：

```
========================================
  发钱管理系统 - 快速运行
========================================

请选择要运行的程序：

  1) 测试数据库连接 (DBUtil)
  2) Step1: 数据库搭建
  3) Step2: 实体类测试
  4) Step3: 用户管理测试
  5) Step4: 账户管理测试
  6) Step5: 发钱功能测试
  7) Step6: 统计功能测试
  8) Main: 完整系统
  9) 编译所有文件
  0) 清理编译文件

请输入选项 (0-9):
```

### 手动编译运行

```bash
# 编译（包含 MySQL 驱动）
javac -cp "mysql-connector-java-8.0.33.jar" -d . -encoding UTF-8 \
  src/moneyTransfer/**/*.java src/moneyTransfer/*.java

# 运行（例如 Step3）
java -cp ".:mysql-connector-java-8.0.33.jar" moneyTransfer.Step3_UserDaoTest
```

**注意：**
- macOS/Linux 用 `:`分隔 classpath
- Windows 用 `;`分隔 classpath

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

### 克隆项目后需要做什么？

```bash
# 1. 安装依赖（检查 MySQL 驱动）
./install.sh

# 2. 配置数据库密码
vim config/db.properties

# 3. 初始化数据库
mysql -u root -p < sql/init.sql

# 4. 测试运行
./run.sh
```

### 编译报错：找不到 mysql.cj.jdbc.Driver？

**原因**：缺少 MySQL JDBC 驱动

**解决方法：**
```bash
# 检查驱动文件是否存在
ls -lh mysql-connector-java-8.0.33.jar

# 如果不存在，运行安装脚本
./install.sh

# 或手动下载
curl -L -o mysql-connector-java-8.0.33.jar \
  https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
```

### 运行报错：No suitable driver found？

**原因**：编译或运行时没有包含 MySQL 驱动

**解决方法：**
```bash
# 使用 run.sh（已经包含驱动）
./run.sh

# 或手动指定 classpath
java -cp ".:mysql-connector-java-8.0.33.jar" moneyTransfer.Step3_UserDaoTest
```

### 编译报错：找不到类？

**原因**：不在项目根目录

**解决方法：**
```bash
# 确保在项目根目录
cd /path/to/fundFlow-manager
pwd  # 应该显示 .../fundFlow-manager

# 然后运行
./run.sh
```

### 连接数据库失败？

**排查步骤：**

```bash
# 1. 检查 MySQL 是否启动
ps aux | grep mysql

# 2. 检查密码是否正确
mysql -u root -p

# 3. 检查数据库是否存在
mysql -u root -p -e "SHOW DATABASES LIKE 'money_transfer_db';"

# 4. 检查配置文件
cat config/db.properties
```

### 中文显示乱码？

**解决方法：**
```bash
# 编译时指定编码
javac -encoding UTF-8 ...

# 或使用 run.sh（已经包含）
./run.sh
```

### IDEA 无法跳转到源码？

**解决方法：**

查看 `IDEA_SETUP.md` 文件，按照步骤配置：
1. 标记 `src` 为 Sources Root
2. 添加 MySQL 驱动到 Libraries

### 如何清理编译文件？

```bash
# 使用脚本
./run.sh
# 选择 0) 清理编译文件

# 或手动删除
rm -rf moneyTransfer/
```

---

## 📚 学习资源

### 项目文档

- **README.md**（本文件）：项目总览和快速开始
- **LEARNING_GUIDE.md**：详细学习指南（Step 1 详解）
- **JDBC_CHEATSHEET.md**：JDBC 常用方法速查表
- **TRANSACTION_GUIDE.md**：数据库事务详解（ACID、commit/rollback）
- **IDEA_SETUP.md**：IntelliJ IDEA 配置指南

### SQL 脚本

- **sql/init.sql**：数据库初始化脚本（包含详细注释）

### 代码注释

每个源文件都包含：
- 📝 详细的类和方法注释
- 💡 代码原理说明
- 📚 使用示例
- ⚠️ 注意事项

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
