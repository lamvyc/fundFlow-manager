package moneyTransfer.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具类
 * 作用：管理数据库连接的获取和关闭
 * 
 * 为什么需要这个类？
 * - 统一管理数据库配置（避免配置散落在各处）
 * - 简化连接获取过程（其他类只需调用 getConnection()）
 * - 便于后续扩展（如添加连接池）
 */
public class DBUtil {
    
    // 数据库配置信息（从配置文件读取）
    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    
    // 静态代码块：类加载时自动执行（只执行一次）
    // 用途：初始化数据库配置信息
    static {
        try {
            // 1. 创建 Properties 对象（用于读取配置文件）
            Properties props = new Properties();
            
            // 2. 读取配置文件（db.properties）
            // 注意：路径相对于项目根目录
            props.load(new FileInputStream("config/db.properties"));
            
            // 3. 从配置文件中获取数据库信息
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");
            driver = props.getProperty("db.driver");
            
            // 4. 加载数据库驱动（MySQL 8.0 会自动加载，这里为了兼容性保留）
            Class.forName(driver);
            
            System.out.println("✓ 数据库驱动加载成功");
            
        } catch (IOException e) {
            System.err.println("✗ 配置文件读取失败: " + e.getMessage());
            System.err.println("请检查 config/db.properties 文件是否存在");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ 数据库驱动加载失败: " + e.getMessage());
            System.err.println("请检查是否添加了 MySQL 驱动依赖");
        }
    }
    
    /**
     * 获取数据库连接
     * 
     * 使用示例：
     * Connection conn = DBUtil.getConnection();
     * 
     * @return 数据库连接对象
     * @throws SQLException 连接失败时抛出异常
     */
    public static Connection getConnection() throws SQLException {
        // 建立数据库连接
        // DriverManager.getConnection() 会根据 url 连接到指定的数据库
        return DriverManager.getConnection(url, username, password);
    }
    
    /**
     * 关闭数据库连接
     * 
     * 为什么需要关闭连接？
     * - 数据库连接是有限资源（MySQL 默认最多 151 个连接）
     * - 不关闭会导致连接泄漏，最终无法创建新连接
     * 
     * 使用示例：
     * Connection conn = null;
     * try {
     *     conn = DBUtil.getConnection();
     *     // 使用连接...
     * } finally {
     *     DBUtil.closeConnection(conn);
     * }
     * 
     * @param conn 要关闭的连接
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("✗ 关闭连接失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 测试数据库连接是否正常
     * 
     * 运行方式：
     * cd /Users/unravel/fundFlow-manager
     * javac -d . -encoding UTF-8 src/moneyTransfer/util/DBUtil.java
     * java moneyTransfer.util.DBUtil
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  测试数据库连接");
        System.out.println("========================================\n");
        
        Connection conn = null;
        try {
            // 尝试获取连接
            System.out.println("正在连接数据库...");
            conn = getConnection();
            
            // 连接成功
            System.out.println("✓ 数据库连接成功！");
            System.out.println("  数据库地址: " + url);
            System.out.println("  用户名: " + username);
            
        } catch (SQLException e) {
            // 连接失败
            System.err.println("\n✗ 数据库连接失败！");
            System.err.println("错误信息: " + e.getMessage());
            System.err.println("\n可能的原因：");
            System.err.println("1. MySQL 服务未启动");
            System.err.println("2. 数据库名称错误（money_transfer_db 不存在）");
            System.err.println("3. 用户名或密码错误");
            System.err.println("4. 数据库地址错误（检查端口号）");
            
        } finally {
            // 关闭连接
            closeConnection(conn);
            System.out.println("\n连接已关闭");
        }
    }
}
