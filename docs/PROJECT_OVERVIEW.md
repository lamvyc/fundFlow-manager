# 项目总览

## 📊 项目统计

- **代码行数**：~5000 行
- **Java 文件数量**：25 个
- **完成度**：100% ✅
- **难度等级**：⭐⭐⭐（中级）

---

## 🗂️ 完整文件列表

### 配置文件
```
config/
└── db.properties          # 数据库配置（用户名/密码/URL）
```

### SQL 脚本
```
sql/
└── init.sql               # 数据库初始化（创建 4 张表 + 测试数据）
```

### 源代码（src/moneyTransfer/）

#### 实体类（entity/）
```
entity/
├── User.java              # 用户实体（对应 users 表）
├── Account.java           # 账户实体（对应 accounts 表）
├── PaymentRecord.java     # 发钱记录实体（对应 payment_records 表）
└── RechargeRecord.java    # 充值记录实体（对应 recharge_records 表）
```

#### 数据访问层（dao/）
```
dao/
├── UserDao.java           # 用户 DAO（CRUD）
├── AccountDao.java        # 账户 DAO（含充值事务）
├── PaymentRecordDao.java  # 发钱记录 DAO
└── StatisticsDao.java     # 统计查询 DAO（JOIN + 聚合）
```

#### 业务逻辑层（service/）
```
service/
├── PaymentService.java    # 发钱业务逻辑（复杂事务）
└── StatisticsService.java # 统计报表格式化输出
```

#### 工具类（util/）
```
util/
├── DBUtil.java            # 数据库连接工具
└── PrintUtil.java         # 控制台美化输出工具
```

#### 自定义异常（exception/）
```
exception/
├── InsufficientBalanceException.java  # 余额不足异常
├── AccountFrozenException.java        # 账户冻结异常
└── InvalidAmountException.java        # 无效金额异常
```

#### 测试 & 主程序
```
Step1_DatabaseSetup.java      # 数据库搭建 ✅
Step2_EntityTest.java         # 实体类测试 ✅
Step3_UserDaoTest.java        # 用户管理测试 ✅
Step4_AccountDaoTest.java     # 账户管理测试 ✅
Step5_PaymentServiceTest.java # 发钱功能测试 ✅
Step6_StatisticsTest.java     # 统计功能测试 ✅
Step7_SystemTest.java         # 完整系统集成测试 ✅
Main.java                     # 交互式命令行主程序 ✅
```

### 文档
```
docs/
├── LEARNING_GUIDE.md      # 学习指南（各 Step 知识点总结）
├── PROJECT_OVERVIEW.md    # 项目总览（本文件）
├── JDBC_CHEATSHEET.md     # JDBC 速查表
├── TRANSACTION_GUIDE.md   # 事务详解
├── IDEA_SETUP.md          # IDEA 配置指南
└── README_UPDATE_NOTES.md # 更新说明
README.md                  # 项目入口说明
```

### 脚本 & 依赖
```
run.sh                            # 快速运行脚本
install.sh                        # 依赖安装脚本
check_db.sh                       # 数据库检查脚本
mysql-connector-java-8.0.33.jar   # MySQL JDBC 驱动
```

---

## 完整系统架构

```
┌─────────────────────────────────────────────┐
│  Main.java（交互式命令行界面）               │
│  - 用户管理菜单                             │
│  - 账户管理菜单                             │
│  - 发钱操作菜单                             │
│  - 统计报表菜单                             │
├─────────────────────────────────────────────┤
│  Service 层（业务逻辑）                      │
│  PaymentService    → 发钱（事务保证原子性）  │
│  StatisticsService → 统计报表格式化          │
├─────────────────────────────────────────────┤
│  DAO 层（数据访问）                          │
│  UserDao           → 用户 CRUD              │
│  AccountDao        → 账户管理 + 充值事务     │
│  PaymentRecordDao  → 发钱记录查询            │
│  StatisticsDao     → JOIN 统计查询           │
├─────────────────────────────────────────────┤
│  Entity 层（数据模型）                       │
│  User / Account / PaymentRecord / RechargeRecord │
├─────────────────────────────────────────────┤
│  Exception 层（自定义异常）                  │
│  InsufficientBalanceException               │
│  AccountFrozenException                     │
│  InvalidAmountException                     │
├─────────────────────────────────────────────┤
│  Util 层（工具类）                           │
│  DBUtil → 数据库连接   PrintUtil → 输出美化  │
├─────────────────────────────────────────────┤
│  MySQL（money_transfer_db）                 │
│  users / accounts                           │
│  payment_records / recharge_records         │
└─────────────────────────────────────────────┘
```

---

## 已掌握的知识点

| 知识点 | 难度 | 在哪个 Step 学的 |
|--------|------|-----------------|
| 数据库设计（主键、外键、索引） | ⭐⭐⭐ | Step 1 |
| 实体类封装（getter/setter、BigDecimal） | ⭐⭐ | Step 2 |
| JDBC 基础（Connection、PreparedStatement） | ⭐⭐⭐ | Step 3 |
| ResultSet 结果集处理 | ⭐⭐⭐ | Step 3 |
| DAO 设计模式 | ⭐⭐⭐ | Step 3 |
| 数据库事务（commit / rollback） | ⭐⭐⭐⭐ | Step 4 |
| SELECT ... FOR UPDATE 悲观锁 | ⭐⭐⭐⭐ | Step 4 |
| 复杂事务处理（多表操作） | ⭐⭐⭐⭐⭐ | Step 5 |
| Service 层业务逻辑设计 | ⭐⭐⭐⭐ | Step 5 |
| 自定义异常分层处理 | ⭐⭐⭐ | Step 5 |
| INNER JOIN / LEFT JOIN | ⭐⭐⭐⭐ | Step 6 |
| GROUP BY + HAVING | ⭐⭐⭐⭐ | Step 6 |
| 聚合函数 SUM/COUNT/AVG/MAX/MIN | ⭐⭐⭐ | Step 6 |
| 子查询 | ⭐⭐⭐⭐ | Step 6 |
| 完整系统整合 + 交互式 UI | ⭐⭐⭐ | Step 7 |

---

## 代码统计

| 模块 | 文件数 | 代码行数 | 完成度 |
|------|--------|---------|--------|
| 实体类 | 4 | ~600 行 | ✅ 100% |
| DAO 层 | 4 | ~1100 行 | ✅ 100% |
| Service 层 | 2 | ~480 行 | ✅ 100% |
| 工具类 | 2 | ~230 行 | ✅ 100% |
| 异常类 | 3 | ~50 行 | ✅ 100% |
| 测试程序 | 7 | ~1800 行 | ✅ 100% |
| 主程序 | 1 | ~350 行 | ✅ 100% |
| **总计** | **23** | **~4600 行** | **100%** |

---

## 运行方式

```bash
# 使用菜单式运行脚本（推荐）
./run.sh

# 直接运行交互式主程序
java -cp .:mysql-connector-java-8.0.33.jar moneyTransfer.Main

# 直接运行集成测试
java -cp .:mysql-connector-java-8.0.33.jar moneyTransfer.Step7_SystemTest
```

---

## 后续学习路径

完成本项目后，建议按以下路径继续学习：

### 阶段 2：工程化基础
- **Maven/Gradle** - 依赖管理，告别手动管理 jar 文件
- **JUnit** - 单元测试框架，自动化测试
- **Log4j/SLF4J** - 日志框架，替代 System.out.println

### 阶段 3：ORM 框架
- **MyBatis** - 半自动 ORM，SQL 与 Java 分离
- **Spring JDBC** - 简化 JDBC 模板代码
- **Hibernate/JPA** - 全自动 ORM

### 阶段 4：Spring 生态
- **Spring Core** - IoC 和 AOP
- **Spring Boot** - 快速开发，自动配置
- **Spring MVC** - Web 层 RESTful API

### 阶段 5：微服务 & 中间件
- **Redis** - 缓存
- **RabbitMQ/Kafka** - 消息队列
- **Docker** - 容器化部署
- **Spring Cloud** - 微服务框架

---

**项目已 100% 完成！** 这是你 Java 后端开发之路的重要起点。
