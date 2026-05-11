package moneyTransfer.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 发钱记录实体类
 * 对应数据库表：payment_records
 */
public class PaymentRecord {
    
    // ========== 字段 ==========
    
    private Integer id;              // 记录ID
    private Integer accountId;       // 发钱账户ID
    private Integer userId;          // 收款用户ID
    private BigDecimal amount;       // 发钱金额
    private String status;           // 状态：PENDING/COMPLETED/CANCELLED
    private String remark;           // 备注
    private Timestamp createTime;    // 创建时间
    private Timestamp updateTime;    // 更新时间
    
    // ========== 状态常量 ==========
    // 使用常量代替字符串，避免拼写错误
    
    public static final String STATUS_PENDING = "PENDING";       // 待处理
    public static final String STATUS_COMPLETED = "COMPLETED";   // 已完成
    public static final String STATUS_CANCELLED = "CANCELLED";   // 已取消
    
    // ========== 构造函数 ==========
    
    public PaymentRecord() {
    }
    
    public PaymentRecord(Integer id, Integer accountId, Integer userId, 
                         BigDecimal amount, String status, String remark, 
                         Timestamp createTime, Timestamp updateTime) {
        this.id = id;
        this.accountId = accountId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.remark = remark;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    
    /**
     * 创建发钱记录时使用
     */
    public PaymentRecord(Integer accountId, Integer userId, BigDecimal amount, String remark) {
        this.accountId = accountId;
        this.userId = userId;
        this.amount = amount;
        this.remark = remark;
        this.status = STATUS_PENDING;  // 默认状态为待处理
    }
    
    // ========== Getter/Setter ==========
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getAccountId() {
        return accountId;
    }
    
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public Timestamp getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    
    public Timestamp getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
    
    // ========== 工具方法 ==========
    
    @Override
    public String toString() {
        return "PaymentRecord{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
    
    /**
     * 判断是否待处理
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }
    
    /**
     * 判断是否已完成
     */
    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }
    
    /**
     * 判断是否已取消
     */
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }
}
