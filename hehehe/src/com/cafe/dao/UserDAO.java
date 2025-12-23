package com.cafe.dao;

import com.cafe.config.DatabaseConfig;
import com.cafe.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        String sql = "SELECT * FROM Users WHERE Username = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setUsername(rs.getString("Username"));
                    user.setPassword(rs.getString("Password"));
                    user.setRole(rs.getString("Role"));
                    user.setFullName(rs.getString("FullName"));
                    user.setActive(rs.getBoolean("Active"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi lấy thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            System.err.println("[ERROR] Thông tin người dùng không hợp lệ");
            return false;
        }
        
        String sql = "INSERT INTO Users (Username, Password, Role, FullName, Active) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole() != null ? user.getRole() : "Nhân viên");
            pstmt.setString(4, user.getFullName() != null ? user.getFullName() : "");
            pstmt.setBoolean(5, user.isActive());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi thêm người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY FullName";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setRole(rs.getString("Role"));
                user.setFullName(rs.getString("FullName"));
                user.setActive(rs.getBoolean("Active"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public boolean updateUser(User user) {
        if (user == null || user.getUserId() <= 0 || user.getUsername() == null) {
            System.err.println("[ERROR] Thông tin người dùng không hợp lệ");
            return false;
        }
        
        // Nếu có mật khẩu, update cả mật khẩu
        String sql;
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            sql = "UPDATE Users SET Password = ?, Role = ?, FullName = ?, Active = ? WHERE UserID = ?";
        } else {
            sql = "UPDATE Users SET Role = ?, FullName = ?, Active = ? WHERE UserID = ?";
        }
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                pstmt.setString(1, user.getPassword());
                pstmt.setString(2, user.getRole() != null ? user.getRole() : "Nhân viên");
                pstmt.setString(3, user.getFullName() != null ? user.getFullName() : "");
                pstmt.setBoolean(4, user.isActive());
                pstmt.setInt(5, user.getUserId());
            } else {
                pstmt.setString(1, user.getRole() != null ? user.getRole() : "Nhân viên");
                pstmt.setString(2, user.getFullName() != null ? user.getFullName() : "");
                pstmt.setBoolean(3, user.isActive());
                pstmt.setInt(4, user.getUserId());
            }
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("[DB] ✓ Cập nhật người dùng #" + user.getUserId() + " thành công");
            }
            return result > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi cập nhật người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteUser(int userId) {
        if (userId <= 0) {
            System.err.println("[ERROR] User ID không hợp lệ");
            return false;
        }
        
        String sql = "DELETE FROM Users WHERE UserID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("[DB] ✓ Xóa người dùng #" + userId + " thành công");
            }
            return result > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi xóa người dùng: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
