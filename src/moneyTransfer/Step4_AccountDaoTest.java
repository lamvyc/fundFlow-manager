package moneyTransfer;

import moneyTransfer.dao.AccountDao;
import moneyTransfer.entity.Account;
import moneyTransfer.entity.RechargeRecord;
import moneyTransfer.util.PrintUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Step 4: 账户管理测试
 * 
 * 学习目标：
 * 1. ⭐ 理解数据库事务（最重要！）
 * 2. 掌握 setAutoCommit() / commit() / rollback()
 * 3. 理解 SELECT ... FOR UPDATE 悲观锁
 * 4. 学会处理并发问题
 * 
 * 运行方式：
 * cd /Users/unravel/fundFlow-manager
 * ./run.sh
 * 选择 5) Step4: 账户管理测试
 */
public class Step4_AccountDaoTest {
    
    public static void main(String[] args) {
        PrintUtil.title("Step 4: 账户管理（事务处理）");
        
        AccountDao accountDao = new AccountDao();
        
        // 1. 测试创建账户
        testCreateAccount(accountDao);
        
        // 2. 测试查询账户
        testGetAccount(accountDao);
        
        // 3. 测试查询所有账户
        testGetAllAccounts(accountDao);
        
        // 4. ⭐ 测试充值（事务处理）
        testRecharge(accountDao);
        
        // 5. 测试查询充值记录
        testGetRechargeRecords(accountDao);
        
        // 6. 测试账户冻结
        testFreezeAccount(accountDao);
        
        // 7. 测试账户解冻
        testUnfreezeAccount(accountDao);
        
        PrintUtil.success("\n✓ Step 4 完成！账户管理和事务处理测试通过");
        PrintUtil.info("\n你已经掌握：");
        PrintUtil.info("  ✓ 数据库事务（ACID）");
        PrintUtil.info("  ✓ commit() 和 rollback()");
        PrintUtil.info("  ✓ SELECT ... FOR UPDATE 悲观锁");
        PrintUtil.info("  ✓ 充值流水记录");
        PrintUtil.info("\n下一步：Step 5 - 发钱功能（复杂事务处理）");
    }
    
    /**
     * 测试创建账户
     */
    private static void testCreateAccount(AccountDao accountDao) {
        PrintUtil.subtitle("1. 测试创建账户");
        
        Account newAccount = new Account("测试账户", new BigDecimal("50000.00"));
        
        PrintUtil.info("准备创建账户：");
        System.out.println("  账户名称: " + newAccount.getName());
        System.out.println("  初始余额: " + newAccount.getBalance());
        
        int accountId = accountDao.createAccount(newAccount);
        
        if (accountId > 0) {
            PrintUtil.success("✓ 创建成功！账户ID: " + accountId);
        } else {
            PrintUtil.error("✗ 创建失败");
        }
        System.out.println();
    }
    
    /**
     * 测试查询账户
     */
    private static void testGetAccount(AccountDao accountDao) {
        PrintUtil.subtitle("2. 测试查询账户");
        
        int accountId = 1;
        PrintUtil.info("查询账户 ID = " + accountId);
        
        Account account = accountDao.getAccountById(accountId);
        
        if (account != null) {
            PrintUtil.success("✓ 查询成功：");
            printAccountInfo(account);
        } else {
            PrintUtil.error("✗ 账户不存在");
        }
        System.out.println();
    }
    
    /**
     * 测试查询所有账户
     */
    private static void testGetAllAccounts(AccountDao accountDao) {
        PrintUtil.subtitle("3. 测试查询所有账户");
        
        List<Account> accounts = accountDao.getAllAccounts();
        
        PrintUtil.success("✓ 查询成功，共找到 " + accounts.size() + " 个账户：");
        System.out.println();
        
        // 打印表头
        System.out.printf("%-5s %-20s %-15s %-10s%n", 
            "ID", "账户名称", "余额", "状态");
        System.out.println("-".repeat(60));
        
        // 打印每个账户
        for (Account account : accounts) {
            System.out.printf("%-5d %-20s %-15s %-10s%n",
                account.getId(),
                account.getName(),
                account.getBalance(),
                account.getStatus()
            );
        }
        System.out.println();
    }
    
    /**
     * ⭐ 测试充值（事务处理）
     * 这是本节最重要的测试！
     */
    private static void testRecharge(AccountDao accountDao) {
        PrintUtil.subtitle("4. ⭐ 测试充值（事务处理）");
        
        int accountId = 1;
        Account accountBefore = accountDao.getAccountById(accountId);
        
        if (accountBefore == null) {
            PrintUtil.error("✗ 账户不存在");
            return;
        }
        
        PrintUtil.info("充值前账户信息：");
        System.out.println("  账户ID: " + accountBefore.getId());
        System.out.println("  账户名称: " + accountBefore.getName());
        System.out.println("  当前余额: " + accountBefore.getBalance());
        
        // 充值 20000 元
        BigDecimal rechargeAmount = new BigDecimal("20000.00");
        PrintUtil.info("\n正在充值 " + rechargeAmount + " 元...");
        
        boolean success = accountDao.recharge(accountId, rechargeAmount);
        
        if (success) {
            PrintUtil.success("✓ 充值成功！");
            
            // 再次查询验证
            Account accountAfter = accountDao.getAccountById(accountId);
            PrintUtil.info("\n充值后账户信息：");
            System.out.println("  当前余额: " + accountAfter.getBalance());
            System.out.println("  余额变化: " + accountBefore.getBalance() + " → " + accountAfter.getBalance());
            
            // 计算差额
            BigDecimal diff = accountAfter.getBalance().subtract(accountBefore.getBalance());
            System.out.println("  增加金额: " + diff);
            
            if (diff.compareTo(rechargeAmount) == 0) {
                PrintUtil.success("✓ 余额正确！");
            } else {
                PrintUtil.error("✗ 余额不正确！");
            }
        } else {
            PrintUtil.error("✗ 充值失败");
        }
        System.out.println();
        
        // 测试异常情况
        PrintUtil.info("测试异常情况：");
        
        // 测试1：充值金额为负数
        PrintUtil.info("1) 充值负数金额：");
        boolean result1 = accountDao.recharge(accountId, new BigDecimal("-100"));
        if (!result1) {
            PrintUtil.success("✓ 正确拒绝了负数充值");
        } else {
            PrintUtil.error("✗ 错误：接受了负数充值");
        }
        
        // 测试2：充值到不存在的账户
        PrintUtil.info("2) 充值到不存在的账户：");
        boolean result2 = accountDao.recharge(9999, new BigDecimal("100"));
        if (!result2) {
            PrintUtil.success("✓ 正确处理了不存在的账户");
        } else {
            PrintUtil.error("✗ 错误：向不存在的账户充值成功");
        }
        
        System.out.println();
    }
    
    /**
     * 测试查询充值记录
     */
    private static void testGetRechargeRecords(AccountDao accountDao) {
        PrintUtil.subtitle("5. 测试查询充值记录");
        
        int accountId = 1;
        PrintUtil.info("查询账户 " + accountId + " 的充值记录：");
        
        List<RechargeRecord> records = accountDao.getRechargeRecords(accountId);
        
        if (records.isEmpty()) {
            PrintUtil.warning("该账户暂无充值记录");
        } else {
            PrintUtil.success("✓ 找到 " + records.size() + " 条充值记录：");
            System.out.println();
            
            // 打印表头
            System.out.printf("%-5s %-15s %-15s %-15s %-20s%n", 
                "ID", "充值金额", "充值前余额", "充值后余额", "充值时间");
            System.out.println("-".repeat(80));
            
            // 打印每条记录
            for (RechargeRecord record : records) {
                System.out.printf("%-5d %-15s %-15s %-15s %-20s%n",
                    record.getId(),
                    record.getAmount(),
                    record.getBeforeBalance(),
                    record.getAfterBalance(),
                    record.getCreateTime()
                );
            }
        }
        System.out.println();
    }
    
    /**
     * 测试账户冻结
     */
    private static void testFreezeAccount(AccountDao accountDao) {
        PrintUtil.subtitle("6. 测试账户冻结");
        
        int accountId = 2;
        Account account = accountDao.getAccountById(accountId);
        
        if (account == null) {
            PrintUtil.error("✗ 账户不存在");
            return;
        }
        
        PrintUtil.info("冻结前状态: " + account.getStatus());
        
        boolean success = accountDao.freezeAccount(accountId);
        
        if (success) {
            PrintUtil.success("✓ 冻结成功");
            
            Account frozenAccount = accountDao.getAccountById(accountId);
            PrintUtil.info("冻结后状态: " + frozenAccount.getStatus());
        } else {
            PrintUtil.error("✗ 冻结失败");
        }
        System.out.println();
    }
    
    /**
     * 测试账户解冻
     */
    private static void testUnfreezeAccount(AccountDao accountDao) {
        PrintUtil.subtitle("7. 测试账户解冻");
        
        int accountId = 2;
        Account account = accountDao.getAccountById(accountId);
        
        if (account == null) {
            PrintUtil.error("✗ 账户不存在");
            return;
        }
        
        PrintUtil.info("解冻前状态: " + account.getStatus());
        
        boolean success = accountDao.unfreezeAccount(accountId);
        
        if (success) {
            PrintUtil.success("✓ 解冻成功");
            
            Account unfrozenAccount = accountDao.getAccountById(accountId);
            PrintUtil.info("解冻后状态: " + unfrozenAccount.getStatus());
        } else {
            PrintUtil.error("✗ 解冻失败");
        }
        System.out.println();
    }
    
    /**
     * 打印账户详细信息
     */
    private static void printAccountInfo(Account account) {
        System.out.println("  ID: " + account.getId());
        System.out.println("  名称: " + account.getName());
        System.out.println("  余额: " + account.getBalance());
        System.out.println("  状态: " + account.getStatus());
        System.out.println("  创建时间: " + account.getCreateTime());
        System.out.println("  更新时间: " + account.getUpdateTime());
    }
}
