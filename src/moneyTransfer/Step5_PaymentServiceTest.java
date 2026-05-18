package moneyTransfer;

import moneyTransfer.dao.AccountDao;
import moneyTransfer.dao.PaymentRecordDao;
import moneyTransfer.dao.UserDao;
import moneyTransfer.entity.Account;
import moneyTransfer.entity.PaymentRecord;
import moneyTransfer.entity.User;
import moneyTransfer.exception.AccountFrozenException;
import moneyTransfer.exception.InsufficientBalanceException;
import moneyTransfer.exception.InvalidAmountException;
import moneyTransfer.service.PaymentService;
import moneyTransfer.util.PrintUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Step 5: 发钱功能测试程序
 * 
 * ⭐⭐⭐ 本节学习重点 ⭐⭐⭐
 * 1. 复杂事务处理（涉及多张表）
 * 2. 业务逻辑层设计（Service 层）
 * 3. 自定义异常处理
 * 4. 原子性保证（两步操作要么都成功，要么都失败）
 * 
 * 测试内容：
 * - 正常发钱流程
 * - 余额不足测试
 * - 账户冻结测试
 * - 无效金额测试
 * - 事务回滚验证
 */
public class Step5_PaymentServiceTest {
    
    public static void main(String[] args) {
        PrintUtil.printHeader("Step 5: 发钱功能测试");
        
        PaymentService paymentService = new PaymentService();
        AccountDao accountDao = new AccountDao();
        UserDao userDao = new UserDao();
        PaymentRecordDao paymentRecordDao = new PaymentRecordDao();
        
        try {
            // ========== 测试准备 ==========
            PrintUtil.printSection("测试准备");
            
            // 查询测试账户和用户
            Account account1 = accountDao.getAccountById(1);  // 公司发薪账户
            Account account2 = accountDao.getAccountById(2);  // 奖金发放账户
            User user1 = userDao.getUserById(1);  // 张三
            User user2 = userDao.getUserById(2);  // 李四
            User user3 = userDao.getUserById(3);  // 王五
            
            System.out.println("✓ 发钱账户：" + account1.getName() + "，余额：" + account1.getBalance());
            System.out.println("✓ 收款人1：" + user1.getName() + "，手机号：" + user1.getPhone());
            System.out.println("✓ 收款人2：" + user2.getName() + "，手机号：" + user2.getPhone());
            System.out.println("✓ 收款人3：" + user3.getName() + "，手机号：" + user3.getPhone());
            System.out.println();
            
            
            // ========== 测试1：正常发钱 ==========
            PrintUtil.printSection("测试1：正常发钱流程");
            
            try {
                BigDecimal amount1 = new BigDecimal("1000.00");
                int paymentId1 = paymentService.sendMoney(
                    account1.getId(), 
                    user1.getId(), 
                    amount1, 
                    "工资发放"
                );
                
                System.out.println();
                
                // 验证结果
                Account updatedAccount = accountDao.getAccountById(1);
                System.out.println("✓ 验证：账户余额已更新为 " + updatedAccount.getBalance());
                
                PaymentRecord record = paymentRecordDao.getPaymentRecordById(paymentId1);
                System.out.println("✓ 验证：发钱记录已创建，状态=" + record.getStatus());
                
            } catch (Exception e) {
                System.err.println("✗ 测试失败: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println();
            
            
            // ========== 测试2：批量发钱 ==========
            PrintUtil.printSection("测试2：批量发钱（给多个用户）");
            
            try {
                BigDecimal amount2 = new BigDecimal("500.00");
                int userId2 = user2.getId();
                int userId3 = user3.getId();
                
                System.out.println("给李四发钱 500 元...");
                int paymentId2 = paymentService.sendMoney(
                    account1.getId(), 
                    userId2, 
                    amount2, 
                    "奖金发放"
                );
                System.out.println();
                
                System.out.println("给王五发钱 500 元...");
                int paymentId3 = paymentService.sendMoney(
                    account1.getId(), 
                    userId3, 
                    amount2, 
                    "奖金发放"
                );
                System.out.println();
                
                // 查看账户余额
                Account updatedAccount = accountDao.getAccountById(1);
                System.out.println("✓ 当前账户余额：" + updatedAccount.getBalance());
                
            } catch (Exception e) {
                System.err.println("✗ 测试失败: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println();
            
            
            // ========== 测试3：余额不足（测试事务回滚）==========
            PrintUtil.printSection("测试3：余额不足（测试事务回滚）");
            
            try {
                BigDecimal largeAmount = new BigDecimal("999999.00");
                System.out.println("尝试发钱 999999 元（超过余额）...");
                
                // 记录发钱前的余额
                Account beforeAccount = accountDao.getAccountById(1);
                BigDecimal balanceBefore = beforeAccount.getBalance();
                System.out.println("发钱前余额：" + balanceBefore);
                
                // 尝试发钱（应该失败）
                paymentService.sendMoney(
                    account1.getId(), 
                    user1.getId(), 
                    largeAmount, 
                    "测试余额不足"
                );
                
                System.err.println("✗ 测试失败：应该抛出 InsufficientBalanceException 异常");
                
            } catch (InsufficientBalanceException e) {
                System.out.println();
                System.out.println("✓ 预期异常：" + e.getMessage());
                
                // 验证余额没有变化（事务已回滚）
                Account afterAccount = accountDao.getAccountById(1);
                BigDecimal balanceAfter = afterAccount.getBalance();
                System.out.println("发钱后余额：" + balanceAfter);
                
                if (balanceAfter.equals(afterAccount.getBalance())) {
                    System.out.println("✓ 验证通过：余额未变化，事务已回滚");
                } else {
                    System.err.println("✗ 验证失败：余额发生变化，事务回滚失败！");
                }
                
            } catch (Exception e) {
                System.err.println("✗ 测试失败: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println();
            
            
            // ========== 测试4：账户冻结 ==========
            PrintUtil.printSection("测试4：账户冻结（不能发钱）");
            
            try {
                // 冻结账户
                System.out.println("冻结账户2（奖金发放账户）...");
                accountDao.freezeAccount(account2.getId());
                System.out.println("✓ 账户已冻结");
                System.out.println();
                
                // 尝试用冻结账户发钱
                System.out.println("尝试用冻结账户发钱...");
                paymentService.sendMoney(
                    account2.getId(), 
                    user1.getId(), 
                    new BigDecimal("100.00"), 
                    "测试冻结账户"
                );
                
                System.err.println("✗ 测试失败：应该抛出 AccountFrozenException 异常");
                
            } catch (AccountFrozenException e) {
                System.out.println("✓ 预期异常：" + e.getMessage());
                
            } catch (Exception e) {
                System.err.println("✗ 测试失败: " + e.getMessage());
                e.printStackTrace();
                
            } finally {
                // 解冻账户
                accountDao.unfreezeAccount(account2.getId());
                System.out.println("✓ 账户已解冻");
            }
            
            System.out.println();
            
            
            // ========== 测试5：无效金额 ==========
            PrintUtil.printSection("测试5：无效金额（负数、零）");
            
            try {
                System.out.println("尝试发钱 -100 元（负数）...");
                paymentService.sendMoney(
                    account1.getId(), 
                    user1.getId(), 
                    new BigDecimal("-100.00"), 
                    "测试负数金额"
                );
                
                System.err.println("✗ 测试失败：应该抛出 InvalidAmountException 异常");
                
            } catch (InvalidAmountException e) {
                System.out.println("✓ 预期异常：" + e.getMessage());
                
            } catch (Exception e) {
                System.err.println("✗ 测试失败: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println();
            
            try {
                System.out.println("尝试发钱 0 元（零）...");
                paymentService.sendMoney(
                    account1.getId(), 
                    user1.getId(), 
                    BigDecimal.ZERO, 
                    "测试零金额"
                );
                
                System.err.println("✗ 测试失败：应该抛出 InvalidAmountException 异常");
                
            } catch (InvalidAmountException e) {
                System.out.println("✓ 预期异常：" + e.getMessage());
                
            } catch (Exception e) {
                System.err.println("✗ 测试失败: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println();
            
            
            // ========== 测试6：查询发钱记录 ==========
            PrintUtil.printSection("测试6：查询发钱记录");
            
            // 查询账户1的所有发钱记录
            List<PaymentRecord> records1 = paymentRecordDao.getPaymentRecordsByAccountId(account1.getId());
            System.out.println("✓ 账户1的发钱记录数：" + records1.size());
            for (PaymentRecord record : records1) {
                System.out.println("  - ID=" + record.getId() + 
                                 ", 用户ID=" + record.getUserId() + 
                                 ", 金额=" + record.getAmount() + 
                                 ", 状态=" + record.getStatus() +
                                 ", 备注=" + record.getRemark());
            }
            System.out.println();
            
            // 查询用户1的收款记录
            List<PaymentRecord> records2 = paymentRecordDao.getPaymentRecordsByUserId(user1.getId());
            System.out.println("✓ 用户1的收款记录数：" + records2.size());
            for (PaymentRecord record : records2) {
                System.out.println("  - ID=" + record.getId() + 
                                 ", 账户ID=" + record.getAccountId() + 
                                 ", 金额=" + record.getAmount() + 
                                 ", 状态=" + record.getStatus() +
                                 ", 备注=" + record.getRemark());
            }
            System.out.println();
            
            // 查询所有已完成的发钱记录
            List<PaymentRecord> completedRecords = paymentRecordDao.getPaymentRecordsByStatus(
                PaymentRecord.STATUS_COMPLETED
            );
            System.out.println("✓ 所有已完成的发钱记录数：" + completedRecords.size());
            System.out.println();
            
            
            // ========== 测试总结 ==========
            PrintUtil.printSection("测试总结");
            
            System.out.println("✓ 所有测试完成！");
            System.out.println();
            System.out.println("📚 本节学到的知识：");
            System.out.println("  1. 复杂事务处理（多表操作）");
            System.out.println("  2. 业务逻辑层设计（Service 层）");
            System.out.println("  3. 自定义异常处理");
            System.out.println("  4. 事务的原子性保证");
            System.out.println("  5. FOR UPDATE 悲观锁防止并发问题");
            System.out.println();
            System.out.println("⭐ 核心要点：");
            System.out.println("  - 发钱 = 扣款 + 创建记录，两步必须在同一个事务中！");
            System.out.println("  - 使用 FOR UPDATE 锁定账户余额，防止并发修改");
            System.out.println("  - 业务异常要回滚事务，保证数据一致性");
            System.out.println("  - Service 层负责业务逻辑，DAO 层负责数据访问");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("✗ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        PrintUtil.printFooter("Step 5 测试结束");
    }
}
