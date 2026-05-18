package moneyTransfer.service;

import moneyTransfer.dao.StatisticsDao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 统计服务类 - 业务逻辑层
 *
 * 职责：
 * 1. 调用 StatisticsDao 获取原始数据
 * 2. 格式化数据，生成易于阅读的报表
 * 3. 提供业务级别的统计接口
 */
public class StatisticsService {

    private StatisticsDao statisticsDao;

    public StatisticsService() {
        this.statisticsDao = new StatisticsDao();
    }

    /**
     * 打印系统整体财务概览
     */
    public void printSystemOverview() {
        System.out.println("=== 系统整体概览 ===");
        Map<String, Object> overview = statisticsDao.getSystemOverview();

        if (overview.isEmpty()) {
            System.out.println("暂无数据");
            return;
        }

        System.out.printf("  活跃用户数：%s 人%n", overview.get("active_users"));
        System.out.printf("  活跃账户数：%s 个%n", overview.get("active_accounts"));
        System.out.printf("  账户总余额：%.2f 元%n", toBigDecimal(overview.get("total_balance")));
        System.out.println();
        System.out.printf("  总发钱笔数：%s 笔%n", overview.get("total_payments"));
        System.out.printf("  已完成发钱：%.2f 元%n", toBigDecimal(overview.get("total_paid_amount")));
        System.out.println();
        System.out.printf("  总充值笔数：%s 笔%n", overview.get("total_recharges"));
        System.out.printf("  总充值金额：%.2f 元%n", toBigDecimal(overview.get("total_recharged_amount")));
    }

    /**
     * 打印账户发钱汇总表
     */
    public void printAccountPaymentSummary() {
        System.out.println("=== 账户发钱汇总 ===");
        List<Map<String, Object>> list = statisticsDao.getAccountPaymentSummary();

        if (list.isEmpty()) {
            System.out.println("暂无账户数据");
            return;
        }

        System.out.printf("  %-20s %10s %10s %8s %10s%n",
            "账户名称", "当前余额", "发钱总额", "发钱次数", "平均每笔");
        System.out.println("  " + "-".repeat(65));

        for (Map<String, Object> row : list) {
            System.out.printf("  %-20s %10.2f %10.2f %8s %10.2f%n",
                row.get("account_name"),
                toBigDecimal(row.get("current_balance")),
                toBigDecimal(row.get("total_paid")),
                row.get("payment_count"),
                toBigDecimal(row.get("avg_payment")));
        }
    }

    /**
     * 打印用户收款汇总表
     */
    public void printUserReceiptSummary() {
        System.out.println("=== 用户收款汇总 ===");
        List<Map<String, Object>> list = statisticsDao.getUserReceiptSummary();

        if (list.isEmpty()) {
            System.out.println("暂无收款记录");
            return;
        }

        System.out.printf("  %-12s %-10s %10s %8s %10s%n",
            "用户姓名", "开户行", "收款总额", "收款次数", "最大单笔");
        System.out.println("  " + "-".repeat(55));

        for (Map<String, Object> row : list) {
            System.out.printf("  %-12s %-10s %10.2f %8s %10.2f%n",
                row.get("user_name"),
                row.get("bank_name"),
                toBigDecimal(row.get("total_received")),
                row.get("receipt_count"),
                toBigDecimal(row.get("max_received")));
        }
    }

    /**
     * 打印发钱明细（含账户名和用户名）
     */
    public void printPaymentDetails() {
        System.out.println("=== 发钱明细 ===");
        List<Map<String, Object>> list = statisticsDao.getPaymentDetails();

        if (list.isEmpty()) {
            System.out.println("暂无发钱记录");
            return;
        }

        System.out.printf("  %4s %-14s %-10s %10s %-10s %-16s%n",
            "ID", "账户", "收款人", "金额", "状态", "时间");
        System.out.println("  " + "-".repeat(70));

        for (Map<String, Object> row : list) {
            System.out.printf("  %4s %-14s %-10s %10.2f %-10s %-16s%n",
                row.get("payment_id"),
                row.get("account_name"),
                row.get("user_name"),
                toBigDecimal(row.get("amount")),
                row.get("status"),
                row.get("payment_time"));
        }
    }

    /**
     * 打印各状态的发钱统计
     */
    public void printPaymentCountByStatus() {
        System.out.println("=== 发钱状态分布 ===");
        List<Map<String, Object>> list = statisticsDao.getPaymentCountByStatus();

        if (list.isEmpty()) {
            System.out.println("暂无发钱记录");
            return;
        }

        for (Map<String, Object> row : list) {
            System.out.printf("  %-12s: %s 笔，共 %.2f 元%n",
                row.get("status"),
                row.get("count"),
                toBigDecimal(row.get("total_amount")));
        }
    }

    /**
     * 打印收款金额 TOP N 用户
     */
    public void printTopReceivers(int topN) {
        System.out.println("=== 收款金额 TOP " + topN + " 用户 ===");
        List<Map<String, Object>> list = statisticsDao.getTopReceivers(topN);

        if (list.isEmpty()) {
            System.out.println("暂无数据");
            return;
        }

        int rank = 1;
        for (Map<String, Object> row : list) {
            System.out.printf("  第%d名  %-10s %-10s  收款 %.2f 元（共 %s 笔）%n",
                rank++,
                row.get("user_name"),
                row.get("bank_name"),
                toBigDecimal(row.get("total_received")),
                row.get("receipt_count"));
        }
    }

    /**
     * 打印发钱次数达标的账户（HAVING 演示）
     */
    public void printActivePayingAccounts(int minCount) {
        System.out.println("=== 发钱次数 >= " + minCount + " 次的账户 ===");
        List<Map<String, Object>> list = statisticsDao.getActivePayingAccounts(minCount);

        if (list.isEmpty()) {
            System.out.println("没有满足条件的账户");
            return;
        }

        for (Map<String, Object> row : list) {
            System.out.printf("  %-20s  发钱 %s 次，合计 %.2f 元%n",
                row.get("account_name"),
                row.get("payment_count"),
                toBigDecimal(row.get("total_paid")));
        }
    }

    /**
     * 打印充值汇总
     */
    public void printRechargesSummary() {
        System.out.println("=== 充值汇总 ===");
        List<Map<String, Object>> list = statisticsDao.getRechargesSummary();

        if (list.isEmpty()) {
            System.out.println("暂无充值记录");
            return;
        }

        for (Map<String, Object> row : list) {
            System.out.printf("  %-20s  充值 %s 次，合计 %.2f 元（最大单笔 %.2f 元）%n",
                row.get("account_name"),
                row.get("recharge_count"),
                toBigDecimal(row.get("total_recharged")),
                toBigDecimal(row.get("max_recharge")));
        }
    }

    // =====================================================
    // 私有工具方法
    // =====================================================

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP);
    }
}
