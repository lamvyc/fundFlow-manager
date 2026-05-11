package moneyTransfer.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 账户实体类
 * 对应数据库表：accounts
 * 
 * 注意：
 * - 余额使用 BigDecimal（精确计算，避免浮点数误差）
 * - 不要使用 double/float 存储金额！
 */
public class Account {
    
    // ========== 字段 ==========
    
    private Integer id;              // 账户ID
    private String name;             // 账户名称
    private BigDecimal balance;      // 当前余额（使用 BigDecimal 而不是 double）
    private String status;           // 状态：ACTIVE/FROZEN
    private Timestamp createTime;    // 创建时间
    private Timestamp updateTime;    // 更新时间
    
    // ========== 构造函数 ==========
    
    /**
     * 无参构造函数
     */
    public Account() {
    }
    
    /**
     * 全参构造函数
     */
    public Account(Integer id, String name, BigDecimal balance, 
                   String status, Timestamp createTime, Timestamp updateTime) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    
    /**
     * 常用字段构造函数
     * 用途：创建新账户时使用
     */
    public Account(String name, BigDecimal balance) {
        this.name = name;
        this.balance = balance;
        this.status = "ACTIVE";  // 默认状态为激活
    }
    
    // ========== Getter/Setter ==========
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    /**
     * 重写 toString()
     */
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
    
    /**
     * 判断账户是否激活
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    /**
     * 判断账户是否冻结
     */
    public boolean isFrozen() {
        return "FROZEN".equals(status);
    }
    
    /**
     * 判断余额是否充足
     * 
     * @param amount 需要的金额
     * @return true=余额充足，false=余额不足
     */
    public boolean hasEnoughBalance(BigDecimal amount) {
        if (balance == null || amount == null) {
            return false;
        }
        // balance >= amount
        return balance.compareTo(amount) >= 0;
    }
}
