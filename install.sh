#!/bin/bash

# 发钱管理系统 - 依赖安装脚本

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  发钱管理系统 - 依赖安装${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 项目根目录
PROJECT_DIR="/Users/unravel/fundFlow-manager"
cd "$PROJECT_DIR"

# MySQL JDBC 驱动
MYSQL_JAR="mysql-connector-java-8.0.33.jar"
MYSQL_URL="https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar"

echo "📦 检查依赖..."
echo ""

# 检查 MySQL 驱动
if [ -f "$MYSQL_JAR" ]; then
    SIZE=$(ls -lh "$MYSQL_JAR" | awk '{print $5}')
    echo -e "${GREEN}✓${NC} MySQL JDBC 驱动已存在 (${SIZE})"
    echo "  路径: $PROJECT_DIR/$MYSQL_JAR"
else
    echo -e "${YELLOW}⚠${NC} MySQL JDBC 驱动不存在，正在下载..."
    echo ""
    
    # 下载驱动
    echo "下载地址: $MYSQL_URL"
    curl -L -o "$MYSQL_JAR" "$MYSQL_URL"
    
    if [ $? -eq 0 ]; then
        SIZE=$(ls -lh "$MYSQL_JAR" | awk '{print $5}')
        echo ""
        echo -e "${GREEN}✓${NC} 下载成功！文件大小: ${SIZE}"
        echo "  保存路径: $PROJECT_DIR/$MYSQL_JAR"
    else
        echo ""
        echo -e "${RED}✗${NC} 下载失败！"
        echo ""
        echo "请手动下载："
        echo "  1. 访问: https://dev.mysql.com/downloads/connector/j/"
        echo "  2. 下载 Platform Independent 版本"
        echo "  3. 解压并将 jar 文件复制到项目根目录"
        echo "  4. 重命名为: mysql-connector-java-8.0.33.jar"
        exit 1
    fi
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✓ 依赖检查完成！${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 检查 JDK
echo "🔍 环境检查..."
echo ""

if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
    echo -e "${GREEN}✓${NC} Java 已安装: $JAVA_VERSION"
else
    echo -e "${RED}✗${NC} Java 未安装"
    echo "  请安装 JDK 8 或更高版本"
fi

# 检查 MySQL
if command -v mysql &> /dev/null; then
    MYSQL_VERSION=$(mysql --version | awk '{print $5}' | sed 's/,//')
    echo -e "${GREEN}✓${NC} MySQL 已安装: $MYSQL_VERSION"
else
    echo -e "${RED}✗${NC} MySQL 未安装"
    echo "  请安装 MySQL 8.0 或更高版本"
fi

# 检查 MySQL 服务
if pgrep -x "mysqld" > /dev/null; then
    echo -e "${GREEN}✓${NC} MySQL 服务正在运行"
else
    echo -e "${YELLOW}⚠${NC} MySQL 服务未运行"
    echo "  请启动 MySQL 服务"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✓ 安装完成！${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo "📝 下一步："
echo "  1. 配置数据库: vim config/db.properties"
echo "  2. 初始化数据库: mysql -u root -p < sql/init.sql"
echo "  3. 运行程序: ./run.sh"
echo ""

# 检查配置文件
if grep -q "your_password" config/db.properties 2>/dev/null; then
    echo -e "${YELLOW}⚠ 提醒：请修改 config/db.properties 中的数据库密码！${NC}"
    echo ""
fi
