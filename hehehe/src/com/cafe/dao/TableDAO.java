package com.cafe.dao;

import com.cafe.config.DatabaseConfig;
import com.cafe.model.Table;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {
    
    public List<Table> getAllTables() {
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT * FROM Tables ORDER BY TableName";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Table table = new Table();
                table.setTableId(rs.getInt("TableID"));
                table.setTableName(rs.getString("TableName"));
                table.setCapacity(rs.getInt("Capacity"));
                table.setStatus(rs.getString("Status"));
                tables.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }
    
    public Table getTableById(int tableId) {
        if (tableId <= 0) {
            return null;
        }
        
        String sql = "SELECT * FROM Tables WHERE TableID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tableId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Table table = new Table();
                    table.setTableId(rs.getInt("TableID"));
                    table.setTableName(rs.getString("TableName"));
                    table.setCapacity(rs.getInt("Capacity"));
                    table.setStatus(rs.getString("Status"));
                    return table;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi lấy thông tin bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addTable(Table table) {
        String sql = "INSERT INTO Tables (TableName, Capacity, Status) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, table.getTableName());
            pstmt.setInt(2, table.getCapacity());
            pstmt.setString(3, "Trống");
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateTable(Table table) {
        String sql = "UPDATE Tables SET TableName = ?, Capacity = ?, Status = ? WHERE TableID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, table.getTableName());
            pstmt.setInt(2, table.getCapacity());
            pstmt.setString(3, table.getStatus());
            pstmt.setInt(4, table.getTableId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteTable(int tableId) {
        String sql = "DELETE FROM Tables WHERE TableID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tableId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateTableStatus(int tableId, String status) {
        String sql = "UPDATE Tables SET Status = ? WHERE TableID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, tableId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean transferTable(int fromTableId, int toTableId) {
        String sql = "UPDATE Invoices SET TableID = ? WHERE TableID = ? AND Status = 'Tạm'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, toTableId);
            pstmt.setInt(2, fromTableId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
