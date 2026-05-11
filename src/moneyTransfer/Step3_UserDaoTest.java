package moneyTransfer;

import moneyTransfer.dao.UserDao;
import moneyTransfer.entity.User;
import moneyTransfer.util.PrintUtil;

import java.util.List;

/**
 * Step 3: 用户管理测试
 * 
 * 学习目标：
 * 1. 理解 DAO 设计模式
 * 2. 掌握 JDBC 的 CRUD 操作
 * 3. 理解 PreparedStatement 的使用
 * 4. 学会处理 ResultSet 结果集
 * 
 * 运行方式：
 * cd /Users/unravel/fundFlow-manager
 * ./run.sh
 * 选择 4) Step3: 用户管理测试
 * 
 * ⚠️ 运行前确保：
 * 1. MySQL 服务已启动
 * 2. 已执行 sql/init.sql 创建数据库和表
 */
public class Step3_UserDaoTest {
    
    public static void main(String[] args) {
        PrintUtil.title("Step 3: 用户管理（CRUD 操作）");
        
        UserDao userDao = new UserDao();
        
        // 1. 测试添加用户
        testAddUser(userDao);
        
        // 2. 测试根据 ID 查询用户
        testGetUserById(userDao);
        
        // 3. 测试根据手机号查询用户
        testGetUserByPhone(userDao);
        
        // 4. 测试查询所有用户
        testGetAllUsers(userDao);
        
        // 5. 测试更新用户
        testUpdateUser(userDao);
        
        // 6. 测试删除用户
        testDeleteUser(userDao);
        
        // 7. 再次查询所有用户（验证删除效果）
        testGetAllUsers(userDao);
        
        PrintUtil.success("\n✓ Step 3 完成！所有 CRUD 操作测试通过");
        PrintUtil.info("\n你已经掌握：");
        PrintUtil.info("  ✓ JDBC 连接管理");
        PrintUtil.info("  ✓ PreparedStatement 使用");
        PrintUtil.info("  ✓ ResultSet 结果集处理");
        PrintUtil.info("  ✓ DAO 设计模式");
        PrintUtil.info("\n下一步：Step 4 - 账户管理（学习事务处理）");
    }
    
    /**
     * 测试添加用户
     */
    private static void testAddUser(UserDao userDao) {
        PrintUtil.subtitle("1. 测试添加用户（INSERT）");
        
        // 创建新用户
        User newUser = new User("赵六", "13600136000", "6222000099998888", "招商银行");
        
        PrintUtil.info("准备添加用户：");
        System.out.println("  姓名: " + newUser.getName());
        System.out.println("  手机: " + newUser.getPhone());
        System.out.println("  银行卡: " + newUser.getBankCard());
        System.out.println("  开户行: " + newUser.getBankName());
        
        // 调用 DAO 方法
        int userId = userDao.addUser(newUser);
        
        if (userId > 0) {
            PrintUtil.success("✓ 添加成功！生成的用户ID: " + userId);
        } else {
            PrintUtil.error("✗ 添加失败");
        }
        System.out.println();
    }
    
    /**
     * 测试根据 ID 查询用户
     */
    private static void testGetUserById(UserDao userDao) {
        PrintUtil.subtitle("2. 测试根据 ID 查询用户（SELECT）");
        
        int userId = 1;  // 查询 ID=1 的用户（张三）
        PrintUtil.info("查询用户 ID = " + userId);
        
        User user = userDao.getUserById(userId);
        
        if (user != null) {
            PrintUtil.success("✓ 查询成功：");
            printUserInfo(user);
        } else {
            PrintUtil.error("✗ 用户不存在");
        }
        System.out.println();
    }
    
    /**
     * 测试根据手机号查询用户
     */
    private static void testGetUserByPhone(UserDao userDao) {
        PrintUtil.subtitle("3. 测试根据手机号查询用户（SELECT）");
        
        String phone = "13900139000";  // 李四的手机号
        PrintUtil.info("查询手机号 = " + phone);
        
        User user = userDao.getUserByPhone(phone);
        
        if (user != null) {
            PrintUtil.success("✓ 查询成功：");
            printUserInfo(user);
        } else {
            PrintUtil.error("✗ 用户不存在");
        }
        System.out.println();
    }
    
    /**
     * 测试查询所有用户
     */
    private static void testGetAllUsers(UserDao userDao) {
        PrintUtil.subtitle("4. 测试查询所有用户（SELECT）");
        
        List<User> users = userDao.getAllUsers();
        
        PrintUtil.success("✓ 查询成功，共找到 " + users.size() + " 个用户：");
        System.out.println();
        
        // 打印表头
        System.out.printf("%-5s %-10s %-15s %-20s %-15s %-10s%n", 
            "ID", "姓名", "手机号", "银行卡号", "开户行", "状态");
        System.out.println("-".repeat(80));
        
        // 打印每个用户
        for (User user : users) {
            System.out.printf("%-5d %-10s %-15s %-20s %-15s %-10s%n",
                user.getId(),
                user.getName(),
                user.getPhone(),
                maskBankCard(user.getBankCard()),
                user.getBankName(),
                user.getStatus()
            );
        }
        System.out.println();
    }
    
    /**
     * 测试更新用户
     */
    private static void testUpdateUser(UserDao userDao) {
        PrintUtil.subtitle("5. 测试更新用户（UPDATE）");
        
        // 先查询用户
        int userId = 1;
        User user = userDao.getUserById(userId);
        
        if (user == null) {
            PrintUtil.error("✗ 用户不存在");
            return;
        }
        
        PrintUtil.info("更新前的信息：");
        printUserInfo(user);
        
        // 修改信息
        user.setName("张三（已更新）");
        user.setPhone("13800138001");  // 修改手机号
        user.setBankName("工商银行（北京分行）");
        
        PrintUtil.info("\n正在更新用户信息...");
        boolean success = userDao.updateUser(user);
        
        if (success) {
            PrintUtil.success("✓ 更新成功");
            
            // 再次查询验证
            User updatedUser = userDao.getUserById(userId);
            PrintUtil.info("\n更新后的信息：");
            printUserInfo(updatedUser);
        } else {
            PrintUtil.error("✗ 更新失败");
        }
        System.out.println();
    }
    
    /**
     * 测试删除用户（软删除）
     */
    private static void testDeleteUser(UserDao userDao) {
        PrintUtil.subtitle("6. 测试删除用户（软删除）");
        
        int userId = 3;  // 删除王五
        PrintUtil.info("准备删除用户 ID = " + userId);
        
        // 先查询用户
        User user = userDao.getUserById(userId);
        if (user != null) {
            PrintUtil.info("删除前的信息：");
            printUserInfo(user);
        }
        
        // 删除用户
        PrintUtil.info("\n正在删除用户...");
        boolean success = userDao.deleteUser(userId);
        
        if (success) {
            PrintUtil.success("✓ 删除成功（状态已标记为 DELETED）");
            
            // 再次查询验证
            User deletedUser = userDao.getUserById(userId);
            if (deletedUser != null) {
                PrintUtil.info("\n删除后的状态: " + deletedUser.getStatus());
            }
        } else {
            PrintUtil.error("✗ 删除失败");
        }
        System.out.println();
    }
    
    /**
     * 打印用户详细信息
     */
    private static void printUserInfo(User user) {
        System.out.println("  ID: " + user.getId());
        System.out.println("  姓名: " + user.getName());
        System.out.println("  手机: " + user.getPhone());
        System.out.println("  银行卡: " + maskBankCard(user.getBankCard()));
        System.out.println("  开户行: " + user.getBankName());
        System.out.println("  状态: " + user.getStatus());
        System.out.println("  创建时间: " + user.getCreateTime());
        System.out.println("  更新时间: " + user.getUpdateTime());
    }
    
    /**
     * 银行卡号脱敏显示
     */
    private static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard;
        }
        String prefix = bankCard.substring(0, 4);
        String suffix = bankCard.substring(bankCard.length() - 4);
        return prefix + "****" + suffix;
    }
}
