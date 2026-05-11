package moneyTransfer;

import moneyTransfer.util.DBUtil;
import moneyTransfer.util.PrintUtil;

import java.sql.Connection;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Step 1: 数据库搭建
 * 
 * 学习目标：
 * 1. 理解数据库表结构设计
 * 2. 掌握 JDBC 执行 SQL 语句
 * 3. 理解主键、外键、索引的作用
 * 
 * 运行方式：
 * cd /Users/unravel/fundFlow-manager
 * javac -d . -encoding UTF-8 src/moneyTransfer/Step1_DatabaseSetup.java src/moneyTransfer/util/*.java
 * java moneyTransfer.Step1_DatabaseSetup
 * 
 * 运行前准备：
 * 1. 确保 MySQL 服务已启动
 * 2. 修改 config/db.properties 中的数据库密码
 */
public class Step1_DatabaseSetup {
    
    public static void main(String[] args) {
        PrintUtil.title("Step 1: 数据库搭建");
        
        PrintUtil.subtitle("准备工作");
        PrintUtil.info("即将创建数据库和表...");
        PrintUtil.info("数据库名称: money_transfer_db");
        PrintUtil.info("包含表: users, accounts, payment_records, recharge_records");
        
        // 方式1：手动执行 SQL 脚本（推荐）
        System.out.println("\n" + "=".repeat(50));
        PrintUtil.warning("推荐方式：手动执行 SQL 脚本");
        System.out.println("=".repeat(50));
        PrintUtil.info("请执行以下步骤：");
        PrintUtil.info("1. 打开终端，连接到 MySQL：");
        PrintUtil.info("   mysql -u root -p");
        PrintUtil.info("");
        PrintUtil.info("2. 执行初始化脚本：");
        PrintUtil.info("   source /Users/unravel/fundFlow-manager/sql/init.sql");
        PrintUtil.info("");
        PrintUtil.info("3. 验证表是否创建成功：");
        PrintUtil.info("   USE money_transfer_db;");
        PrintUtil.info("   SHOW TABLES;");
        
        // 方式2：通过 Java 代码执行（备用）
        System.out.println("\n" + "=".repeat(50));
        PrintUtil.subtitle("或者：通过 Java 代码自动执行");
        System.out.println("=".repeat(50));
        PrintUtil.info("如果你想通过 Java 代码自动执行，请按回车继续...");
        PrintUtil.info("（不推荐，因为无法看到详细的 SQL 执行过程）");
        
        try {
            System.in.read(); // 等待用户按回车
            executeInitScript();
        } catch (Exception e) {
            PrintUtil.error("执行失败: " + e.getMessage());
        }
    }
    
    /**
     * 通过 Java 代码执行初始化脚本
     * 
     * 注意：这种方式有局限性
     * - 无法执行某些复杂的 SQL（如 USE 语句）
     * - 无法看到详细的执行过程
     * - 出错时不容易调试
     * 
     * 所以推荐使用方式1（手动执行）
     */
    private static void executeInitScript() {
        // 这个变量将来用来保存与数据库建立的连接
        Connection conn = null;
        // 这个变量用来执行 SQL 语句
        Statement stmt = null;
        
        try {
            PrintUtil.subtitle("连接数据库");
            
            // 注意：这里需要先连接到 MySQL 服务器（不指定数据库）
            String url = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
            conn = java.sql.DriverManager.getConnection(url, "root", "your_password");
            
            PrintUtil.success("连接成功");
            
            // 读取 SQL 脚本
            PrintUtil.subtitle("读取初始化脚本");
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader("sql/init.sql"))) {
                String line;
                // 先赋值然后比较，既完成了赋值，又完成了判断，还避免了代码重复。
                while ((line = reader.readLine()) != null) {
                    // 跳过注释和空行
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--")) {
                        continue;
                    }
                    sqlBuilder.append(line).append(" ");
                }
            }
            
            PrintUtil.success("脚本读取完成");
            
            // 执行 SQL
            PrintUtil.subtitle("执行 SQL 语句");
            stmt = conn.createStatement();
            
            // 分割多条 SQL（以分号分隔）
            String[] sqls = sqlBuilder.toString().split(";");
            int successCount = 0;
            
            for (String sql : sqls) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    try {
                        stmt.execute(sql);
                        successCount++;
                    } catch (Exception e) {
                        // 某些语句可能执行失败（如表已存在），这是正常的
                        PrintUtil.warning("SQL 执行警告: " + e.getMessage().substring(0, Math.min(50, e.getMessage().length())));
                    }
                }
            }
            
            PrintUtil.success("执行完成，成功执行 " + successCount + " 条 SQL");
            
            PrintUtil.subtitle("验证结果");
            PrintUtil.info("请执行以下命令验证：");
            PrintUtil.info("mysql -u root -p");
            PrintUtil.info("USE money_transfer_db;");
            PrintUtil.info("SHOW TABLES;");
            
        } catch (Exception e) {
            PrintUtil.error("执行失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                // 忽略
            }
        }
    }
}
