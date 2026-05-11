package moneyTransfer.entity;

import java.sql.Timestamp;

/**
 * 用户实体类
 * 对应数据库表：users
 * 
 * 作用：用 Java 对象表示数据库中的一行数据
 * 
 * 设计原则：
 * 1. 字段名与数据库列名对应
 * 2. 使用 private 封装（外部不能直接访问）
 * 3. 提供 getter/setter 方法（访问和修改字段）
 * 4. 提供构造函数（方便创建对象）
 * 5. 重写 toString()（方便打印调试）
 */
public class User {
    
    // ========== 字段（对应数据库列）==========
    
    private Integer id;              // 用户ID
    private String name;             // 用户姓名
    private String phone;            // 手机号
    private String bankCard;         // 银行卡号
    private String bankName;         // 开户行
    private String status;           // 状态：ACTIVE/DELETED
    private Timestamp createTime;    // 创建时间
    private Timestamp updateTime;    // 更新时间
    
    // ========== 构造函数 ==========
    
    /**
     * 无参构造函数
     * 用途：用于框架反射创建对象、或需要空对象时
     */
    public User() {
    }
    
    /**
     * 全参构造函数
     * 用途：快速创建包含所有字段的对象
     */
    public User(Integer id, String name, String phone, String bankCard, 
                String bankName, String status, Timestamp createTime, Timestamp updateTime) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.bankCard = bankCard;
        this.bankName = bankName;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    
    /**
     * 常用字段构造函数（不包含 ID 和时间）
     * 用途：创建新用户时使用（ID 由数据库自动生成）
     */
    public User(String name, String phone, String bankCard, String bankName) {
        this.name = name;
        this.phone = phone;
        this.bankCard = bankCard;
        this.bankName = bankName;
        this.status = "ACTIVE";  // 默认状态为激活
    }
    
    // ========== Getter 方法（获取字段值）==========
    
    public Integer getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getBankCard() {
        return bankCard;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public Timestamp getCreateTime() {
        return createTime;
    }
    
    public Timestamp getUpdateTime() {
        return updateTime;
    }
    
    // ========== Setter 方法（设置字段值）==========
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    
    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
    
    // ========== 工具方法 ==========
    
    /**
     * 重写 toString() 方法
     * 作用：方便打印对象信息（调试时非常有用）
     * 
     * 使用示例：
     * User user = new User("张三", "13800138000", "6222...", "工商银行");
     * System.out.println(user);  // 会调用 toString()
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", bankCard='" + maskBankCard(bankCard) + '\'' +  // 脱敏显示
                ", bankName='" + bankName + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
    
    /**
     * 银行卡号脱敏
     * 例如：6222000012345678 → 6222****5678
     */
    private String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 8) {
            return bankCard;
        }
        String prefix = bankCard.substring(0, 4);  // 前4位
        String suffix = bankCard.substring(bankCard.length() - 4);  // 后4位
        return prefix + "****" + suffix;
    }
    
    /**
     * 判断用户是否激活
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}
