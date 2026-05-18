package moneyTransfer;

import moneyTransfer.service.StatisticsService;
import moneyTransfer.util.PrintUtil;

/**
 * Step 6: 统计功能测试程序
 *
 * ⭐⭐⭐ 本节学习重点 ⭐⭐⭐
 * 1. JOIN 查询   - INNER JOIN、LEFT JOIN 多表关联
 * 2. 聚合函数    - SUM、COUNT、AVG、MAX、MIN
 * 3. GROUP BY    - 按字段分组统计
 * 4. HAVING      - 过滤分组结果（不能用 WHERE）
 * 5. 子查询      - SELECT 中嵌套 SELECT
 * 6. ORDER BY    - 按聚合结果排序
 *
 * 运行前确保已执行 Step5（数据库中有发钱记录）
 */
public class Step6_StatisticsTest {

    public static void main(String[] args) {
        PrintUtil.printHeader("Step 6: 统计功能测试（JOIN + 聚合函数）");

        StatisticsService service = new StatisticsService();

        // ========== 1. 系统整体概览（子查询演示）==========
        PrintUtil.printSection("1. 系统整体概览（子查询）");
        service.printSystemOverview();

        System.out.println();
        System.out.println("  💡 SQL 知识点：子查询");
        System.out.println("     在 SELECT 中嵌套多个独立的 SELECT 子查询");
        System.out.println("     每个括号内的 SELECT 就是一个子查询");
        System.out.println();

        // ========== 2. 账户发钱汇总（LEFT JOIN + GROUP BY）==========
        PrintUtil.printSection("2. 账户发钱汇总（LEFT JOIN + GROUP BY）");
        service.printAccountPaymentSummary();

        System.out.println();
        System.out.println("  💡 SQL 知识点：LEFT JOIN");
        System.out.println("     LEFT JOIN 即使右表没有匹配行，也会保留左表的行");
        System.out.println("     用 COALESCE(x, 0) 把 NULL 转成 0");
        System.out.println("     GROUP BY 对每个账户分别计算 SUM/COUNT/AVG/MAX/MIN");
        System.out.println();

        // ========== 3. 用户收款汇总（INNER JOIN + GROUP BY）==========
        PrintUtil.printSection("3. 用户收款汇总（INNER JOIN + GROUP BY）");
        service.printUserReceiptSummary();

        System.out.println();
        System.out.println("  💡 SQL 知识点：INNER JOIN vs LEFT JOIN");
        System.out.println("     INNER JOIN：只返回两边都有匹配的行（没收过款的用户不显示）");
        System.out.println("     LEFT  JOIN：保留左表所有行，右表没有则为 NULL");
        System.out.println();

        // ========== 4. 发钱明细（多表 JOIN）==========
        PrintUtil.printSection("4. 发钱明细（三表 JOIN）");
        service.printPaymentDetails();

        System.out.println();
        System.out.println("  💡 SQL 知识点：多表 JOIN");
        System.out.println("     可以连续 JOIN 多张表：payment_records → accounts → users");
        System.out.println("     别名（AS）让列名更直观易读");
        System.out.println();

        // ========== 5. 各状态的发钱分布（GROUP BY）==========
        PrintUtil.printSection("5. 发钱状态分布（GROUP BY status）");
        service.printPaymentCountByStatus();

        System.out.println();
        System.out.println("  💡 SQL 知识点：GROUP BY");
        System.out.println("     GROUP BY status 将记录按状态分组");
        System.out.println("     对每一组分别统计 COUNT(*) 和 SUM(amount)");
        System.out.println();

        // ========== 6. TOP N 收款用户（ORDER BY + LIMIT）==========
        PrintUtil.printSection("6. 收款金额 TOP 3 用户（ORDER BY + LIMIT）");
        service.printTopReceivers(3);

        System.out.println();
        System.out.println("  💡 SQL 知识点：LIMIT");
        System.out.println("     LIMIT N 只返回前 N 行");
        System.out.println("     ORDER BY total_received DESC 先排序再 LIMIT，就是 TOP N 查询");
        System.out.println();

        // ========== 7. 发钱次数达标账户（HAVING）==========
        PrintUtil.printSection("7. 发钱次数达标账户（HAVING）");
        service.printActivePayingAccounts(1);

        System.out.println();
        System.out.println("  💡 SQL 知识点：HAVING vs WHERE");
        System.out.println("     WHERE  在分组前过滤（过滤原始行）");
        System.out.println("     HAVING 在分组后过滤（过滤聚合结果）");
        System.out.println("     HAVING payment_count >= 1 不能用 WHERE，");
        System.out.println("     因为 payment_count 是 COUNT() 的聚合结果");
        System.out.println();

        // ========== 8. 充值汇总 ==========
        PrintUtil.printSection("8. 充值汇总");
        service.printRechargesSummary();

        System.out.println();

        // ========== 学习总结 ==========
        PrintUtil.printSection("学习总结");
        System.out.println("  本节掌握的 SQL 技术：");
        System.out.println("  ✓ LEFT JOIN    - 左连接，保留左表所有行");
        System.out.println("  ✓ INNER JOIN   - 内连接，只保留两边匹配行");
        System.out.println("  ✓ 多表 JOIN    - 同时关联三张或更多表");
        System.out.println("  ✓ GROUP BY     - 按字段分组");
        System.out.println("  ✓ HAVING       - 过滤分组结果");
        System.out.println("  ✓ 聚合函数     - SUM / COUNT / AVG / MAX / MIN");
        System.out.println("  ✓ COALESCE     - NULL 值处理");
        System.out.println("  ✓ LIMIT        - 限制返回行数（TOP N）");
        System.out.println("  ✓ 子查询       - SELECT 嵌套 SELECT");
        System.out.println();

        PrintUtil.printFooter("Step 6 测试结束");
    }
}
