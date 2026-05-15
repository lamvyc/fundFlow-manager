-- ========================================
-- 发钱管理系统 - 数据库初始化脚本
-- ========================================

-- 1. 创建数据库
DROP DATABASE IF EXISTS money_transfer_db;
-- CHARACTER SET utf8mb4（指定字符集）utf8 其实是个“阉割版”所以无法保存像 😀 这样的 Emoji 表情或某些生僻汉字,而 utf8mb4 才是真正完整的
-- COLLATE utf8mb4_unicode_ci（指定排序规则）ci 代表“不区分大小写”
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


/*
 * DEFAULT CURRENT_TIMESTAMP与ON UPDATE CURRENT_TIMESTAMP数据库会自动帮你维护时间
 * INDEX idx_name (name),INDEX idx_phone (phone) 是创建复合索引，提高查询效率
 * 如果没有索引，当你要查一个名字叫"张三"的用户时，数据库只能从第一行开始，一行一行地往下翻（这叫全表扫描），数据量大了会非常慢。索引就像是字典前面的"拼音目录"。
 * ENGINE=InnoDB：指定这张表的存储引擎为 InnoDB。它支持事务（保证转账等资金操作要么全部成功，要么全部失败回滚）和行级锁（多人同时操作不同数据互不干扰）。
 * CHECK (balance >= 0) 约束：确保账户余额不会为负。
 */

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

/*
 * 外键约束：保证数据的完整性和一致性,避免孤儿数据
 * FOREIGN KEY (account_id)：指定当前这张表（子表/参照表）中的 account_id 字段作为外键。
 * REFERENCES accounts(id)：指明这个外键必须去引用（关联）父表（被参照表）accounts 的主键 id。
 * 插入数据时（INSERT），必须先在 accounts 表插入数据，再在 recharge_records 表插入数据，否则会报错。
 * 删除数据时（DELETE），必须先在 recharge_records 表删除数据，再在 accounts 表删除数据，否则会报错。
 * 延伸知识：更智能的级联操作
 * 外键其实还可以玩得更高级。比如在定义外键时加上 ON DELETE CASCADE（级联删除）。
 * 意思是，如果 accounts 表中的某条记录被删除了，那么 recharge_records 表中所有对应的外键记录都会自动删除。
 * 这意味着，一旦你删除了 accounts 表里的某个账户，数据库会自动帮你把 recharge_records 表里所有属于这个账户的充值记录也一并全部删掉，彻底防止产生垃圾数据。。
 */


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
