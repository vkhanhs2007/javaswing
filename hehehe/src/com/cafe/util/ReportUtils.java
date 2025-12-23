package com.cafe.util;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ReportUtils {
    
    public static Map<Integer, Integer> getMostOrderedItems() {
        Map<Integer, Integer> itemCount = new HashMap<>();
        String sql = "SELECT MenuItemID, SUM(Quantity) as TotalQuantity FROM InvoiceDetails GROUP BY MenuItemID ORDER BY TotalQuantity DESC";
        
        try (Connection conn = com.cafe.config.DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                itemCount.put(rs.getInt("MenuItemID"), rs.getInt("TotalQuantity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemCount;
    }
    
    public static Map<Integer, Integer> getLeastOrderedItems() {
        Map<Integer, Integer> itemCount = new HashMap<>();
        String sql = "SELECT m.MenuItemID, ISNULL(SUM(d.Quantity), 0) as TotalQuantity " +
                     "FROM MenuItems m LEFT JOIN InvoiceDetails d ON m.MenuItemID = d.MenuItemID " +
                     "WHERE m.Available = 1 GROUP BY m.MenuItemID ORDER BY TotalQuantity ASC";
        
        try (Connection conn = com.cafe.config.DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                itemCount.put(rs.getInt("MenuItemID"), rs.getInt("TotalQuantity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemCount;
    }
    
    public static double getTotalRevenueByDate(java.time.LocalDateTime date) {
        String sql = "SELECT SUM(TotalAmount) as TotalRevenue FROM Invoices " +
                     "WHERE CAST(PaidDate AS DATE) = CAST(? AS DATE) AND Status = 'Đã thanh toán'";
        
        try (Connection conn = com.cafe.config.DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TotalRevenue");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
