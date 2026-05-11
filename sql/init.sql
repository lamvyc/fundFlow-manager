-- ========================================
-- 发钱管理系统 - 数据库初始化脚本
-- ========================================

-- 1. 创建数据库
DROP DATABASE IF EXISTS money_transfer_db;
CREATE DATABASE money_transfer_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE money_transfer_db;

-- 2. 创建用户表
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '用户姓名',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    bank_card VARCHAR(30) NOT NULL UNIQUE COMMENT '银行卡号',
    bank_name VARCHAR(100) NOT NULL COMMENT '开户行',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/DELETED',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 创建账户表
CREATE TABLE accounts (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '账户ID',
    name VARCHAR(100) NOT NULL COMMENT '账户名称',
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '当前余额',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/FROZEN',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CHECK (balance >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';

-- 4. 创建发钱记录表
CREATE TABLE payment_records (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    account_id INT NOT NULL COMMENT '发钱账户ID',
    user_id INT NOT NULL COMMENT '收款用户ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '发钱金额',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/COMPLETED/CANCELLED',
    remark VARCHAR(500) COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_account_id (account_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_status (status),
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发钱记录表';

-- 5. 创建充值记录表
CREATE TABLE recharge_records (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    account_id INT NOT NULL COMMENT '账户ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '充值金额',
    before_balance DECIMAL(15,2) NOT NULL COMMENT '充值前余额',
    after_balance DECIMAL(15,2) NOT NULL COMMENT '充值后余额',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '充值时间',
    INDEX idx_account_id (account_id),
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值记录表';

-- 6. 插入测试数据
-- 插入测试用户
INSERT INTO users (name, phone, bank_card, bank_name) VALUES
('张三', '13800138000', '6222000012345678', '工商银行'),
('李四', '13900139000', '6222000087654321', '建设银行'),
('王五', '13700137000', '6222000011112222', '农业银行');

-- 插入测试账户
INSERT INTO accounts (name, balance) VALUES
('公司发薪账户', 100000.00),
('奖金发放账户', 50000.00);

-- 显示创建的表
SHOW TABLES;

-- 显示表结构
DESC users;
DESC accounts;
DESC payment_records;
DESC recharge_records;

SELECT '数据库初始化完成！' AS message;
