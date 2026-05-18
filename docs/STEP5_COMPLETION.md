# ✅ Step 5 完成报告 - 发钱功能（复杂事务）

## 📊 完成情况

**完成日期：** 2026-05-18  
**完成度：** 100% ✅

---

## 🎯 本节目标

实现发钱功能，这是本项目最核心的业务逻辑之一。涉及：
1. **复杂事务处理**：一次发钱操作需要更新两张表（账户表 + 发钱记录表）
2. **业务逻辑层设计**：创建 Service 层，封装业务逻辑
3. **自定义异常处理**：处理余额不足、账户冻结等业务异常
4. **事务原子性保证**：两步操作要么都成功，要么都失败

---

## 📁 新增文件列表

### 1. 自定义异常类（exception/）

#### `InsufficientBalanceException.java`
- **作用**：余额不足异常
- **触发条件**：账户余额小于发钱金额
- **位置**：src/moneyTransfer/exception/InsufficientBalanceException.java

#### `AccountFrozenException.java`
- **作用**：账户冻结异常
- **触发条件**：账户状态为 FROZEN
- **位置**：src/moneyTransfer/exception/AccountFrozenException.java

#### `InvalidAmountException.java`
- **作用**：无效金额异常
- **触发条件**：金额为负数或零
- **位置**：src/moneyTransfer/exception/InvalidAmountException.java

---

### 2. 数据访问层（dao/）

#### `PaymentRecordDao.java`
- **作用**：发钱记录的 CRUD 操作
- **核心方法**：
  - `createPaymentRecord()` - 创建发钱记录（在事务中调用）
  - `getPaymentRecordById()` - 根据 ID 查询
  - `getPaymentRecordsByAccountId()` - 查询账户的发钱记录
  - `getPaymentRecordsByUserId()` - 查询用户的收款记录
  - `getPaymentRecordsByStatus()` - 按状态查询
  - `updatePaymentStatus()` - 更新记录状态
- **位置**：src/moneyTransfer/dao/PaymentRecordDao.java
- **代码行数**：315 行

---

### 3. 业务逻辑层（service/）

#### `PaymentService.java` ⭐⭐⭐
- **作用**：发钱业务逻辑（本节最重要的类）
- **核心方法**：
  
  **`sendMoney()` - 发钱操作（核心方法）**
  
  流程：
  1. 参数校验（金额、账户、用户）
  2. 开启事务（`setAutoCommit(false)`）
  3. 查询账户信息（`FOR UPDATE` 加锁）
  4. 检查账户状态（是否冻结）
  5. 检查账户余额（是否足够）
  6. 扣减账户余额
  7. 创建发钱记录（状态：COMPLETED）
  8. 提交事务（`commit()`）
  9. 发生异常则回滚（`rollback()`）
  
  **`batchSendMoney()` - 批量发钱**
  - 给多个用户发钱
  - 每笔发钱独立事务
  
  **`cancelPayment()` - 取消发钱**
  - 仅支持 PENDING 状态的记录
  
- **位置**：src/moneyTransfer/service/PaymentService.java
- **代码行数**：232 行

---

### 4. 测试程序

#### `Step5_PaymentServiceTest.java`
- **作用**：测试发钱功能的所有场景
- **测试内容**：
  1. ✅ 正常发钱流程
  2. ✅ 批量发钱（给多个用户）
  3. ✅ 余额不足（测试事务回滚）
  4. ✅ 账户冻结（不能发钱）
  5. ✅ 无效金额（负数、零）
  6. ✅ 查询发钱记录
- **位置**：src/moneyTransfer/Step5_PaymentServiceTest.java
- **代码行数**：319 行

---

## 🔧 修改的文件

### 1. `PrintUtil.java`
- **修改内容**：新增 3 个方法
  - `printHeader()` - 打印测试程序头部
  - `printSection()` - 打印章节标题
  - `printFooter()` - 打印测试程序底部
- **目的**：美化测试输出，提升可读性

### 2. `db.properties`
- **修改内容**：在数据库 URL 中添加 `allowPublicKeyRetrieval=true`
- **原因**：解决 MySQL 8.0 的公钥认证问题
- **修改前**：
  ```properties
  db.url=jdbc:mysql://localhost:3306/money_transfer_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
  ```
- **修改后**：
  ```properties
  db.url=jdbc:mysql://localhost:3306/money_transfer_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true
  ```

### 3. `run.sh`
- **修改内容**：修复 Step5 选项的类名
- **修改前**：`moneyTransfer.Step5_PaymentTest`
- **修改后**：`moneyTransfer.Step5_PaymentServiceTest`

---

## 🎓 核心知识点总结

### 1. 复杂事务处理（多表操作）

发钱操作涉及两张表：
- **accounts 表**：扣减余额
- **payment_records 表**：创建发钱记录

**关键点：必须在同一个事务中完成！**

```java
// 开启事务
conn.setAutoCommit(false);

try {
    // 1. 扣减账户余额
    updateBalance(accountId, newBalance, conn);
    
    // 2. 创建发钱记录
    createPaymentRecord(record, conn);
    
    // 3. 提交事务
    conn.commit();
    
} catch (Exception e) {
    // 4. 回滚事务
    conn.rollback();
}
```

---

### 2. FOR UPDATE 悲观锁

**作用**：防止并发修改账户余额

```sql
SELECT balance FROM accounts WHERE id = ? FOR UPDATE
```

**原理**：
- 查询时锁定该行数据
- 其他事务必须等待当前事务完成
- 防止"脏读"和"并发扣款"问题

**场景举例**：
- 事务 A：查询余额 1000 元 → 发钱 500 元 → 更新余额为 500 元
- 事务 B：同时查询余额 1000 元 → 发钱 600 元 → 更新余额为 400 元
- **问题**：如果不加锁，最终余额可能是 400 元（实际应该是 -100 元，余额不足）
- **解决**：使用 `FOR UPDATE` 锁定，事务 B 必须等待事务 A 完成

---

### 3. 业务逻辑层设计（Service 层）

**为什么需要 Service 层？**

| 层级 | 职责 | 举例 |
|------|------|------|
| **Entity** | 数据封装 | `User`, `Account`, `PaymentRecord` |
| **DAO** | 数据访问 | `UserDao.getUserById()` |
| **Service** | 业务逻辑 | `PaymentService.sendMoney()` |

**Service 层的职责**：
- ✅ 参数校验（金额、账户、用户）
- ✅ 业务逻辑验证（余额、状态）
- ✅ 事务管理（开启、提交、回滚）
- ✅ 调用多个 DAO 方法
- ✅ 异常处理（业务异常 + 数据库异常）

**为什么不在 DAO 层做业务逻辑？**
- DAO 层只负责单表操作
- Service 层负责跨表操作和业务规则
- 符合"单一职责原则"

---

### 4. 自定义异常处理

**为什么需要自定义异常？**

- ✅ 区分业务异常和数据库异常
- ✅ 提供更清晰的错误信息
- ✅ 便于上层调用者处理

**异常分类**：

```java
// 业务异常（继承自 Exception）
InsufficientBalanceException  // 余额不足
AccountFrozenException        // 账户冻结
InvalidAmountException        // 无效金额

// 数据库异常（SQLException）
"账户不存在"
"用户不存在"
"更新余额失败"
```

**异常处理策略**：

```java
try {
    sendMoney(...);
} catch (InsufficientBalanceException e) {
    // 业务异常：提示用户
    System.out.println("余额不足，请充值");
} catch (SQLException e) {
    // 数据库异常：记录日志，系统维护
    System.err.println("系统异常，请联系管理员");
}
```

---

### 5. 事务的 ACID 特性

#### A - Atomicity（原子性）
- 发钱 = 扣款 + 创建记录
- 要么都成功，要么都失败
- 通过 `commit()` 和 `rollback()` 保证

#### C - Consistency（一致性）
- 扣款金额 = 发钱记录金额
- 账户余额 >= 0
- 通过业务逻辑验证保证

#### I - Isolation（隔离性）
- 并发发钱不会互相干扰
- 通过 `FOR UPDATE` 锁定保证

#### D - Durability（持久性）
- 事务提交后，数据永久保存
- 数据库自动保证

---

## 🧪 测试结果

### 测试 1：正常发钱流程 ✅
- **操作**：账户 1 给用户 1 发钱 1000 元
- **结果**：
  - ✅ 账户余额：120000 → 119000
  - ✅ 创建发钱记录 ID=1，状态=COMPLETED

### 测试 2：批量发钱 ✅
- **操作**：账户 1 给用户 2、3 各发钱 500 元
- **结果**：
  - ✅ 给用户 2 发钱成功：余额 119000 → 118500
  - ❌ 给用户 3 发钱失败：用户已被删除（符合预期）

### 测试 3：余额不足（事务回滚）✅
- **操作**：尝试发钱 999999 元（超过余额 118500）
- **结果**：
  - ✅ 抛出 `InsufficientBalanceException` 异常
  - ✅ 余额未变化：118500（事务已回滚）

### 测试 4：账户冻结 ✅
- **操作**：冻结账户 2，尝试发钱
- **结果**：
  - ✅ 抛出 `AccountFrozenException` 异常
  - ✅ 发钱失败

### 测试 5：无效金额 ✅
- **操作 1**：尝试发钱 -100 元
- **结果**：✅ 抛出 `InvalidAmountException` 异常
- **操作 2**：尝试发钱 0 元
- **结果**：✅ 抛出 `InvalidAmountException` 异常

### 测试 6：查询发钱记录 ✅
- **账户 1 的发钱记录**：2 条（给用户 1 和用户 2）
- **用户 1 的收款记录**：1 条（来自账户 1）
- **所有已完成记录**：2 条

---

## 📊 代码统计

| 类型 | 文件数 | 代码行数 |
|------|--------|----------|
| 实体类 | 0 个（复用） | 0 行 |
| DAO 类 | 1 个 | 315 行 |
| Service 类 | 1 个 | 232 行 |
| 异常类 | 3 个 | 45 行 |
| 测试程序 | 1 个 | 319 行 |
| **总计** | **6 个** | **911 行** |

---

## 🎯 项目整体进度

| 模块 | 完成度 | 状态 |
|------|--------|------|
| 数据库设计 | 100% | ✅ |
| 实体类 | 100% | ✅ |
| DAO 层 | 100% | ✅ |
| Service 层 | 50% | ⏳ (PaymentService 完成) |
| 测试程序 | 71% (5/7) | ⏳ |
| **总体** | **75%** | **✅** |

---

## 📚 下一步：Step 6 - 统计功能

### 即将学习的内容

1. **JOIN 查询**
   - 多表关联查询
   - INNER JOIN, LEFT JOIN
   
2. **聚合函数**
   - SUM(), COUNT(), AVG()
   - GROUP BY, HAVING
   
3. **复杂查询**
   - 子查询
   - 分组统计
   
4. **报表生成**
   - 账户发钱统计
   - 用户收款统计
   - 时间范围查询

---

## 🎓 学习建议

1. **重点理解事务处理**
   - 多看几遍 `PaymentService.sendMoney()` 方法
   - 理解 `setAutoCommit(false)`, `commit()`, `rollback()` 的作用
   
2. **掌握 FOR UPDATE 锁**
   - 理解为什么需要锁定
   - 什么情况下使用悲观锁
   
3. **学习 Service 层设计**
   - Service 层的职责
   - 与 DAO 层的区别
   - 如何处理业务逻辑
   
4. **练习异常处理**
   - 如何设计自定义异常
   - 业务异常 vs 系统异常
   - 异常的传递和处理

---

## 🔗 相关文档

- 📖 **学习指南**：`docs/LEARNING_GUIDE.md`
- 📖 **事务详解**：`docs/TRANSACTION_GUIDE.md`
- 📖 **JDBC 速查表**：`docs/JDBC_CHEATSHEET.md`

---

## ✅ 完成标志

- ✅ 所有代码编译通过
- ✅ 所有测试用例通过
- ✅ 事务回滚验证成功
- ✅ 异常处理验证成功
- ✅ 文档更新完成

**Step 5 圆满完成！🎉**

---

## 🚀 运行方法

### 方式 1：使用 run.sh 脚本
```bash
./run.sh
# 选择选项 6: Step5: 发钱功能测试
```

### 方式 2：手动编译运行
```bash
# 编译
javac -encoding UTF-8 -cp .:mysql-connector-java-8.0.33.jar -d . \
  src/moneyTransfer/exception/*.java \
  src/moneyTransfer/entity/*.java \
  src/moneyTransfer/util/*.java \
  src/moneyTransfer/dao/*.java \
  src/moneyTransfer/service/*.java \
  src/moneyTransfer/Step5_PaymentServiceTest.java

# 运行
java -cp .:mysql-connector-java-8.0.33.jar moneyTransfer.Step5_PaymentServiceTest
```

---

**祝贺你完成了 Step 5！继续加油！💪**
