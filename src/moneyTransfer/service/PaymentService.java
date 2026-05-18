package moneyTransfer.service;

import moneyTransfer.dao.AccountDao;
import moneyTransfer.dao.PaymentRecordDao;
import moneyTransfer.dao.UserDao;
import moneyTransfer.entity.Account;
import moneyTransfer.entity.PaymentRecord;
import moneyTransfer.entity.User;
import moneyTransfer.exception.AccountFrozenException;
import moneyTransfer.exception.InsufficientBalanceException;
import moneyTransfer.exception.InvalidAmountException;
import moneyTransfer.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 发钱服务类 - 业务逻辑层
 * 
 * ⭐⭐⭐ 这是 Step 5 的核心类！⭐⭐⭐
 * 
 * 核心功能：
 * 1. 发钱操作（复杂事务：账户扣款 + 创建发钱记录）
 * 2. 业务逻辑验证（余额检查、状态检查）
 * 3. 事务管理（保证原子性）
 * 4. 异常处理（业务异常）
 */
public class PaymentService {
    
    private AccountDao accountDao;
    private UserDao userDao;
    private PaymentRecordDao paymentRecordDao;
    
    /**
     * 构造函数
     */
    public PaymentService() {
        this.accountDao = new AccountDao();
        this.userDao = new UserDao();
        this.paymentRecordDao = new PaymentRecordDao();
    }
    
    /**
     * 发钱操作（核心方法）
     * 
     * ⭐⭐⭐ Step 5 最重要的方法！⭐⭐⭐
     * 
     * 发钱流程：
     * 1. 参数校验（金额、账户、用户）
     * 2. 开启事务
     * 3. 检查账户状态（是否冻结）
     * 4. 检查账户余额（是否足够）
     * 5. 扣减账户余额（FOR UPDATE 加锁）
     * 6. 创建发钱记录（COMPLETED 状态）
     * 7. 提交事务
     * 8. 发生异常则回滚
     * 
     * @param accountId 发钱账户ID
     * @param userId 收款用户ID
     * @param amount 发钱金额
     * @param remark 备注
     * @return 发钱记录ID
     * @throws InvalidAmountException 无效金额异常
     * @throws InsufficientBalanceException 余额不足异常
     * @throws AccountFrozenException 账户冻结异常
     * @throws SQLException 数据库异常
     */
    public int sendMoney(int accountId, int userId, BigDecimal amount, String remark) 
            throws InvalidAmountException, InsufficientBalanceException, AccountFrozenException, SQLException {
        
        // 1. 参数校验
        validateSendMoneyParams(accountId, userId, amount);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // 2. 获取数据库连接
            conn = DBUtil.getConnection();
            
            // 3. 开启事务（关闭自动提交）
            conn.setAutoCommit(false);
            
            // 4. 查询账户信息（FOR UPDATE 加锁，防止并发问题）
            String selectSql = "SELECT id, name, balance, status FROM accounts WHERE id = ? FOR UPDATE";
            pstmt = conn.prepareStatement(selectSql);
            pstmt.setInt(1, accountId);
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                throw new SQLException("账户不存在：accountId=" + accountId);
            }
            
            // 5. 检查账户状态
            String accountStatus = rs.getString("status");
            if ("FROZEN".equals(accountStatus)) {
                throw new AccountFrozenException("账户已冻结，无法发钱：accountId=" + accountId);
            }
            
            // 6. 检查账户余额
            BigDecimal currentBalance = rs.getBigDecimal("balance");
            if (currentBalance.compareTo(amount) < 0) {
                throw new InsufficientBalanceException(
                    String.format("账户余额不足：当前余额=%.2f，发钱金额=%.2f", 
                                currentBalance, amount)
                );
            }
            
            rs.close();
            pstmt.close();
            
            // 7. 扣减账户余额
            BigDecimal newBalance = currentBalance.subtract(amount);
            String updateSql = "UPDATE accounts SET balance = ? WHERE id = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setInt(2, accountId);
            int updatedRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updatedRows == 0) {
                throw new SQLException("更新账户余额失败");
            }
            
            // 8. 创建发钱记录
            PaymentRecord record = new PaymentRecord(accountId, userId, amount, remark);
            record.setStatus(PaymentRecord.STATUS_COMPLETED);  // 扣款成功，状态为已完成
            
            int paymentId = paymentRecordDao.createPaymentRecord(record, conn);
            if (paymentId == -1) {
                throw new SQLException("创建发钱记录失败");
            }
            
            // 9. 提交事务
            conn.commit();
            
            System.out.println("✓ 发钱成功：");
            System.out.println("  - 发钱记录ID：" + paymentId);
            System.out.println("  - 账户ID：" + accountId);
            System.out.println("  - 用户ID：" + userId);
            System.out.println("  - 发钱金额：" + amount);
            System.out.println("  - 余额变化：" + currentBalance + " → " + newBalance);
            
            return paymentId;
            
        } catch (InsufficientBalanceException | AccountFrozenException e) {
            // 业务异常，回滚事务
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("✗ 发钱失败，事务已回滚：" + e.getMessage());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;  // 重新抛出业务异常
            
        } catch (SQLException e) {
            // 数据库异常，回滚事务
            System.err.println("✗ 发钱失败: " + e.getMessage());
            e.printStackTrace();
            
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("✗ 事务已回滚");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;  // 重新抛出数据库异常
            
        } finally {
            // 恢复自动提交，关闭资源
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
     * 参数校验
     */
    private void validateSendMoneyParams(int accountId, int userId, BigDecimal amount) 
            throws InvalidAmountException, SQLException {
        
        // 1. 校验金额
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("发钱金额必须大于0");
        }
        
        // 2. 校验账户是否存在
        Account account = accountDao.getAccountById(accountId);
        if (account == null) {
            throw new SQLException("账户不存在：accountId=" + accountId);
        }
        
        // 3. 校验用户是否存在
        User user = userDao.getUserById(userId);
        if (user == null) {
            throw new SQLException("用户不存在：userId=" + userId);
        }
        
        // 4. 校验用户状态（是否已删除）
        if ("DELETED".equals(user.getStatus())) {
            throw new SQLException("用户已被删除，无法发钱：userId=" + userId);
        }
    }
    
    /**
     * 批量发钱（扩展功能）
     * 
     * 注意：这个方法会为每笔发钱创建单独的事务
     * 如果需要所有发钱在同一个事务中，需要重新设计
     * 
     * @param accountId 发钱账户ID
     * @param userIds 收款用户ID列表
     * @param amount 每人的发钱金额
     * @param remark 备注
     * @return 成功发钱的记录数
     */
    public int batchSendMoney(int accountId, int[] userIds, BigDecimal amount, String remark) {
        int successCount = 0;
        
        for (int userId : userIds) {
            try {
                sendMoney(accountId, userId, amount, remark);
                successCount++;
            } catch (Exception e) {
                System.err.println("发钱失败（用户ID=" + userId + "）: " + e.getMessage());
            }
        }
        
        return successCount;
    }
    
    /**
     * 取消发钱（仅支持待处理状态）
     * 
     * @param paymentId 发钱记录ID
     * @return true=取消成功，false=取消失败
     */
    public boolean cancelPayment(int paymentId) {
        // 1. 查询发钱记录
        PaymentRecord record = paymentRecordDao.getPaymentRecordById(paymentId);
        if (record == null) {
            System.err.println("发钱记录不存在：paymentId=" + paymentId);
            return false;
        }
        
        // 2. 检查状态（只有待处理状态才能取消）
        if (!record.isPending()) {
            System.err.println("只有待处理状态的记录才能取消，当前状态：" + record.getStatus());
            return false;
        }
        
        // 3. 更新状态为已取消
        return paymentRecordDao.updatePaymentStatus(paymentId, PaymentRecord.STATUS_CANCELLED);
    }
}
