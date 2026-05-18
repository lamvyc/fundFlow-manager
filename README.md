# 💰 发钱管理系统

一个用于学习 Java + JDBC + MySQL 的渐进式项目

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-blue.svg)](https://www.mysql.com/)

---

## 📋 目录

- [环境要求](#-环境要求)
- [快速开始](#-快速开始)
- [克隆后检查清单](#-克隆后检查清单)
- [项目结构](#-项目结构)
- [学习路线](#-学习路线)
- [项目功能](#-项目功能)
- [依赖说明](#-依赖说明)
- [使用方法](#-使用方法)
- [常见问题](#-常见问题)
- [学习资源](#-学习资源)

---

## 💻 环境要求

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| **JDK** | 8 或更高 | Java 开发工具包 |
| **MySQL** | 8.0 或更高 | 数据库服务器 |
| **Git** | 任意版本 | 版本控制（可选） |

```bash
# 检查 Java 版本
java -version

# 检查 MySQL 版本
mysql --version

# 检查 MySQL 是否运行（macOS/Linux）
ps aux | grep mysql
```

---

## 🚀 快速开始

### 最简步骤（4 步）

```bash
# 1. 克隆项目
git clone <your-repo-url>
cd fundFlow-manager

# 2. 安装/检查依赖（MySQL 驱动）
./install.sh

# 3. 配置数据库密码 & 初始化
vim config/db.properties      # 修改 db.password
mysql -u root -p < sql/init.sql

# 4. 启动系统
./run.sh
```

### 配置数据库

编辑 `config/db.properties`：

```properties
db.url=jdbc:mysql://localhost:3306/money_transfer_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true
db.username=root
db.password=你的MySQL密码
db.driver=com.mysql.cj.jdbc.Driver
```

> ⚠️ `allowPublicKeyRetrieval=true` 在 MySQL 8.0 使用 caching_sha2_password 认证时必须添加。

### 验证安装

```bash
./run.sh
# 选择 1) 测试数据库连接
# 看到 "✓ 数据库连接成功！" 即配置正确
```

---

## ✅ 克隆后检查清单

**环境检查**
- [ ] JDK 8+ 已安装（`java -version`）
- [ ] MySQL 8.0+ 已安装（`mysql --version`）
- [ ] MySQL 服务已启动（`ps aux | grep mysql`）

**依赖检查**
- [ ] MySQL JDBC 驱动存在（`ls mysql-connector-java-8.0.33.jar`）
- [ ] 如果不存在，运行 `./install.sh`

**配置检查**
- [ ] 修改 `config/db.properties` 中的密码
- [ ] 确认 URL 包含 `allowPublicKeyRetrieval=true`

**数据库初始化**
- [ ] 执行 `mysql -u root -p < sql/init.sql`
- [ ] 验证表是否创建（`SHOW TABLES;`）
- [ ] 验证测试数据（`SELECT * FROM users;`）

**测试运行**
- [ ] 运行 `./run.sh` → 选择 1) → 看到 "✓ 数据库连接成功！"

**IDEA 配置（可选）**
- [ ] 标记 `src` 为 Sources Root
- [ ] 添加 MySQL 驱动到 Libraries（参考 `docs/IDEA_SETUP.md`）

---

## 📁 项目结构

```
fundFlow-manager/
├── config/
│   └── db.properties                    # 数据库配置（需修改密码）
│
├── docs/                                # 📚 项目文档
│   ├── LEARNING_GUIDE.md               # 学习指南（各 Step 知识点）
│   ├── JDBC_CHEATSHEET.md              # JDBC 常用方法速查表
│   ├── TRANSACTION_GUIDE.md            # 数据库事务详解
│   ├── IDEA_SETUP.md                   # IntelliJ IDEA 配置指南
│   ├── PROJECT_OVERVIEW.md             # 项目总览和统计
│   └── README_UPDATE_NOTES.md          # 更新说明
│
├── sql/
│   └── init.sql                        # 数据库初始化脚本
│
├── src/moneyTransfer/
│   ├── entity/                         # 实体类（对应数据库表）
│   │   ├── User.java                  ✅
│   │   ├── Account.java               ✅
│   │   ├── PaymentRecord.java         ✅
│   │   └── RechargeRecord.java        ✅
│   │
│   ├── dao/                            # 数据访问层
│   │   ├── UserDao.java               ✅ 用户 CRUD
│   │   ├── AccountDao.java            ✅ 账户管理 + 充值事务
│   │   ├── PaymentRecordDao.java      ✅ 发钱记录查询
│   │   └── StatisticsDao.java         ✅ JOIN 统计查询
│   │
│   ├── service/                        # 业务逻辑层
│   │   ├── PaymentService.java        ✅ 发钱（复杂事务）
│   │   └── StatisticsService.java     ✅ 统计报表格式化
│   │
│   ├── exception/                      # 自定义异常
│   │   ├── InsufficientBalanceException.java  ✅
│   │   ├── AccountFrozenException.java        ✅
│   │   └── InvalidAmountException.java        ✅
│   │
│   ├── util/                           # 工具类
│   │   ├── DBUtil.java                ✅ 数据库连接
│   │   └── PrintUtil.java             ✅ 控制台美化输出
│   │
│   ├── Step1_DatabaseSetup.java       ✅ 数据库搭建
│   ├── Step2_EntityTest.java          ✅ 实体类测试
│   ├── Step3_UserDaoTest.java         ✅ 用户管理测试
│   ├── Step4_AccountDaoTest.java      ✅ 账户管理测试
│   ├── Step5_PaymentServiceTest.java  ✅ 发钱功能测试
│   ├── Step6_StatisticsTest.java      ✅ 统计功能测试
│   ├── Step7_SystemTest.java          ✅ 完整系统集成测试
│   └── Main.java                      ✅ 交互式命令行主程序
│
├── run.sh                              # 快速运行脚本
├── install.sh                          # 依赖安装脚本
├── check_db.sh                         # 数据库检查脚本
├── mysql-connector-java-8.0.33.jar     # MySQL JDBC 驱动（已包含）
├── .gitignore
└── README.md
```

---

## 📚 学习路线

| Step | 内容 | 学习重点 | 状态 |
|------|------|---------|------|
| Step 0 | 项目结构搭建 | 项目组织、配置管理 | ✅ |
| Step 1 | 数据库搭建 | 表设计、主键外键、索引 | ✅ |
| Step 2 | 实体类 | 类封装、getter/setter、BigDecimal | ✅ |
| Step 3 | 用户管理 | JDBC、PreparedStatement、CRUD | ✅ |
| Step 4 | 账户管理 | 事务处理、commit/rollback | ✅ |
| Step 5 | 发钱功能 | 复杂事务、Service 层、自定义异常 | ✅ |
| Step 6 | 统计功能 | JOIN、GROUP BY、聚合函数、子查询 | ✅ |
| Step 7 | 完整系统 | 三层架构整合、交互式 UI | ✅ |

**当前进度：100% 完成** ✅

---

## 🎯 项目功能

### 1. 用户管理
- 添加收款人（姓名、手机号、银行卡）
- 查询用户（按 ID、手机号）
- 更新用户信息
- 删除用户（软删除）

### 2. 账户管理
- 创建发钱账户
- 充值（事务保证，记录充值流水）
- 账户冻结 / 解冻

### 3. 发钱功能（核心）
- 发钱（扣款 + 创建记录，原子事务）
- 余额不足 / 账户冻结等业务异常处理
- 查询发钱记录

### 4. 统计报表
- 账户发钱汇总（LEFT JOIN + 聚合）
- 用户收款汇总（INNER JOIN + GROUP BY）
- 发钱明细（三表 JOIN）
- TOP N 收款用户（ORDER BY + LIMIT）
- HAVING 过滤演示
- 系统整体概览（子查询）

---

## 📦 依赖说明

本项目**只有 1 个外部依赖**：

| 依赖 | 版本 | 大小 | 说明 |
|------|------|------|------|
| mysql-connector-java | 8.0.33 | 2.4MB | MySQL JDBC 驱动，已包含在项目中 |

驱动文件已随 Git 提交，克隆后**无需额外下载**。如果文件丢失：

```bash
# 方式1：安装脚本自动下载
./install.sh

# 方式2：手动下载
curl -L -o mysql-connector-java-8.0.33.jar \
  https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
```

### 为什么不用 Maven/Gradle？

本项目是**学习项目**，目的是专注 JDBC 核心，理解依赖本质。

```
原生 JDBC（本项目）→ Maven/Gradle → Spring Boot
        ↑
      你在这里
```

---

## 🛠️ 使用方法

### 推荐：使用运行脚本

```bash
./run.sh
```

菜单选项：

```
  1) 测试数据库连接
  2) Step1: 数据库搭建
  3) Step2: 实体类测试
  4) Step3: 用户管理测试
  5) Step4: 账户管理测试
  6) Step5: 发钱功能测试
  7) Step6: 统计功能测试
  8) Step7: 完整系统集成测试
  9) Main: 交互式完整系统
  c) 编译所有文件
  0) 清理编译文件
```

### 手动编译运行

```bash
# 编译（包含 MySQL 驱动）
javac -cp "mysql-connector-java-8.0.33.jar" -d . -encoding UTF-8 \
  src/moneyTransfer/**/*.java src/moneyTransfer/*.java

# 运行（例如交互式主程序）
java -cp ".:mysql-connector-java-8.0.33.jar" moneyTransfer.Main
```

> macOS/Linux classpath 分隔符用 `:`，Windows 用 `;`

---

## ❓ 常见问题

### 连接失败：Public Key Retrieval is not allowed

在 `config/db.properties` 的 URL 末尾加上 `&allowPublicKeyRetrieval=true`。

### 编译报错：找不到 com.mysql.cj.jdbc.Driver

缺少 MySQL JDBC 驱动，运行 `./install.sh` 自动下载。

### 运行报错：No suitable driver found

编译或运行时未包含驱动，使用 `./run.sh` 或手动指定：
```bash
java -cp ".:mysql-connector-java-8.0.33.jar" moneyTransfer.Main
```

### 连接数据库失败

```bash
ps aux | grep mysql          # 1. 确认 MySQL 已启动
mysql -u root -p             # 2. 确认密码正确
cat config/db.properties     # 3. 确认配置文件
```

### 编译报错：找不到类

确保在项目根目录执行 `./run.sh`，而不是在 src 目录内。

### 中文乱码

编译时加 `-encoding UTF-8`（`run.sh` 已包含，手动编译时需手动加）。

### IDEA 无法跳转源码

查看 `docs/IDEA_SETUP.md`：标记 `src` 为 Sources Root，并添加 JDBC 驱动到 Libraries。

### 清理编译产物

```bash
./run.sh  # 选择 0) 清理编译文件
# 或
rm -rf moneyTransfer/
```

---

## 📖 学习资源

| 文档 | 内容 | 何时查看 |
|------|------|---------|
| **README.md**（本文件） | 项目总览、快速开始 | 克隆项目后第一个看 |
| **docs/LEARNING_GUIDE.md** | 各 Step 详细知识点总结 | 每个 Step 开始前 |
| **docs/JDBC_CHEATSHEET.md** | JDBC 常用方法速查 | 写代码时随时查阅 |
| **docs/TRANSACTION_GUIDE.md** | 事务详解（ACID）| 学习 Step 4/5 时必读 |
| **docs/IDEA_SETUP.md** | IDEA 配置指南 | 使用 IDEA 时查看 |
| **docs/PROJECT_OVERVIEW.md** | 项目全貌、架构图、统计 | 了解整体结构时 |

---

## 🎉 完成本项目后你将掌握

1. 数据库设计（主键、外键、索引）
2. JDBC 操作（CRUD、PreparedStatement）
3. 事务处理（ACID、commit/rollback、悲观锁）
4. SQL 统计（JOIN、GROUP BY、HAVING、聚合函数、子查询）
5. 代码分层（Entity、DAO、Service）
6. 自定义异常处理
7. 完整系统整合（交互式命令行）

---

## 📞 获取帮助

遇到问题随时提问：
- 不理解某个概念
- 代码运行报错
- 不知道下一步怎么做

**开始学习：打开 `docs/LEARNING_GUIDE.md` 查看详细指导。**
