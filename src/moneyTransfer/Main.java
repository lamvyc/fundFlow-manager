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
import moneyTransfer.service.StatisticsService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * 发钱管理系统 - 主程序入口
 *
 * ⭐⭐⭐ Step 7: 完整系统整合 ⭐⭐⭐
 *
 * 本程序将前 6 个 Step 学到的所有知识整合到一个
 * 交互式命令行系统中，涵盖：
 *
 * - 用户管理：增删改查
 * - 账户管理：创建、充值、冻结/解冻
 * - 发钱操作：发钱、查询记录
 * - 统计报表：各类汇总统计
 *
 * 系统架构：
 *   用户界面（Main.java）
 *       ↓ 调用
 *   Service 层（业务逻辑：PaymentService, StatisticsService）
 *       ↓ 调用
 *   DAO 层（数据访问：UserDao, AccountDao, PaymentRecordDao, StatisticsDao）
 *       ↓ 操作
 *   数据库（MySQL: money_transfer_db）
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final UserDao userDao                   = new UserDao();
    private static final AccountDao accountDao             = new AccountDao();
    private static final PaymentRecordDao paymentRecordDao = new PaymentRecordDao();
    private static final PaymentService paymentService     = new PaymentService();
    private static final StatisticsService statisticsService = new StatisticsService();

    // =====================================================================
    // 主入口
    // =====================================================================

    public static void main(String[] args) {
        printBanner();
        while (true) {
            printMainMenu();
            int choice = readInt("请输入选项");
            System.out.println();
            switch (choice) {
                case 1: userManagementMenu();    break;
                case 2: accountManagementMenu(); break;
                case 3: paymentMenu();           break;
                case 4: statisticsMenu();        break;
                case 0:
                    System.out.println("再见！感谢使用发钱管理系统。");
                    scanner.close();
                    return;
                default:
                    System.out.println("无效选项，请重新输入。");
            }
        }
    }

    // =====================================================================
    // 一、用户管理
    // =====================================================================

    private static void userManagementMenu() {
        while (true) {
            System.out.println("┌─────────────────────────┐");
            System.out.println("│  用户管理                │");
            System.out.println("├─────────────────────────┤");
            System.out.println("│  1. 查看所有用户         │");
            System.out.println("│  2. 新增用户             │");
            System.out.println("│  3. 修改用户信息         │");
            System.out.println("│  4. 删除用户（软删除）   │");
            System.out.println("│  0. 返回上级菜单         │");
            System.out.println("└─────────────────────────┘");

            int choice = readInt("请输入选项");
            System.out.println();
            switch (choice) {
                case 1: listUsers();       break;
                case 2: addUser();         break;
                case 3: updateUser();      break;
                case 4: deleteUser();      break;
                case 0: return;
                default: System.out.println("无效选项");
            }
        }
    }

    private static void listUsers() {
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("暂无用户数据。");
        } else {
            System.out.printf("  %-4s %-12s %-14s %-20s %-12s%n",
                "ID", "姓名", "手机号", "银行卡号", "开户行");
            System.out.println("  " + "-".repeat(66));
            for (User u : users) {
                System.out.printf("  %-4d %-12s %-14s %-20s %-12s%n",
                    u.getId(), u.getName(), u.getPhone(),
                    u.getBankCard(), u.getBankName());
            }
        }
        System.out.println();
    }

    private static void addUser() {
        System.out.println("=== 新增用户 ===");
        String name     = readString("姓名");
        String phone    = readString("手机号");
        String bankCard = readString("银行卡号");
        String bankName = readString("开户行");

        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setBankCard(bankCard);
        user.setBankName(bankName);

        int id = userDao.addUser(user);
        if (id > 0) {
            System.out.println("✓ 新增用户成功，ID = " + id);
        } else {
            System.out.println("✗ 新增用户失败（手机号或银行卡号可能已存在）");
        }
        System.out.println();
    }

    private static void updateUser() {
        System.out.println("=== 修改用户信息 ===");
        int id = readInt("用户ID");
        User user = userDao.getUserById(id);
        if (user == null) {
            System.out.println("✗ 用户不存在");
            System.out.println();
            return;
        }
        System.out.println("当前信息：姓名=" + user.getName()
            + "  手机号=" + user.getPhone()
            + "  开户行=" + user.getBankName());

        String name     = readString("新姓名（回车保持不变）", user.getName());
        String phone    = readString("新手机号（回车保持不变）", user.getPhone());
        String bankName = readString("新开户行（回车保持不变）", user.getBankName());

        user.setName(name);
        user.setPhone(phone);
        user.setBankName(bankName);

        if (userDao.updateUser(user)) {
            System.out.println("✓ 修改成功");
        } else {
            System.out.println("✗ 修改失败");
        }
        System.out.println();
    }

    private static void deleteUser() {
        System.out.println("=== 删除用户（软删除）===");
        int id = readInt("用户ID");
        User user = userDao.getUserById(id);
        if (user == null) {
            System.out.println("✗ 用户不存在");
            System.out.println();
            return;
        }
        System.out.println("即将删除：" + user.getName() + "（" + user.getPhone() + "）");
        String confirm = readString("确认删除？(y/n)");
        if ("y".equalsIgnoreCase(confirm.trim())) {
            if (userDao.deleteUser(id)) {
                System.out.println("✓ 删除成功（软删除，数据仍保留在数据库中）");
            } else {
                System.out.println("✗ 删除失败");
            }
        } else {
            System.out.println("已取消");
        }
        System.out.println();
    }

    // =====================================================================
    // 二、账户管理
    // =====================================================================

    private static void accountManagementMenu() {
        while (true) {
            System.out.println("┌──────────────────────────┐");
            System.out.println("│  账户管理                 │");
            System.out.println("├──────────────────────────┤");
            System.out.println("│  1. 查看所有账户          │");
            System.out.println("│  2. 新建账户              │");
            System.out.println("│  3. 账户充值              │");
            System.out.println("│  4. 冻结账户              │");
            System.out.println("│  5. 解冻账户              │");
            System.out.println("│  0. 返回上级菜单          │");
            System.out.println("└──────────────────────────┘");

            int choice = readInt("请输入选项");
            System.out.println();
            switch (choice) {
                case 1: listAccounts();    break;
                case 2: createAccount();   break;
                case 3: rechargeAccount(); break;
                case 4: freezeAccount();   break;
                case 5: unfreezeAccount(); break;
                case 0: return;
                default: System.out.println("无效选项");
            }
        }
    }

    private static void listAccounts() {
        List<Account> accounts = accountDao.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("暂无账户数据。");
        } else {
            System.out.printf("  %-4s %-20s %14s %-10s%n",
                "ID", "账户名称", "余额", "状态");
            System.out.println("  " + "-".repeat(52));
            for (Account a : accounts) {
                System.out.printf("  %-4d %-20s %14.2f %-10s%n",
                    a.getId(), a.getName(), a.getBalance(), a.getStatus());
            }
        }
        System.out.println();
    }

    private static void createAccount() {
        System.out.println("=== 新建账户 ===");
        String name           = readString("账户名称");
        BigDecimal initBalance = readBigDecimal("初始余额（元）", BigDecimal.ZERO);

        Account account = new Account();
        account.setName(name);
        account.setBalance(initBalance);

        int id = accountDao.createAccount(account);
        if (id > 0) {
            System.out.println("✓ 账户创建成功，ID = " + id);
        } else {
            System.out.println("✗ 账户创建失败");
        }
        System.out.println();
    }

    private static void rechargeAccount() {
        System.out.println("=== 账户充值 ===");
        listAccounts();
        int id = readInt("账户ID");
        Account account = accountDao.getAccountById(id);
        if (account == null) {
            System.out.println("✗ 账户不存在");
            System.out.println();
            return;
        }
        System.out.println("当前余额：" + account.getBalance() + " 元");
        BigDecimal amount = readBigDecimal("充值金额（元）", null);

        if (accountDao.recharge(id, amount)) {
            Account updated = accountDao.getAccountById(id);
            System.out.println("✓ 充值成功，当前余额：" + updated.getBalance() + " 元");
        } else {
            System.out.println("✗ 充值失败");
        }
        System.out.println();
    }

    private static void freezeAccount() {
        System.out.println("=== 冻结账户 ===");
        listAccounts();
        int id = readInt("账户ID");
        if (accountDao.freezeAccount(id)) {
            System.out.println("✓ 账户已冻结");
        } else {
            System.out.println("✗ 操作失败");
        }
        System.out.println();
    }

    private static void unfreezeAccount() {
        System.out.println("=== 解冻账户 ===");
        listAccounts();
        int id = readInt("账户ID");
        if (accountDao.unfreezeAccount(id)) {
            System.out.println("✓ 账户已解冻");
        } else {
            System.out.println("✗ 操作失败");
        }
        System.out.println();
    }

    // =====================================================================
    // 三、发钱操作
    // =====================================================================

    private static void paymentMenu() {
        while (true) {
            System.out.println("┌────────────────────────┐");
            System.out.println("│  发钱操作              │");
            System.out.println("├────────────────────────┤");
            System.out.println("│  1. 发起发钱           │");
            System.out.println("│  2. 查看账户发钱记录   │");
            System.out.println("│  3. 查看用户收款记录   │");
            System.out.println("│  0. 返回上级菜单       │");
            System.out.println("└────────────────────────┘");

            int choice = readInt("请输入选项");
            System.out.println();
            switch (choice) {
                case 1: sendMoney();               break;
                case 2: listPaymentsByAccount();   break;
                case 3: listPaymentsByUser();      break;
                case 0: return;
                default: System.out.println("无效选项");
            }
        }
    }

    private static void sendMoney() {
        System.out.println("=== 发起发钱 ===");
        listAccounts();
        int accountId = readInt("发钱账户ID");
        System.out.println();
        listUsers();
        int userId    = readInt("收款用户ID");
        BigDecimal amount = readBigDecimal("发钱金额（元）", null);
        String remark = readString("备注（回车跳过）", "");

        try {
            int paymentId = paymentService.sendMoney(accountId, userId, amount, remark);
            System.out.println("✓ 发钱成功，记录ID = " + paymentId);
        } catch (InvalidAmountException e) {
            System.out.println("✗ 无效金额：" + e.getMessage());
        } catch (InsufficientBalanceException e) {
            System.out.println("✗ 余额不足：" + e.getMessage());
        } catch (AccountFrozenException e) {
            System.out.println("✗ 账户冻结：" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("✗ 系统错误：" + e.getMessage());
        }
        System.out.println();
    }

    private static void listPaymentsByAccount() {
        System.out.println("=== 账户发钱记录 ===");
        listAccounts();
        int accountId = readInt("账户ID");
        List<PaymentRecord> records = paymentRecordDao.getPaymentRecordsByAccountId(accountId);
        printPaymentRecords(records);
    }

    private static void listPaymentsByUser() {
        System.out.println("=== 用户收款记录 ===");
        listUsers();
        int userId = readInt("用户ID");
        List<PaymentRecord> records = paymentRecordDao.getPaymentRecordsByUserId(userId);
        printPaymentRecords(records);
    }

    private static void printPaymentRecords(List<PaymentRecord> records) {
        if (records.isEmpty()) {
            System.out.println("暂无记录。");
        } else {
            System.out.printf("  %-4s %-6s %-6s %12s %-12s %-12s%n",
                "ID", "账户ID", "用户ID", "金额", "状态", "时间");
            System.out.println("  " + "-".repeat(58));
            for (PaymentRecord r : records) {
                System.out.printf("  %-4d %-6d %-6d %12.2f %-12s %-12s%n",
                    r.getId(), r.getAccountId(), r.getUserId(),
                    r.getAmount(), r.getStatus(),
                    r.getCreateTime() != null ? r.getCreateTime().toString().substring(0, 16) : "-");
            }
        }
        System.out.println();
    }

    // =====================================================================
    // 四、统计报表
    // =====================================================================

    private static void statisticsMenu() {
        while (true) {
            System.out.println("┌─────────────────────────┐");
            System.out.println("│  统计报表               │");
            System.out.println("├─────────────────────────┤");
            System.out.println("│  1. 系统整体概览        │");
            System.out.println("│  2. 账户发钱汇总        │");
            System.out.println("│  3. 用户收款汇总        │");
            System.out.println("│  4. 发钱明细            │");
            System.out.println("│  5. 收款 TOP 3 用户     │");
            System.out.println("│  6. 发钱状态分布        │");
            System.out.println("│  7. 充值汇总            │");
            System.out.println("│  0. 返回上级菜单        │");
            System.out.println("└─────────────────────────┘");

            int choice = readInt("请输入选项");
            System.out.println();
            switch (choice) {
                case 1: statisticsService.printSystemOverview();          break;
                case 2: statisticsService.printAccountPaymentSummary();   break;
                case 3: statisticsService.printUserReceiptSummary();      break;
                case 4: statisticsService.printPaymentDetails();          break;
                case 5: statisticsService.printTopReceivers(3);           break;
                case 6: statisticsService.printPaymentCountByStatus();    break;
                case 7: statisticsService.printRechargesSummary();        break;
                case 0: return;
                default: System.out.println("无效选项");
            }
            System.out.println();
        }
    }

    // =====================================================================
    // 工具方法：Banner / 菜单 / 输入
    // =====================================================================

    private static void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║          发 钱 管 理 系 统  v1.0             ║");
        System.out.println("║      fundFlow-manager  (Step 7 完整版)       ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMainMenu() {
        System.out.println("┌─────────────────────────┐");
        System.out.println("│  主菜单                 │");
        System.out.println("├─────────────────────────┤");
        System.out.println("│  1. 用户管理            │");
        System.out.println("│  2. 账户管理            │");
        System.out.println("│  3. 发钱操作            │");
        System.out.println("│  4. 统计报表            │");
        System.out.println("│  0. 退出系统            │");
        System.out.println("└─────────────────────────┘");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("请输入数字！");
            }
        }
    }

    private static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    private static String readString(String prompt, String defaultValue) {
        System.out.print(prompt + ": ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    private static BigDecimal readBigDecimal(String prompt, BigDecimal defaultValue) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty() && defaultValue != null) return defaultValue;
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("金额必须大于 0，请重新输入！");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字（例如：1000.00）！");
            }
        }
    }
}
