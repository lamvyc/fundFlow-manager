#!/bin/bash

# 数据库初始化检查脚本

echo "========================================="
echo "  检查数据库是否已初始化"
echo "========================================="
echo ""

# 检查 MySQL 是否运行
echo "1. 检查 MySQL 服务..."
if pgrep -x "mysqld" > /dev/null; then
    echo "   ✓ MySQL 服务正在运行"
else
    echo "   ✗ MySQL 服务未启动"
    echo "   请先启动 MySQL 服务"
    exit 1
fi

echo ""
echo "2. 检查数据库和表..."
mysql -u root -proot1234 -e "
USE money_transfer_db;
SHOW TABLES;
SELECT '数据库检查完成' AS status;
" 2>&1

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ 数据库已初始化"
    echo ""
    echo "3. 查看现有用户数据..."
    mysql -u root -proot1234 -e "
    USE money_transfer_db;
    SELECT id, name, phone, bank_name, status FROM users;
    " 2>&1
else
    echo ""
    echo "✗ 数据库未初始化"
    echo ""
    echo "请执行以下命令初始化数据库："
    echo "  mysql -u root -p"
    echo "  source /Users/unravel/fundFlow-manager/sql/init.sql"
fi
