package moneyTransfer.util;

/**
 * 打印工具类
 * 作用：美化控制台输出，让测试结果更清晰
 * 
 * 使用示例：
 * PrintUtil.title("用户管理测试");
 * PrintUtil.subtitle("1. 测试添加用户");
 * PrintUtil.success("添加成功");
 * PrintUtil.error("添加失败");
 */
public class PrintUtil {
    
    // ANSI 颜色代码（让输出更漂亮）
    private static final String RESET = "\033[0m";
    private static final String RED = "\033[31m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String BLUE = "\033[34m";
    private static final String CYAN = "\033[36m";
    
    /**
     * 打印大标题
     */
    public static void title(String text) {
        System.out.println("\n" + BLUE + "========================================");
        System.out.println("  " + text);
        System.out.println("========================================" + RESET + "\n");
    }
    
    /**
     * 打印小标题
     */
    public static void subtitle(String text) {
        System.out.println("\n" + CYAN + "--- " + text + " ---" + RESET);
    }
    
    /**
     * 打印成功信息
     */
    public static void success(String text) {
        System.out.println(GREEN + "✓ " + text + RESET);
    }
    
    /**
     * 打印错误信息
     */
    public static void error(String text) {
        System.out.println(RED + "✗ " + text + RESET);
    }
    
    /**
     * 打印警告信息
     */
    public static void warning(String text) {
        System.out.println(YELLOW + "⚠ " + text + RESET);
    }
    
    /**
     * 打印普通信息
     */
    public static void info(String text) {
        System.out.println("  " + text);
    }
    
    /**
     * 打印分隔线
     */
    public static void line() {
        System.out.println("----------------------------------------");
    }
    
    /**
     * 打印表格行
     */
    public static void tableRow(String... columns) {
        for (String col : columns) {
            System.out.printf("%-20s", col);
        }
        System.out.println();
    }
}
