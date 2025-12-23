package com.cafe.dao;

import com.cafe.config.DatabaseConfig;
import com.cafe.model.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {
    
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM MenuItems WHERE Available = 1 ORDER BY Category, ItemName";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                MenuItem item = new MenuItem();
                item.setMenuItemId(rs.getInt("MenuItemID"));
                item.setItemName(rs.getString("ItemName"));
                item.setCategory(rs.getString("Category"));
                item.setPrice(rs.getDouble("Price"));
                item.setDescription(rs.getString("Description"));
                item.setAvailable(rs.getBoolean("Available"));
                item.setDiscount(rs.getDouble("Discount"));
                item.setTimeDiscountStart(rs.getString("TimeDiscountStart"));
                item.setTimeDiscountEnd(rs.getString("TimeDiscountEnd"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM MenuItems WHERE Category = ? AND Available = 1 ORDER BY ItemName";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem();
                    item.setMenuItemId(rs.getInt("MenuItemID"));
                    item.setItemName(rs.getString("ItemName"));
                    item.setCategory(rs.getString("Category"));
                    item.setPrice(rs.getDouble("Price"));
                    item.setDescription(rs.getString("Description"));
                    item.setAvailable(rs.getBoolean("Available"));
                    item.setDiscount(rs.getDouble("Discount"));
                    item.setTimeDiscountStart(rs.getString("TimeDiscountStart"));
                    item.setTimeDiscountEnd(rs.getString("TimeDiscountEnd"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public MenuItem getMenuItemById(int menuItemId) {
        if (menuItemId <= 0) {
            return null;
        }
        
        String sql = "SELECT * FROM MenuItems WHERE MenuItemID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, menuItemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MenuItem item = new MenuItem();
                    item.setMenuItemId(rs.getInt("MenuItemID"));
                    item.setItemName(rs.getString("ItemName"));
                    item.setCategory(rs.getString("Category"));
                    item.setPrice(rs.getDouble("Price"));
                    item.setDescription(rs.getString("Description"));
                    item.setAvailable(rs.getBoolean("Available"));
                    item.setDiscount(rs.getDouble("Discount"));
                    item.setTimeDiscountStart(rs.getString("TimeDiscountStart"));
                    item.setTimeDiscountEnd(rs.getString("TimeDiscountEnd"));
                    return item;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi lấy thông tin món: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT Category FROM MenuItems WHERE Available = 1 ORDER BY Category";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("Category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    public boolean addMenuItem(MenuItem item) {
        String sql = "INSERT INTO MenuItems (ItemName, Category, Price, Description, Available, Discount, TimeDiscountStart, TimeDiscountEnd) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getItemName());
            pstmt.setString(2, item.getCategory());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setString(4, item.getDescription());
            pstmt.setBoolean(5, item.isAvailable());
            pstmt.setDouble(6, item.getDiscount());
            pstmt.setString(7, item.getTimeDiscountStart());
            pstmt.setString(8, item.getTimeDiscountEnd());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateMenuItem(MenuItem item) {
        String sql = "UPDATE MenuItems SET ItemName = ?, Category = ?, Price = ?, Description = ?, Available = ?, " +
                     "Discount = ?, TimeDiscountStart = ?, TimeDiscountEnd = ? WHERE MenuItemID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getItemName());
            pstmt.setString(2, item.getCategory());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setString(4, item.getDescription());
            pstmt.setBoolean(5, item.isAvailable());
            pstmt.setDouble(6, item.getDiscount());
            pstmt.setString(7, item.getTimeDiscountStart());
            pstmt.setString(8, item.getTimeDiscountEnd());
            pstmt.setInt(9, item.getMenuItemId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteMenuItem(int menuItemId) {
        String sql = "UPDATE MenuItems SET Available = 0 WHERE MenuItemID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, menuItemId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
