package moneyTransfer;

import moneyTransfer.dao.AccountDao;
import moneyTransfer.dao.PaymentRecordDao;
import moneyTransfer.dao.UserDao;
import moneyTransfer.entity.Account;
import moneyTransfer.entity.User;
import moneyTransfer.exception.AccountFrozenException;
import moneyTransfer.exception.InsufficientBalanceException;
import moneyTransfer.exception.InvalidAmountException;
import moneyTransfer.service.PaymentService;
import moneyTransfer.service.StatisticsService;
import moneyTransfer.util.PrintUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Step 7: 完整系统集成测试
 *
 * ⭐⭐⭐ 最终步骤：验证整个系统端到端流程 ⭐⭐⭐
 *
 * 测试场景模拟一套完整的业务流程：
 *   1. 查看初始状态
 *   2. 新建用户和账户
 *   3. 充值账户
 *   4. 批量发钱
 *   5. 处理各种异常情况
 *   6. 查询记录
 *   7. 统计报表汇总
 *
 * 运行前提：数据库已初始化（执行过 Step1）
 */
public class Step7_SystemTest {

    private static final UserDao userDao                   = new UserDao();
    private static final AccountDao accountDao             = new AccountDao();
    private static final PaymentRecordDao paymentRecordDao = new PaymentRecordDao();
    private static final PaymentService paymentService     = new PaymentService();
    private static final StatisticsService statisticsService = new StatisticsService();

    public static void main(String[] args) {
        PrintUtil.printHeader("Step 7: 完整系统集成测试");

        // ──────────────────────────────────────────
        // 1. 查看系统初始状态
        // ──────────────────────────────────────────
        PrintUtil.printSection("1. 系统初始状态");
        statisticsService.printSystemOverview();
        System.out.println();

        // ──────────────────────────────────────────
        // 2. 新建用户
        // ──────────────────────────────────────────
        PrintUtil.printSection("2. 新建用户");

        User newUser = new User();
        newUser.setName("赵六");
        newUser.setPhone("13600136000");
        newUser.setBankCard("6222000099998888");
        newUser.setBankName("招商银行");
        int newUserId = userDao.addUser(newUser);

        if (newUserId > 0) {
            System.out.println("✓ 新增用户成功：赵六，ID = " + newUserId);
        } else {
            // 如果已存在（重复跑测试），则查找已有记录
            System.out.println("  用户已存在，查找已有记录...");
            User existing = userDao.getUserByPhone("13600136000");
            if (existing != null) {
                newUserId = existing.getId();
                System.out.println("  找到已有用户：ID = " + newUserId);
            }
        }

        // ──────────────────────────────────────────
        // 3. 新建账户并充值
        // ──────────────────────────────────────────
        PrintUtil.printSection("3. 新建账户并充值");

        Account newAccount = new Account();
        newAccount.setName("年终奖账户");
        newAccount.setBalance(new BigDecimal("0.00"));
        int newAccountId = accountDao.createAccount(newAccount);

        if (newAccountId > 0) {
            System.out.println("✓ 账户创建成功：年终奖账户，ID = " + newAccountId);
        } else {
            // 重复测试时账户可能已存在，取已有的第一个账户
            List<Account> accounts = accountDao.getAllAccounts();
            newAccountId = accounts.get(0).getId();
            System.out.println("  使用已有账户 ID = " + newAccountId);
        }

        // 充值 50000 元
        boolean recharged = accountDao.recharge(newAccountId, new BigDecimal("50000.00"));
        if (recharged) {
            Account a = accountDao.getAccountById(newAccountId);
            System.out.println("✓ 充值成功，当前余额：" + a.getBalance() + " 元");
        }
        System.out.println();

        // ──────────────────────────────────────────
        // 4. 批量发钱（给所有活跃用户）
        // ──────────────────────────────────────────
        PrintUtil.printSection("4. 批量发钱给所有活跃用户");

        List<User> activeUsers = userDao.getAllUsers();
        System.out.println("当前活跃用户数：" + activeUsers.size() + " 人");

        BigDecimal payAmount = new BigDecimal("888.00");
        int successCount = 0;

        for (User u : activeUsers) {
            try {
                int pid = paymentService.sendMoney(
                    newAccountId, u.getId(), payAmount, "年终奖发放");
                System.out.println("  ✓ 给 " + u.getName() + " 发钱 888 元，记录ID=" + pid);
                successCount++;
            } catch (InsufficientBalanceException e) {
                System.out.println("  ✗ " + u.getName() + " 余额不足：" + e.getMessage());
            } catch (AccountFrozenException e) {
                System.out.println("  ✗ " + u.getName() + " 账户冻结：" + e.getMessage());
            } catch (InvalidAmountException | SQLException e) {
                System.out.println("  ✗ " + u.getName() + " 失败：" + e.getMessage());
            }
        }

        System.out.println();
        System.out.println("发钱结果：" + successCount + " / " + activeUsers.size() + " 人成功");
        Account afterPay = accountDao.getAccountById(newAccountId);
        System.out.println("账户剩余余额：" + afterPay.getBalance() + " 元");
        System.out.println();

        // ──────────────────────────────────────────
        // 5. 异常场景测试
        // ──────────────────────────────────────────
        PrintUtil.printSection("5. 异常场景测试");

        // 5a. 余额不足
        try {
            paymentService.sendMoney(newAccountId, activeUsers.get(0).getId(),
                new BigDecimal("9999999.00"), "测试余额不足");
            System.out.println("✗ 未预期：应抛出余额不足异常");
        } catch (InsufficientBalanceException e) {
            System.out.println("✓ 余额不足异常正确捕获：" + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ 异常类型错误：" + e.getClass().getSimpleName());
        }

        // 5b. 冻结账户后发钱
        accountDao.freezeAccount(newAccountId);
        try {
            paymentService.sendMoney(newAccountId, activeUsers.get(0).getId(),
                new BigDecimal("100.00"), "测试冻结");
            System.out.println("✗ 未预期：应抛出账户冻结异常");
        } catch (AccountFrozenException e) {
            System.out.println("✓ 账户冻结异常正确捕获：" + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ 异常类型错误：" + e.getClass().getSimpleName());
        }
        accountDao.unfreezeAccount(newAccountId);
        System.out.println("  账户已解冻");
        System.out.println();

        // ──────────────────────────────────────────
        // 6. 查询发钱记录
        // ──────────────────────────────────────────
        PrintUtil.printSection("6. 查询发钱记录");

        var records = paymentRecordDao.getPaymentRecordsByAccountId(newAccountId);
        System.out.println("账户 [年终奖账户] 共有 " + records.size() + " 条发钱记录：");
        for (var r : records) {
            System.out.printf("  ID=%-3d  用户ID=%-3d  金额=%-10s  状态=%-12s  备注=%s%n",
                r.getId(), r.getUserId(), r.getAmount(), r.getStatus(), r.getRemark());
        }
        System.out.println();

        // ──────────────────────────────────────────
        // 7. 最终统计报表
        // ──────────────────────────────────────────
        PrintUtil.printSection("7. 最终统计报表");

        statisticsService.printSystemOverview();
        System.out.println();
        statisticsService.printAccountPaymentSummary();
        System.out.println();
        statisticsService.printUserReceiptSummary();
        System.out.println();
        statisticsService.printTopReceivers(5);
        System.out.println();

        // ──────────────────────────────────────────
        // 完成总结
        // ──────────────────────────────────────────
        PrintUtil.printSection("系统整合总结");
        System.out.println("  整个项目完整架构：");
        System.out.println();
        System.out.println("  ┌─────────────────────────────────────────┐");
        System.out.println("  │  Main.java (交互式命令行界面)            │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │  Service 层（业务逻辑）                  │");
        System.out.println("  │    PaymentService    - 发钱（事务）      │");
        System.out.println("  │    StatisticsService - 统计报表          │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │  DAO 层（数据访问）                      │");
        System.out.println("  │    UserDao           - 用户 CRUD         │");
        System.out.println("  │    AccountDao        - 账户 + 充值事务   │");
        System.out.println("  │    PaymentRecordDao  - 发钱记录查询      │");
        System.out.println("  │    StatisticsDao     - JOIN 统计查询     │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │  Entity 层（数据模型）                   │");
        System.out.println("  │    User / Account / PaymentRecord 等     │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │  Exception 层（自定义异常）              │");
        System.out.println("  │    InsufficientBalanceException          │");
        System.out.println("  │    AccountFrozenException                │");
        System.out.println("  │    InvalidAmountException                │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │  Util 层（工具类）                       │");
        System.out.println("  │    DBUtil    - 数据库连接管理            │");
        System.out.println("  │    PrintUtil - 控制台美化输出            │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │  MySQL Database                         │");
        System.out.println("  │    users / accounts                     │");
        System.out.println("  │    payment_records / recharge_records   │");
        System.out.println("  └─────────────────────────────────────────┘");
        System.out.println();
        System.out.println("  所有 Step 已完成！项目完成度：100% ✅");
        System.out.println();
        System.out.println("  运行交互系统：");
        System.out.println("    java -cp .:mysql-connector-java-8.0.33.jar moneyTransfer.Main");

        PrintUtil.printFooter("Step 7 完成！整个项目 100% 收工！🎉");
    }
}
