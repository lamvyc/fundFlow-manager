# 数据库事务详解

## 🎯 什么是事务？

**事务（Transaction）** = 一组必须全部成功或全部失败的数据库操作

### 生活中的例子：银行转账

```
张三向李四转账 1000 元：
  步骤1：张三账户 -1000 ✓
  步骤2：李四账户 +1000 ✗ （失败了！）

结果：
  ❌ 如果没有事务：张三的钱没了，李四也没收到（钱丢了）
  ✅ 如果有事务：两步必须都成功，否则全部撤销（保证一致性）
```

---

## 📚 ACID 特性

事务必须满足 ACID 四大特性：

### A - Atomicity（原子性）

**要么全做，要么全不做**

```java
// 例子：充值操作
开始事务
  步骤1：扣款          ✓
  步骤2：记录流水      ✓  → 全部成功，提交事务
  
开始事务
  步骤1：扣款          ✓
  步骤2：记录流水      ✗  → 有步骤失败，回滚事务（步骤1也撤销）
```

### C - Consistency（一致性）

**数据从一个正确状态到另一个正确状态**

```java
// 例子：余额不能为负数
充值前：余额 = 1000（正确状态）
充值后：余额 = 1000 + 500 = 1500（仍然正确）

发钱时：余额 = 100，要发 200
  ❌ 不允许：余额变成 -100（错误状态）
  ✅ 正确：拒绝操作，余额仍是 100
```

### I - Isolation（隔离性）

**多个事务并发执行时，互不干扰**

```java
// 例子：两个人同时充值
事务A：查询余额 1000 → 充值 500 → 更新为 1500
事务B：查询余额 1000 → 充值 300 → 更新为 1300

❌ 没有隔离：最终余额可能是 1300（丢失了事务A的 500）
✅ 有隔离：最终余额是 1800（两次充值都生效）
```

### D - Durability（持久性）

**事务提交后，数据永久保存**

```java
// 例子：充值成功后断电
充值 1000 元 → commit() 提交 → 断电重启
  ✅ 数据仍在：余额已更新，充值记录已保存
```

---

## 💻 Java 中的事务操作

### 基本操作

```java
Connection conn = DBUtil.getConnection();

try {
    // 1. 开启事务（关闭自动提交）
    conn.setAutoCommit(false);
    
    // 2. 执行多个 SQL 操作
    // ... SQL 1
    // ... SQL 2
    // ... SQL 3
    
    // 3. 提交事务（所有操作生效）
    conn.commit();
    
} catch (Exception e) {
    // 4. 回滚事务（撤销所有操作）
    conn.rollback();
} finally {
    // 5. 恢复自动提交
    conn.setAutoCommit(true);
    conn.close();
}
```

### 完整示例：充值操作

```java
public boolean recharge(int accountId, BigDecimal amount) {
    Connection conn = null;
    
    try {
        // 1. 获取连接
        conn = DBUtil.getConnection();
        
        // 2. 开启事务
        conn.setAutoCommit(false);
        
        // 3. 查询当前余额（加锁）
        PreparedStatement pstmt = conn.prepareStatement(
            "SELECT balance FROM accounts WHERE id = ? FOR UPDATE"
        );
        pstmt.setInt(1, accountId);
        ResultSet rs = pstmt.executeQuery();
        
        if (!rs.next()) {
            conn.rollback();  // 账户不存在，回滚
            return false;
        }
        
        BigDecimal currentBalance = rs.getBigDecimal("balance");
        
        // 4. 更新余额
        PreparedStatement updateStmt = conn.prepareStatement(
            "UPDATE accounts SET balance = ? WHERE id = ?"
        );
        updateStmt.setBigDecimal(1, currentBalance.add(amount));
        updateStmt.setInt(2, accountId);
        updateStmt.executeUpdate();
        
        // 5. 插入充值记录
        PreparedStatement insertStmt = conn.prepareStatement(
            "INSERT INTO recharge_records (account_id, amount, ...) VALUES (?, ?, ...)"
        );
        insertStmt.setInt(1, accountId);
        insertStmt.setBigDecimal(2, amount);
        // ...
        insertStmt.executeUpdate();
        
        // 6. 提交事务
        conn.commit();
        return true;
        
    } catch (SQLException e) {
        // 7. 发生异常，回滚事务
        if (conn != null) {
            conn.rollback();
        }
        return false;
        
    } finally {
        // 8. 恢复自动提交，关闭连接
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
```

---

## 🔒 悲观锁：SELECT ... FOR UPDATE

### 为什么需要加锁？

```java
// 场景：两个线程同时充值到同一个账户

线程A：                          线程B：
查询余额 1000                    查询余额 1000
充值 500，余额变成 1500          充值 300，余额变成 1300
更新数据库：1500                 更新数据库：1300

结果：❌ 最终余额是 1300（线程A的充值丢失了！）
```

### 使用 FOR UPDATE 解决

```java
// 线程A 先执行
SELECT balance FROM accounts WHERE id = 1 FOR UPDATE;
// ↑ 这行会锁定这条记录，其他事务必须等待

线程A：                          线程B：
查询余额 1000（加锁）            查询余额（等待...）
充值 500，余额变成 1500          （等待...）
更新数据库：1500                 （等待...）
提交事务（释放锁）               （等待...）
                                查询余额 1500（获得锁）
                                充值 300，余额变成 1800
                                更新数据库：1800
                                提交事务

结果：✅ 最终余额是 1800（两次充值都生效！）
```

### FOR UPDATE 的注意事项

```java
// ✅ 正确用法：在事务中使用
conn.setAutoCommit(false);  // 开启事务
SELECT ... FOR UPDATE;       // 加锁
// ... 执行操作
conn.commit();              // 提交后释放锁

// ❌ 错误用法：不在事务中使用
conn.setAutoCommit(true);   // 自动提交（默认）
SELECT ... FOR UPDATE;       // 查询完立即释放锁，没用！
```

---

## 🎓 事务隔离级别

MySQL 支持 4 种隔离级别（从低到高）：

| 隔离级别 | 脏读 | 不可重复读 | 幻读 |
|---------|------|-----------|------|
| READ UNCOMMITTED（读未提交） | ✗ | ✗ | ✗ |
| READ COMMITTED（读已提交） | ✓ | ✗ | ✗ |
| REPEATABLE READ（可重复读，MySQL默认） | ✓ | ✓ | ✓ |
| SERIALIZABLE（串行化） | ✓ | ✓ | ✓ |

### 脏读（Dirty Read）

```
事务A：更新余额为 1500（未提交）
事务B：读取余额 1500          ← 读到了未提交的数据
事务A：回滚（余额恢复为 1000）
事务B：基于 1500 做计算       ← 数据是错的！
```

### 不可重复读（Non-repeatable Read）

```
事务A：第一次读取余额 1000
事务B：更新余额为 1500，提交
事务A：第二次读取余额 1500    ← 两次读取结果不一样
```

### 幻读（Phantom Read）

```
事务A：查询余额 > 1000 的账户，找到 5 个
事务B：插入一个余额 2000 的账户，提交
事务A：再次查询，找到 6 个    ← 凭空多出了一条记录
```

---

## 🛠️ 常见问题

### Q1: 什么时候需要使用事务？

**A:** 当多个操作必须保证原子性时：

- ✅ 充值：更新余额 + 插入流水记录
- ✅ 发钱：扣款 + 创建发钱记录
- ✅ 转账：账户A扣款 + 账户B加款
- ❌ 单纯查询数据（不需要事务）
- ❌ 单个 INSERT/UPDATE/DELETE（自动是事务）

### Q2: commit() 和 rollback() 的区别？

**A:**

```java
commit()    // 提交：让所有操作生效，数据永久保存
rollback()  // 回滚：撤销所有操作，数据恢复到事务开始前
```

### Q3: 忘记调用 commit() 会怎样？

**A:**

```java
conn.setAutoCommit(false);
// ... 执行 SQL
// 忘记 conn.commit();
conn.close();

结果：❌ 事务自动回滚，所有操作都没生效！
```

### Q4: 为什么要 setAutoCommit(true) 恢复？

**A:**

```java
// Connection 可能被连接池复用
// 如果不恢复，下次使用这个连接时：
conn.setAutoCommit(false);  // 还是 false
// 后续的单条 SQL 也不会自动提交，导致问题

// 所以必须在 finally 中恢复：
finally {
    conn.setAutoCommit(true);
}
```

### Q5: 事务越大越好吗？

**A:** ❌ 不是！

```java
// ❌ 错误：事务太大
开始事务
  操作1：查询数据
  操作2：复杂计算（耗时 10 秒）
  操作3：更新数据
提交事务

问题：
- 锁定时间太长，其他事务等待
- 容易超时

// ✅ 正确：事务尽量小
开始事务
  操作1：更新数据
提交事务

在事务外做：
  复杂计算、网络请求、文件操作等
```

---

## 📝 最佳实践

1. **事务尽量短**：只包含必须的数据库操作
2. **避免在事务中做耗时操作**：复杂计算、网络请求等
3. **合理使用锁**：需要时用 `FOR UPDATE`，但不要滥用
4. **总是在 finally 中恢复 autoCommit**
5. **捕获异常并回滚**：不要让半完成的事务提交

---

## 🎯 Step 4 核心代码

查看 `AccountDao.java` 中的 `recharge()` 方法，这是标准的事务处理流程！

**事务处理三步走**：
1. `setAutoCommit(false)` - 开启事务
2. 执行 SQL 操作
3. `commit()` 提交 或 `rollback()` 回滚

---

**掌握了事务，你就掌握了数据一致性的核心！** 🎉
