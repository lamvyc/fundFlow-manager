package moneyTransfer.dao;

import moneyTransfer.entity.User;
import moneyTransfer.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象（DAO - Data Access Object）
 * 
 * 作用：封装所有与 users 表相关的数据库操作
 * 
 * DAO 设计模式：
 * - 把数据库操作封装到一个类中
 * - 其他层（Service、Controller）不直接写 SQL
 * - 便于维护和测试
 * 
 * 包含功能：
 * 1. 添加用户（INSERT）
 * 2. 根据 ID 查询用户（SELECT）
 * 3. 根据手机号查询用户（SELECT）
 * 4. 查询所有用户（SELECT）
 * 5. 更新用户信息（UPDATE）
 * 6. 删除用户（软删除）（UPDATE）
 */
public class UserDao {
    
    /**
     * 添加用户
     * 
     * SQL 示例：
     * INSERT INTO users (name, phone, bank_card, bank_name, status) 
     * VALUES ('张三', '13800138000', '6222...', '工商银行', 'ACTIVE')
     * 
     * @param user 用户对象（不需要包含 id，数据库会自动生成）
     * @return 添加成功返回生成的用户ID，失败返回 -1
     */
    public int addUser(User user) {
        // SQL 语句（使用 ? 占位符，防止 SQL 注入）
        String sql = "INSERT INTO users (name, phone, bank_card, bank_name, status) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // 1. 获取数据库连接
            conn = DBUtil.getConnection();
            
            // 2. 准备 SQL（RETURN_GENERATED_KEYS 表示需要返回自动生成的主键）
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // 3. 设置参数（索引从 1 开始，对应 SQL 中的第几个 ?）
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getBankCard());
            pstmt.setString(4, user.getBankName());
            pstmt.setString(5, user.getStatus() != null ? user.getStatus() : "ACTIVE");
            
            // 4. 执行 SQL（INSERT/UPDATE/DELETE 用 executeUpdate）
            int affectedRows = pstmt.executeUpdate();
            
            // 5. 获取自动生成的 ID
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);  // 返回生成的 ID
                }
            }
            
            return -1;  // 插入失败
            
        } catch (SQLException e) {
            System.err.println("添加用户失败: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            // 6. 关闭资源（顺序：ResultSet → Statement → Connection）
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 根据 ID 查询用户
     * 
     * SQL 示例：
     * SELECT id, name, phone, bank_card, bank_name, status, create_time, update_time
     * FROM users WHERE id = 1
     * 
     * @param id 用户ID
     * @return 用户对象，不存在返回 null
     */
    public User getUserById(int id) {
        String sql = "SELECT id, name, phone, bank_card, bank_name, status, create_time, update_time " +
                     "FROM users WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            // 执行查询（SELECT 用 executeQuery）
            rs = pstmt.executeQuery();
            
            // 处理结果集
            if (rs.next()) {
                // 从结果集中取出数据，封装成 User 对象
                return extractUserFromResultSet(rs);
            }
            
            return null;  // 未找到
            
        } catch (SQLException e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户对象，不存在返回 null
     */
    public User getUserByPhone(String phone) {
        String sql = "SELECT id, name, phone, bank_card, bank_name, status, create_time, update_time " +
                     "FROM users WHERE phone = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phone);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 查询所有用户（只查询激活状态的用户）
     * 
     * SQL 示例：
     * SELECT * FROM users WHERE status = 'ACTIVE' ORDER BY id
     * 
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        String sql = "SELECT id, name, phone, bank_card, bank_name, status, create_time, update_time " +
                     "FROM users WHERE status = 'ACTIVE' ORDER BY id";
        
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            // 遍历结果集（while 循环）
            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                users.add(user);
            }
            
            return users;
            
        } catch (SQLException e) {
            System.err.println("查询用户列表失败: " + e.getMessage());
            e.printStackTrace();
            return users;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 更新用户信息
     * 
     * SQL 示例：
     * UPDATE users SET name = ?, phone = ?, bank_name = ? WHERE id = ?
     * 
     * @param user 用户对象（必须包含 id）
     * @return true=更新成功，false=更新失败
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, phone = ?, bank_name = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getBankName());
            pstmt.setInt(4, user.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("更新用户失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 删除用户（软删除：标记为 DELETED，不物理删除）
     * 
     * SQL 示例：
     * UPDATE users SET status = 'DELETED' WHERE id = ?
     * 
     * @param id 用户ID
     * @return true=删除成功，false=删除失败
     */
    public boolean deleteUser(int id) {
        String sql = "UPDATE users SET status = 'DELETED' WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("删除用户失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 从结果集中提取用户对象（私有方法，复用代码）
     * 
     * 作用：避免重复写相同的代码
     * 
     * @param rs 结果集
     * @return 用户对象
     * @throws SQLException SQL异常
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setPhone(rs.getString("phone"));
        user.setBankCard(rs.getString("bank_card"));
        user.setBankName(rs.getString("bank_name"));
        user.setStatus(rs.getString("status"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        user.setUpdateTime(rs.getTimestamp("update_time"));
        return user;
    }
}
