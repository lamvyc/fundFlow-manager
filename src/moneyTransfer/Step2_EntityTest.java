package moneyTransfer;

import moneyTransfer.entity.*;
import moneyTransfer.util.PrintUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Step 2: 实体类测试
 * 
 * 学习目标：
 * 1. 理解什么是实体类
 * 2. 掌握 getter/setter 的使用
 * 3. 理解构造函数的作用
 * 4. 学会使用 BigDecimal 处理金额
 * 
 * 运行方式：
 * cd /Users/unravel/fundFlow-manager
 * ./run.sh
 * 选择 3) Step2: 实体类测试
 */
public class Step2_EntityTest {
    
    public static void main(String[] args) {
        PrintUtil.title("Step 2: 实体类测试");
        
        // 测试 User 实体类
        testUser();
        
        // 测试 Account 实体类
        testAccount();
        
        // 测试 PaymentRecord 实体类
        testPaymentRecord();
        
        // 测试 RechargeRecord 实体类
        testRechargeRecord();
        
        PrintUtil.success("\n✓ Step 2 完成！所有实体类测试通过");
        PrintUtil.info("\n下一步：Step 3 - 用户管理（实现 CRUD 操作）");
    }
    
    /**
     * 测试 User 实体类
     */
    private static void testUser() {
        PrintUtil.subtitle("1. 测试 User 实体类");
        
        // 方式1：使用无参构造函数 + setter
        User user1 = new User();
        user1.setId(1);
        user1.setName("张三");
        user1.setPhone("13800138000");
        user1.setBankCard("6222000012345678");
        user1.setBankName("工商银行");
        user1.setStatus("ACTIVE");
        user1.setCreateTime(new Timestamp(System.currentTimeMillis()));
        
        PrintUtil.info("方式1 - 使用 setter 创建对象：");
        System.out.println(user1);
        System.out.println();
        
        // 方式2：使用常用字段构造函数
        User user2 = new User("李四", "13900139000", "6222000087654321", "建设银行");
        
        PrintUtil.info("方式2 - 使用构造函数创建对象：");
        System.out.println(user2);
        System.out.println();
        
        // 方式3：使用全参构造函数
        User user3 = new User(
            3, 
            "王五", 
            "13700137000", 
            "6222000011112222", 
            "农业银行",
            "ACTIVE",
            new Timestamp(System.currentTimeMillis()),
            new Timestamp(System.currentTimeMillis())
        );
        
        PrintUtil.info("方式3 - 使用全参构造函数：");
        System.out.println(user3);
        System.out.println();
        
        // 测试 getter 方法
        PrintUtil.info("测试 Getter 方法：");
        System.out.println("用户ID: " + user1.getId());
        System.out.println("用户姓名: " + user1.getName());
        System.out.println("手机号: " + user1.getPhone());
        System.out.println("银行卡号: " + user1.getBankCard());
        System.out.println("开户行: " + user1.getBankName());
        System.out.println("状态: " + user1.getStatus());
        System.out.println("是否激活: " + user1.isActive());
        System.out.println();
    }
    
    /**
     * 测试 Account 实体类
     */
    private static void testAccount() {
        PrintUtil.subtitle("2. 测试 Account 实体类");
        
        // 创建账户（注意：金额使用 BigDecimal）
        Account account1 = new Account("公司发薪账户", new BigDecimal("100000.00"));
        account1.setId(1);
        account1.setCreateTime(new Timestamp(System.currentTimeMillis()));
        
        PrintUtil.info("创建账户：");
        System.out.println(account1);
        System.out.println();
        
        // 测试金额计算（BigDecimal 的使用）
        PrintUtil.info("测试 BigDecimal 金额计算：");
        BigDecimal balance = account1.getBalance();
        System.out.println("原始余额: " + balance);
        
        // 加法
        BigDecimal addAmount = new BigDecimal("5000.00");
        BigDecimal newBalance1 = balance.add(addAmount);
        System.out.println("充值 " + addAmount + " 后余额: " + newBalance1);
        
        // 减法
        BigDecimal subtractAmount = new BigDecimal("3000.00");
        BigDecimal newBalance2 = balance.subtract(subtractAmount);
        System.out.println("发钱 " + subtractAmount + " 后余额: " + newBalance2);
        
        // 比较
        System.out.println("余额是否充足（需要 50000）: " + account1.hasEnoughBalance(new BigDecimal("50000")));
        System.out.println("余额是否充足（需要 200000）: " + account1.hasEnoughBalance(new BigDecimal("200000")));
        System.out.println();
        
        // ⚠️ 为什么不用 double？
        PrintUtil.warning("为什么金额不用 double？看这个例子：");
        double d1 = 0.1;
        double d2 = 0.2;
        double d3 = d1 + d2;
        System.out.println("double 计算: 0.1 + 0.2 = " + d3);  // 输出: 0.30000000000000004
        System.out.println("期望结果: 0.3");
        System.out.println("结论: double 有精度误差，金额计算必须用 BigDecimal！");
        System.out.println();
    }
    
    /**
     * 测试 PaymentRecord 实体类
     */
    private static void testPaymentRecord() {
        PrintUtil.subtitle("3. 测试 PaymentRecord 实体类");
        
        // 创建发钱记录
        PaymentRecord record = new PaymentRecord(
            1,  // 账户ID
            1,  // 用户ID
            new BigDecimal("5000.00"),  // 金额
            "工资发放"  // 备注
        );
        record.setId(1);
        record.setCreateTime(new Timestamp(System.currentTimeMillis()));
        
        PrintUtil.info("创建发钱记录：");
        System.out.println(record);
        System.out.println();
        
        // 测试状态常量
        PrintUtil.info("测试状态判断：");
        System.out.println("当前状态: " + record.getStatus());
        System.out.println("是否待处理: " + record.isPending());
        System.out.println("是否已完成: " + record.isCompleted());
        System.out.println("是否已取消: " + record.isCancelled());
        System.out.println();
        
        // 修改状态
        record.setStatus(PaymentRecord.STATUS_COMPLETED);
        PrintUtil.info("修改状态为已完成：");
        System.out.println("新状态: " + record.getStatus());
        System.out.println("是否已完成: " + record.isCompleted());
        System.out.println();
    }
    
    /**
     * 测试 RechargeRecord 实体类
     */
    private static void testRechargeRecord() {
        PrintUtil.subtitle("4. 测试 RechargeRecord 实体类");
        
        // 创建充值记录
        BigDecimal beforeBalance = new BigDecimal("100000.00");
        BigDecimal rechargeAmount = new BigDecimal("50000.00");
        BigDecimal afterBalance = beforeBalance.add(rechargeAmount);
        
        RechargeRecord record = new RechargeRecord(
            1,  // 账户ID
            rechargeAmount,
            beforeBalance,
            afterBalance
        );
        record.setId(1);
        record.setCreateTime(new Timestamp(System.currentTimeMillis()));
        
        PrintUtil.info("创建充值记录：");
        System.out.println(record);
        System.out.println();
        
        PrintUtil.info("充值详情：");
        System.out.println("充值前余额: " + record.getBeforeBalance());
        System.out.println("充值金额: " + record.getAmount());
        System.out.println("充值后余额: " + record.getAfterBalance());
        System.out.println();
    }
}
