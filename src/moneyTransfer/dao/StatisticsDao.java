package moneyTransfer.dao;

import moneyTransfer.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * 统计数据访问对象（DAO）
 *
 * ⭐⭐⭐ Step 6 的核心类！⭐⭐⭐
 *
 * 本类演示 SQL 中常用的统计技术：
 *
 * 1. JOIN 查询      - 多表关联，获取跨表数据
 * 2. 聚合函数       - SUM(), COUNT(), AVG(), MAX(), MIN()
 * 3. GROUP BY       - 分组统计
 * 4. HAVING         - 对分组结果过滤（类似 WHERE 但作用于聚合结果）
 * 5. 子查询         - SQL 嵌套查询
 * 6. ORDER BY       - 排序输出
 */
public class StatisticsDao {

    // =====================================================
    // 一、账户统计
    // =====================================================

    /**
     * 统计每个账户的发钱汇总信息
     *
     * SQL 知识点：
     * - LEFT JOIN：即使账户没有发钱记录，也会包含在结果中（余额为0）
     * - GROUP BY account_id：按账户分组
     * - COALESCE(x, 0)：如果 x 为 NULL 则返回 0（用于无发钱记录的账户）
     *
     * @return 每个账户的发钱汇总 List，每项为 Map（键：列名，值：数据）
     */
    public List<Map<String, Object>> getAccountPaymentSummary() {
        String sql =
            "SELECT " +
            "  a.id         AS account_id, " +
            "  a.name       AS account_name, " +
            "  a.balance    AS current_balance, " +
            "  COALESCE(COUNT(p.id), 0)       AS payment_count, " +
            "  COALESCE(SUM(p.amount), 0)     AS total_paid, " +
            "  COALESCE(AVG(p.amount), 0)     AS avg_payment, " +
            "  COALESCE(MAX(p.amount), 0)     AS max_payment, " +
            "  COALESCE(MIN(p.amount), 0)     AS min_payment " +
            "FROM accounts a " +
            "LEFT JOIN payment_records p " +
            "  ON a.id = p.account_id AND p.status = 'COMPLETED' " +
            "GROUP BY a.id, a.name, a.balance " +
            "ORDER BY total_paid DESC";

        return queryToList(sql);
    }

    /**
     * 统计每个用户的收款汇总信息
     *
     * SQL 知识点：
     * - INNER JOIN：只返回两表都有匹配的行
     * - 与 LEFT JOIN 的区别：INNER JOIN 不会包含没有收款记录的用户
     *
     * @return 每个用户的收款汇总
     */
    public List<Map<String, Object>> getUserReceiptSummary() {
        String sql =
            "SELECT " +
            "  u.id         AS user_id, " +
            "  u.name       AS user_name, " +
            "  u.phone      AS phone, " +
            "  u.bank_name  AS bank_name, " +
            "  COUNT(p.id)     AS receipt_count, " +
            "  SUM(p.amount)   AS total_received, " +
            "  AVG(p.amount)   AS avg_received, " +
            "  MAX(p.amount)   AS max_received " +
            "FROM users u " +
            "INNER JOIN payment_records p " +
            "  ON u.id = p.user_id AND p.status = 'COMPLETED' " +
            "GROUP BY u.id, u.name, u.phone, u.bank_name " +
            "ORDER BY total_received DESC";

        return queryToList(sql);
    }

    // =====================================================
    // 二、发钱记录统计
    // =====================================================

    /**
     * 获取发钱明细（JOIN 多张表）
     *
     * SQL 知识点：
     * - 多表 JOIN：同时关联 payment_records、accounts、users 三张表
     * - 别名（AS）：让列名更易读
     *
     * @return 发钱明细列表（含账户名和用户名）
     */
    public List<Map<String, Object>> getPaymentDetails() {
        String sql =
            "SELECT " +
            "  p.id           AS payment_id, " +
            "  p.amount       AS amount, " +
            "  p.status       AS status, " +
            "  p.remark       AS remark, " +
            "  p.create_time  AS payment_time, " +
            "  a.name         AS account_name, " +
            "  u.name         AS user_name, " +
            "  u.phone        AS user_phone, " +
            "  u.bank_name    AS user_bank " +
            "FROM payment_records p " +
            "INNER JOIN accounts a ON p.account_id = a.id " +
            "INNER JOIN users    u ON p.user_id    = u.id " +
            "ORDER BY p.create_time DESC";

        return queryToList(sql);
    }

    /**
     * 按状态统计发钱记录数量和金额
     *
     * SQL 知识点：
     * - GROUP BY status：按状态分组
     * - 每组统计 COUNT 和 SUM
     *
     * @return 各状态的发钱汇总
     */
    public List<Map<String, Object>> getPaymentCountByStatus() {
        String sql =
            "SELECT " +
            "  status, " +
            "  COUNT(*) AS count, " +
            "  SUM(amount) AS total_amount " +
            "FROM payment_records " +
            "GROUP BY status " +
            "ORDER BY count DESC";

        return queryToList(sql);
    }

    // =====================================================
    // 三、账户总览
    // =====================================================

    /**
     * 获取系统整体财务概览
     *
     * SQL 知识点：
     * - 子查询（Subquery）：在 SELECT 中嵌套另一个 SELECT
     * - 不需要 GROUP BY，直接对全表聚合
     *
     * @return 系统整体概览（单行 Map）
     */
    public Map<String, Object> getSystemOverview() {
        String sql =
            "SELECT " +
            "  (SELECT COUNT(*) FROM users WHERE status = 'ACTIVE')    AS active_users, " +
            "  (SELECT COUNT(*) FROM accounts WHERE status = 'ACTIVE') AS active_accounts, " +
            "  (SELECT COALESCE(SUM(balance), 0) FROM accounts)        AS total_balance, " +
            "  (SELECT COUNT(*) FROM payment_records)                   AS total_payments, " +
            "  (SELECT COALESCE(SUM(amount), 0) FROM payment_records " +
            "     WHERE status = 'COMPLETED')                           AS total_paid_amount, " +
            "  (SELECT COUNT(*) FROM recharge_records)                  AS total_recharges, " +
            "  (SELECT COALESCE(SUM(amount), 0) FROM recharge_records) AS total_recharged_amount";

        List<Map<String, Object>> result = queryToList(sql);
        return result.isEmpty() ? new LinkedHashMap<>() : result.get(0);
    }

    // =====================================================
    // 四、TOP 排行榜
    // =====================================================

    /**
     * 收款金额最多的 TOP N 用户
     *
     * SQL 知识点：
     * - LIMIT：限制返回的行数
     * - ORDER BY total DESC：从大到小排序
     *
     * @param topN 取前 N 名
     * @return TOP N 收款用户
     */
    public List<Map<String, Object>> getTopReceivers(int topN) {
        String sql =
            "SELECT " +
            "  u.name        AS user_name, " +
            "  u.bank_name   AS bank_name, " +
            "  COUNT(p.id)   AS receipt_count, " +
            "  SUM(p.amount) AS total_received " +
            "FROM users u " +
            "INNER JOIN payment_records p " +
            "  ON u.id = p.user_id AND p.status = 'COMPLETED' " +
            "GROUP BY u.id, u.name, u.bank_name " +
            "ORDER BY total_received DESC " +
            "LIMIT ?";

        return queryToListWithParam(sql, topN);
    }

    /**
     * 查询发钱次数大于指定次数的账户（HAVING 演示）
     *
     * SQL 知识点：
     * - HAVING：对 GROUP BY 后的结果进行过滤（WHERE 不能用于聚合结果）
     * - WHERE vs HAVING 的区别：
     *   WHERE 在分组前过滤行，HAVING 在分组后过滤组
     *
     * @param minCount 最小发钱次数
     * @return 满足条件的账户列表
     */
    public List<Map<String, Object>> getActivePayingAccounts(int minCount) {
        String sql =
            "SELECT " +
            "  a.name           AS account_name, " +
            "  COUNT(p.id)      AS payment_count, " +
            "  SUM(p.amount)    AS total_paid " +
            "FROM accounts a " +
            "INNER JOIN payment_records p " +
            "  ON a.id = p.account_id AND p.status = 'COMPLETED' " +
            "GROUP BY a.id, a.name " +
            "HAVING payment_count >= ? " +
            "ORDER BY payment_count DESC";

        return queryToListWithParam(sql, minCount);
    }

    // =====================================================
    // 五、充值记录统计
    // =====================================================

    /**
     * 统计每个账户的充值汇总
     *
     * @return 充值汇总列表
     */
    public List<Map<String, Object>> getRechargesSummary() {
        String sql =
            "SELECT " +
            "  a.name          AS account_name, " +
            "  COUNT(r.id)     AS recharge_count, " +
            "  SUM(r.amount)   AS total_recharged, " +
            "  MAX(r.amount)   AS max_recharge, " +
            "  MIN(r.amount)   AS min_recharge " +
            "FROM accounts a " +
            "INNER JOIN recharge_records r ON a.id = r.account_id " +
            "GROUP BY a.id, a.name " +
            "ORDER BY total_recharged DESC";

        return queryToList(sql);
    }

    // =====================================================
    // 私有辅助方法
    // =====================================================

    /**
     * 执行查询，将结果转为 List<Map>（无参数）
     */
    private List<Map<String, Object>> queryToList(String sql) {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                result.add(row);
            }

        } catch (SQLException e) {
            System.err.println("统计查询失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return result;
    }

    /**
     * 执行查询，将结果转为 List<Map>（带单个 int 参数）
     */
    private List<Map<String, Object>> queryToListWithParam(String sql, int param) {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, param);
            rs = pstmt.executeQuery();

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                result.add(row);
            }

        } catch (SQLException e) {
            System.err.println("统计查询失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }

        return result;
    }

    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
