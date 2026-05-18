#!/bin/bash

# 发钱管理系统 - 编译运行脚本
# 使用方法：./run.sh <类名>
# 例如：./run.sh moneyTransfer.Step1_DatabaseSetup

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 项目根目录
PROJECT_DIR="/Users/unravel/fundFlow-manager"
cd "$PROJECT_DIR"

# MySQL JDBC 驱动路径
MYSQL_JAR="$PROJECT_DIR/mysql-connector-java-8.0.33.jar"

# 显示菜单
if [ $# -eq 0 ]; then
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}  发钱管理系统 - 快速运行${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo "请选择要运行的程序："
    echo ""
    echo "  1) 测试数据库连接 (DBUtil)"
    echo "  2) Step1: 数据库搭建"
    echo "  3) Step2: 实体类测试"
    echo "  4) Step3: 用户管理测试"
    echo "  5) Step4: 账户管理测试"
    echo "  6) Step5: 发钱功能测试"
    echo "  7) Step6: 统计功能测试"
    echo "  8) Step7: 完整系统集成测试"
    echo "  9) Main: 交互式完整系统"
    echo "  c) 编译所有文件"
    echo "  0) 清理编译文件"
    echo ""
    read -p "请输入选项: " choice
    
    case $choice in
        1) CLASS="moneyTransfer.util.DBUtil" ;;
        2) CLASS="moneyTransfer.Step1_DatabaseSetup" ;;
        3) CLASS="moneyTransfer.Step2_EntityTest" ;;
        4) CLASS="moneyTransfer.Step3_UserDaoTest" ;;
        5) CLASS="moneyTransfer.Step4_AccountDaoTest" ;;
        6) CLASS="moneyTransfer.Step5_PaymentServiceTest" ;;
        7) CLASS="moneyTransfer.Step6_StatisticsTest" ;;
        8) CLASS="moneyTransfer.Step7_SystemTest" ;;
        9) CLASS="moneyTransfer.Main" ;;
        c)
            echo -e "${YELLOW}正在编译所有文件...${NC}"
            javac -cp "$MYSQL_JAR" -d . -encoding UTF-8 src/moneyTransfer/**/*.java src/moneyTransfer/*.java 2>&1
            if [ $? -eq 0 ]; then
                echo -e "${GREEN}✓ 编译成功！${NC}"
            else
                echo -e "${RED}✗ 编译失败${NC}"
            fi
            exit 0
            ;;
        0)
            echo -e "${YELLOW}正在清理编译文件...${NC}"
            rm -rf moneyTransfer
            echo -e "${GREEN}✓ 清理完成${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}无效选项${NC}"
            exit 1
            ;;
    esac
else
    CLASS="$1"
fi

# 编译
echo -e "${YELLOW}正在编译 $CLASS...${NC}"

# 查找需要编译的源文件
SOURCE_FILES=$(find src/moneyTransfer -name "*.java" | grep -E "(util|entity|dao|service|exception|Step|Main)")

javac -cp "$MYSQL_JAR" -d . -encoding UTF-8 $SOURCE_FILES 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ 编译失败${NC}"
    exit 1
fi

echo -e "${GREEN}✓ 编译成功${NC}"
echo ""

# 运行
echo -e "${YELLOW}运行 $CLASS:${NC}"
echo ""
java -cp ".:$MYSQL_JAR" "$CLASS"

echo ""
echo -e "${GREEN}程序执行完毕${NC}"
