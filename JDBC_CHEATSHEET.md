# JDBC 速查表（常用方法）

## 1. 获取连接
```java
Connection conn = DriverManager.getConnection(url, user, password);
```

## 2. 执行 INSERT/UPDATE/DELETE
```java
String sql = "INSERT INTO users (name, age) VALUES (?, ?)";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setString(1, "张三");
    pstmt.setInt(2, 25);
    int rows = pstmt.executeUpdate();  // 返回影响的行数
    System.out.println("插入了 " + rows + " 行");
}
```

## 3. 执行 SELECT
```java
String sql = "SELECT id, name, age FROM users WHERE age > ?";
try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setInt(1, 18);
    ResultSet rs = pstmt.executeQuery();
    while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int age = rs.getInt("age");
        System.out.println(id + " - " + name + " - " + age);
    }
    rs.close();
}
```

## 4. 事务处理
```java
try {
    conn.setAutoCommit(false);  // 开启事务
    
    // 执行多个 SQL
    // ...
    
    conn.commit();  // 提交事务
} catch (Exception e) {
    conn.rollback();  // 回滚事务
    throw e;
}
```

## 5. 常用方法速查

### Connection 常用方法
| 方法 | 作用 |
|------|------|
| `createStatement()` | 创建普通 SQL 执行器 |
| `prepareStatement(String sql)` | 创建预编译 SQL 执行器 |
| `setAutoCommit(boolean)` | 开启/关闭自动提交 |
| `commit()` | 提交事务 |
| `rollback()` | 回滚事务 |
| `close()` | 关闭连接 |

### PreparedStatement 常用方法
| 方法 | 作用 |
|------|------|
| `setInt(int index, int value)` | 设置整数参数 |
| `setString(int index, String value)` | 设置字符串参数 |
| `setDouble(int index, double value)` | 设置小数参数 |
| `setTimestamp(int index, Timestamp value)` | 设置时间戳参数 |
| `executeUpdate()` | 执行 INSERT/UPDATE/DELETE |
| `executeQuery()` | 执行 SELECT |
| `close()` | 关闭执行器 |

### ResultSet 常用方法
| 方法 | 作用 |
|------|------|
| `next()` | 移动到下一行（返回 true/false） |
| `getInt(String columnName)` | 获取整数列 |
| `getString(String columnName)` | 获取字符串列 |
| `getDouble(String columnName)` | 获取小数列 |
| `getTimestamp(String columnName)` | 获取时间戳列 |
| `close()` | 关闭结果集 |

---

## 记忆口诀

**JDBC 五步曲**：
1. **获取连接**（DriverManager.getConnection）
2. **准备 SQL**（prepareStatement）
3. **设置参数**（setXxx）
4. **执行 SQL**（executeUpdate/executeQuery）
5. **处理结果**（ResultSet.next + getXxx）
6. **关闭资源**（close）

---

## 学习建议

1. **不要死记硬背**：用到什么学什么
2. **重复使用**：同一个方法用 3-5 次就记住了
3. **建立关联**：Connection → Statement → ResultSet（流程化记忆）
4. **善用 IDE**：不确定方法名时按 Ctrl+Space
5. **看官方文档**：https://docs.oracle.com/javase/8/docs/api/

---

## 进阶技巧

### 如何快速查看某个类有哪些方法？

**方式1：IDE 中查看**
```
1. 在代码中输入类名，按住 Ctrl（Mac 用 Cmd）+ 左键点击
2. 跳转到类定义，可以看到所有方法
```

**方式2：查看官方文档**
```
Google 搜索：java.sql.Connection javadoc
直接跳转到官方文档，查看所有方法
```

**方式3：用 javap 命令**
```bash
# 查看 Connection 接口的所有方法
javap java.sql.Connection
```

---

## 常见疑问

### Q: 为什么有些方法我从没见过？
A: 因为很多方法是高级用法或特殊场景才用。80% 的开发只用到 20% 的方法。

### Q: 怎么知道该用哪个方法？
A: 
1. 看方法名（Java 方法名通常很直观）
2. 看 IDE 提示（会显示参数和返回值）
3. 看官方文档
4. 问 AI 或查 StackOverflow

### Q: 需要记住所有参数吗？
A: 不需要！IDE 会提示参数类型。你只需记住：
- 方法名
- 大概的作用
- 什么时候用

---

**更新日期**：2026-05-09
