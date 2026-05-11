package moneyTransfer.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 充值记录实体类
 * 对应数据库表：recharge_records
 */
public class RechargeRecord {
    
    // ========== 字段 ==========
    
    private Integer id;              // 记录ID
    private Integer accountId;       // 账户ID
    private BigDecimal amount;       // 充值金额
    private BigDecimal beforeBalance;  // 充值前余额
    private BigDecimal afterBalance;   // 充值后余额
    private Timestamp createTime;    // 充值时间
    
    // ========== 构造函数 ==========
    
    public RechargeRecord() {
    }
    
    public RechargeRecord(Integer id, Integer accountId, BigDecimal amount, 
                          BigDecimal beforeBalance, BigDecimal afterBalance, Timestamp createTime) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.beforeBalance = beforeBalance;
        this.afterBalance = afterBalance;
        this.createTime = createTime;
    }
    
    /**
     * 创建充值记录时使用
     */
    public RechargeRecord(Integer accountId, BigDecimal amount, 
                          BigDecimal beforeBalance, BigDecimal afterBalance) {
        this.accountId = accountId;
        this.amount = amount;
        this.beforeBalance = beforeBalance;
        this.afterBalance = afterBalance;
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
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getBeforeBalance() {
        return beforeBalance;
    }
    
    public void setBeforeBalance(BigDecimal beforeBalance) {
        this.beforeBalance = beforeBalance;
    }
    
    public BigDecimal getAfterBalance() {
        return afterBalance;
    }
    
    public void setAfterBalance(BigDecimal afterBalance) {
        this.afterBalance = afterBalance;
    }
    
    public Timestamp getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    
    // ========== 工具方法 ==========
    
    @Override
    public String toString() {
        return "RechargeRecord{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", amount=" + amount +
                ", beforeBalance=" + beforeBalance +
                ", afterBalance=" + afterBalance +
                ", createTime=" + createTime +
                '}';
    }
}
