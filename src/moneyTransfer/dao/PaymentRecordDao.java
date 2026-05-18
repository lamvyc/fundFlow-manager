package moneyTransfer.dao;

import moneyTransfer.entity.PaymentRecord;
import moneyTransfer.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 发钱记录数据访问对象（DAO）
 * 
 * 核心功能：
 * 1. 创建发钱记录
 * 2. 查询发钱记录
 * 3. 更新记录状态
 * 4. 统计查询
 */
public class PaymentRecordDao {
    
    /**
     * 创建发钱记录（在事务中调用）
     * 
     * 注意：此方法需要在事务中调用，不自己管理连接
     * 
     * @param record 发钱记录对象
     * @param conn 数据库连接（由调用者管理事务）
     * @return 生成的记录ID，失败返回 -1
     * @throws SQLException 数据库异常
     */
    public int createPaymentRecord(PaymentRecord record, Connection conn) throws SQLException {
        String sql = "INSERT INTO payment_records (account_id, user_id, amount, status, remark) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, record.getAccountId());
            pstmt.setInt(2, record.getUserId());
            pstmt.setBigDecimal(3, record.getAmount());
            pstmt.setString(4, record.getStatus() != null ? record.getStatus() : PaymentRecord.STATUS_PENDING);
            pstmt.setString(5, record.getRemark());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            
            return -1;
        }
    }
    
    /**
     * 根据 ID 查询发钱记录
     * 
     * @param id 记录ID
     * @return 发钱记录对象，不存在返回 null
     */
    public PaymentRecord getPaymentRecordById(int id) {
        String sql = "SELECT id, account_id, user_id, amount, status, remark, create_time, update_time " +
                     "FROM payment_records WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractPaymentRecordFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("查询发钱记录失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * 查询指定账户的发钱记录
     * 
     * @param accountId 账户ID
     * @return 发钱记录列表
     */
    public List<PaymentRecord> getPaymentRecordsByAccountId(int accountId) {
        String sql = "SELECT id, account_id, user_id, amount, status, remark, create_time, update_time " +
                     "FROM payment_records WHERE account_id = ? ORDER BY create_time DESC";
        
        List<PaymentRecord> records = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PaymentRecord record = extractPaymentRecordFromResultSet(rs);
                records.add(record);
            }
            
            return records;
            
        } catch (SQLException e) {
            System.err.println("查询发钱记录列表失败: " + e.getMessage());
            e.printStackTrace();
            return records;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * 查询指定用户的收款记录
     * 
     * @param userId 用户ID
     * @return 发钱记录列表
     */
    public List<PaymentRecord> getPaymentRecordsByUserId(int userId) {
        String sql = "SELECT id, account_id, user_id, amount, status, remark, create_time, update_time " +
                     "FROM payment_records WHERE user_id = ? ORDER BY create_time DESC";
        
        List<PaymentRecord> records = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PaymentRecord record = extractPaymentRecordFromResultSet(rs);
                records.add(record);
            }
            
            return records;
            
        } catch (SQLException e) {
            System.err.println("查询收款记录列表失败: " + e.getMessage());
            e.printStackTrace();
            return records;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * 查询指定状态的发钱记录
     * 
     * @param status 状态（PENDING/COMPLETED/CANCELLED）
     * @return 发钱记录列表
     */
    public List<PaymentRecord> getPaymentRecordsByStatus(String status) {
        String sql = "SELECT id, account_id, user_id, amount, status, remark, create_time, update_time " +
                     "FROM payment_records WHERE status = ? ORDER BY create_time DESC";
        
        List<PaymentRecord> records = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PaymentRecord record = extractPaymentRecordFromResultSet(rs);
                records.add(record);
            }
            
            return records;
            
        } catch (SQLException e) {
            System.err.println("查询指定状态的发钱记录失败: " + e.getMessage());
            e.printStackTrace();
            return records;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * 更新发钱记录状态
     * 
     * @param id 记录ID
     * @param status 新状态
     * @return true=更新成功，false=更新失败
     */
    public boolean updatePaymentStatus(int id, String status) {
        String sql = "UPDATE payment_records SET status = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("更新发钱记录状态失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * 查询所有发钱记录
     * 
     * @return 发钱记录列表
     */
    public List<PaymentRecord> getAllPaymentRecords() {
        String sql = "SELECT id, account_id, user_id, amount, status, remark, create_time, update_time " +
                     "FROM payment_records ORDER BY create_time DESC";
        
        List<PaymentRecord> records = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PaymentRecord record = extractPaymentRecordFromResultSet(rs);
                records.add(record);
            }
            
            return records;
            
        } catch (SQLException e) {
            System.err.println("查询所有发钱记录失败: " + e.getMessage());
            e.printStackTrace();
            return records;
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * 从结果集中提取发钱记录对象
     */
    private PaymentRecord extractPaymentRecordFromResultSet(ResultSet rs) throws SQLException {
        PaymentRecord record = new PaymentRecord();
        record.setId(rs.getInt("id"));
        record.setAccountId(rs.getInt("account_id"));
        record.setUserId(rs.getInt("user_id"));
        record.setAmount(rs.getBigDecimal("amount"));
        record.setStatus(rs.getString("status"));
        record.setRemark(rs.getString("remark"));
        record.setCreateTime(rs.getTimestamp("create_time"));
        record.setUpdateTime(rs.getTimestamp("update_time"));
        return record;
    }
    
    /**
     * 关闭资源
     */
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
