package moneyTransfer.dao;

import moneyTransfer.entity.Account;
import moneyTransfer.entity.RechargeRecord;
import moneyTransfer.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 账户数据访问对象（DAO）
 * 
 * 核心功能：
 * 1. 账户的 CRUD 操作
 * 2. 充值功能（包含事务处理）
 * 3. 余额查询和更新
 * 4. 账户状态管理（冻结/解冻）
 */
public class AccountDao {
    
    /**
     * 创建账户
     * 
     * @param account 账户对象
     * @return 生成的账户ID，失败返回 -1
     */
    public int createAccount(Account account) {
        String sql = "INSERT INTO accounts (name, balance, status) VALUES (?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, account.getName());
            pstmt.setBigDecimal(2, account.getBalance());
            pstmt.setString(3, account.getStatus() != null ? account.getStatus() : "ACTIVE");
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
            return -1;
            
        } catch (SQLException e) {
            System.err.println("创建账户失败: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 根据 ID 查询账户
     * 
     * @param id 账户ID
     * @return 账户对象，不存在返回 null
     */
    public Account getAccountById(int id) {
        String sql = "SELECT id, name, balance, status, create_time, update_time " +
                     "FROM accounts WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("查询账户失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 查询所有账户
     * 
     * @return 账户列表
     */
    public List<Account> getAllAccounts() {
        String sql = "SELECT id, name, balance, status, create_time, update_time " +
                     "FROM accounts ORDER BY id";
        
        List<Account> accounts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Account account = extractAccountFromResultSet(rs);
                accounts.add(account);
            }
            
            return accounts;
            
        } catch (SQLException e) {
            System.err.println("查询账户列表失败: " + e.getMessage());
            e.printStackTrace();
            return accounts;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 更新账户余额
     * 
     * 注意：这个方法不应该直接调用！
     * 应该通过 recharge() 或 deductBalance() 等方法来修改余额
     * 
     * @param accountId 账户ID
     * @param newBalance 新余额
     * @return true=更新成功，false=更新失败
     */
    private boolean updateBalance(int accountId, BigDecimal newBalance, Connection conn) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setInt(2, accountId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * 冻结账户
     * 
     * @param accountId 账户ID
     * @return true=冻结成功，false=冻结失败
     */
    public boolean freezeAccount(int accountId) {
        String sql = "UPDATE accounts SET status = 'FROZEN' WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("冻结账户失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 解冻账户
     * 
     * @param accountId 账户ID
     * @return true=解冻成功，false=解冻失败
     */
    public boolean unfreezeAccount(int accountId) {
        String sql = "UPDATE accounts SET status = 'ACTIVE' WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("解冻账户失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 充值（包含事务处理）
     * 
     * ⭐⭐⭐ 这是本节最重要的方法！⭐⭐⭐
     * 
     * 充值流程：
     * 1. 开启事务（关闭自动提交）
     * 2. 查询当前余额（加锁，防止并发问题）
     * 3. 计算新余额
     * 4. 更新账户余额
     * 5. 插入充值记录
     * 6. 提交事务
     * 
     * 如果任何一步失败，回滚事务，保证数据一致性！
     * 
     * @param accountId 账户ID
     * @param amount 充值金额
     * @return true=充值成功，false=充值失败
     */
    public boolean recharge(int accountId, BigDecimal amount) {
        // 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("充值金额必须大于0");
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // 1. 获取数据库连接
            conn = DBUtil.getConnection();
            
            // 2. 开启事务（关闭自动提交）
            conn.setAutoCommit(false);
            
            // 3. 查询当前余额（FOR UPDATE 加锁，防止并发问题）
            String selectSql = "SELECT balance FROM accounts WHERE id = ? FOR UPDATE";
            pstmt = conn.prepareStatement(selectSql);
            pstmt.setInt(1, accountId);
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                System.err.println("账户不存在");
                conn.rollback();
                return false;
            }
            
            BigDecimal currentBalance = rs.getBigDecimal("balance");
            rs.close();
            pstmt.close();
            
            // 4. 计算新余额
            BigDecimal newBalance = currentBalance.add(amount);
            
            // 5. 更新账户余额
            boolean updated = updateBalance(accountId, newBalance, conn);
            if (!updated) {
                System.err.println("更新余额失败");
                conn.rollback();
                return false;
            }
            
            // 6. 插入充值记录
            String insertSql = "INSERT INTO recharge_records (account_id, amount, before_balance, after_balance) " +
                              "VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, accountId);
            pstmt.setBigDecimal(2, amount);
            pstmt.setBigDecimal(3, currentBalance);
            pstmt.setBigDecimal(4, newBalance);
            pstmt.executeUpdate();
            pstmt.close();
            
            // 7. 提交事务
            conn.commit();
            
            System.out.println("充值成功：账户ID=" + accountId + ", 充值金额=" + amount + 
                             ", 余额：" + currentBalance + " → " + newBalance);
            return true;
            
        } catch (SQLException e) {
            // 8. 发生异常，回滚事务
            System.err.println("充值失败: " + e.getMessage());
            e.printStackTrace();
            
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("事务已回滚");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
            
        } finally {
            // 9. 恢复自动提交，关闭资源
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);  // 恢复自动提交
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 查询账户的充值记录
     * 
     * @param accountId 账户ID
     * @return 充值记录列表
     */
    public List<RechargeRecord> getRechargeRecords(int accountId) {
        String sql = "SELECT id, account_id, amount, before_balance, after_balance, create_time " +
                     "FROM recharge_records WHERE account_id = ? ORDER BY create_time DESC";
        
        List<RechargeRecord> records = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RechargeRecord record = new RechargeRecord();
                record.setId(rs.getInt("id"));
                record.setAccountId(rs.getInt("account_id"));
                record.setAmount(rs.getBigDecimal("amount"));
                record.setBeforeBalance(rs.getBigDecimal("before_balance"));
                record.setAfterBalance(rs.getBigDecimal("after_balance"));
                record.setCreateTime(rs.getTimestamp("create_time"));
                records.add(record);
            }
            
            return records;
            
        } catch (SQLException e) {
            System.err.println("查询充值记录失败: " + e.getMessage());
            e.printStackTrace();
            return records;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 从结果集中提取账户对象
     */
    private Account extractAccountFromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getInt("id"));
        account.setName(rs.getString("name"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(rs.getString("status"));
        account.setCreateTime(rs.getTimestamp("create_time"));
        account.setUpdateTime(rs.getTimestamp("update_time"));
        return account;
    }
}
