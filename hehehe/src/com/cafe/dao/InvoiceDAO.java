package com.cafe.dao;

import com.cafe.config.DatabaseConfig;
import com.cafe.model.Invoice;
import com.cafe.model.InvoiceDetail;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    
    public int addInvoice(Invoice invoice) {
        String sql = "INSERT INTO Invoices (TableID, CreatedDate, Status, TotalAmount, DiscountAmount, PointUsed) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, invoice.getTableId());
            pstmt.setTimestamp(2, Timestamp.valueOf(invoice.getCreatedDate()));
            pstmt.setString(3, invoice.getStatus());
            pstmt.setDouble(4, invoice.getTotalAmount());
            pstmt.setDouble(5, invoice.getDiscountAmount());
            pstmt.setDouble(6, invoice.getPointUsed());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public boolean updateInvoice(Invoice invoice) {
        if (invoice == null || invoice.getInvoiceId() <= 0) {
            System.err.println("[ERROR] Invoice null hoặc InvoiceID không hợp lệ");
            return false;
        }
        
        String sql = "UPDATE Invoices SET TotalAmount = ?, DiscountAmount = ?, Status = ?, PaidDate = ?, PointUsed = ? " +
                     "WHERE InvoiceID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, invoice.getTotalAmount());
            pstmt.setDouble(2, invoice.getDiscountAmount());
            pstmt.setString(3, invoice.getStatus());
            
            if (invoice.getPaidDate() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(invoice.getPaidDate()));
            } else {
                pstmt.setNull(4, java.sql.Types.TIMESTAMP);
            }
            
            pstmt.setDouble(5, invoice.getPointUsed());
            pstmt.setInt(6, invoice.getInvoiceId());
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("[DB] Cập nhật hóa đơn #" + invoice.getInvoiceId() + " thành công");
                return true;
            } else {
                System.err.println("[ERROR] Không tìm thấy hóa đơn có ID: " + invoice.getInvoiceId());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi cập nhật hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    public Invoice getInvoiceById(int invoiceId) {
        if (invoiceId <= 0) {
            return null;
        }
        
        String sql = "SELECT * FROM Invoices WHERE InvoiceID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setInvoiceId(rs.getInt("InvoiceID"));
                    invoice.setTableId(rs.getInt("TableID"));
                    invoice.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    
                    Timestamp paidDate = rs.getTimestamp("PaidDate");
                    if (paidDate != null) {
                        invoice.setPaidDate(paidDate.toLocalDateTime());
                    }
                    
                    invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                    invoice.setDiscountAmount(rs.getDouble("DiscountAmount"));
                    invoice.setPointUsed(rs.getDouble("PointUsed"));
                    invoice.setStatus(rs.getString("Status"));
                    return invoice;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi lấy thông tin hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public Invoice getInvoiceByTableId(int tableId) {
        if (tableId <= 0) {
            return null;
        }
        
        String sql = "SELECT * FROM Invoices WHERE TableID = ? AND Status = N'Tạm' ORDER BY CreatedDate DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tableId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setInvoiceId(rs.getInt("InvoiceID"));
                    invoice.setTableId(rs.getInt("TableID"));
                    invoice.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                    invoice.setDiscountAmount(rs.getDouble("DiscountAmount"));
                    invoice.setPointUsed(rs.getDouble("PointUsed"));
                    invoice.setStatus(rs.getString("Status"));
                    
                    System.out.println("[DB] ✓ Tìm thấy hóa đơn tạm cho bàn #" + tableId 
                                     + " | ID: #" + invoice.getInvoiceId()
                                     + " | Status: '" + invoice.getStatus() + "'"
                                     + " | Tổng tiền: " + invoice.getTotalAmount());
                    return invoice;
                } else {
                    System.out.println("[DB] ⚠ Không tìm thấy hóa đơn tạm (Status='Tạm') cho bàn #" + tableId);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi lấy hóa đơn bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Invoice> getAllInvoicesByTableId(int tableId) {
        List<Invoice> invoices = new ArrayList<>();
        if (tableId <= 0) {
            return invoices;
        }
        
        String sql = "SELECT * FROM Invoices WHERE TableID = ? ORDER BY CreatedDate DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tableId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setInvoiceId(rs.getInt("InvoiceID"));
                    invoice.setTableId(rs.getInt("TableID"));
                    invoice.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                    invoice.setDiscountAmount(rs.getDouble("DiscountAmount"));
                    invoice.setPointUsed(rs.getDouble("PointUsed"));
                    invoice.setStatus(rs.getString("Status"));
                    
                    Timestamp paidDate = rs.getTimestamp("PaidDate");
                    if (paidDate != null) {
                        invoice.setPaidDate(paidDate.toLocalDateTime());
                    }
                    
                    invoices.add(invoice);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi lấy tất cả hóa đơn bàn: " + e.getMessage());
            e.printStackTrace();
        }
        return invoices;
    }
    
    public List<Invoice> getInvoicesByDate(LocalDateTime startDate, LocalDateTime endDate, String period) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "";
        
        switch(period) {
            case "Ngày":
                sql = "SELECT * FROM Invoices WHERE CAST(CreatedDate AS DATE) = CAST(? AS DATE) " +
                      "ORDER BY CreatedDate DESC";
                break;
            case "Tháng":
                sql = "SELECT * FROM Invoices WHERE MONTH(CreatedDate) = MONTH(?) " +
                      "AND YEAR(CreatedDate) = YEAR(?) " +
                      "ORDER BY CreatedDate DESC";
                break;
            case "Quý":
                sql = "SELECT * FROM Invoices WHERE DATEPART(QUARTER, CreatedDate) = DATEPART(QUARTER, ?) " +
                      "AND YEAR(CreatedDate) = YEAR(?) " +
                      "ORDER BY CreatedDate DESC";
                break;
            case "Năm":
                sql = "SELECT * FROM Invoices WHERE YEAR(CreatedDate) = YEAR(?) " +
                      "ORDER BY CreatedDate DESC";
                break;
            default:
                sql = "SELECT * FROM Invoices WHERE CreatedDate BETWEEN ? AND ? " +
                      "ORDER BY CreatedDate DESC";
        }
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (period.equals("Tùy chỉnh")) {
                pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
                pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            } else {
                pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
                if (!period.equals("Ngày") && !period.equals("Năm")) {
                    pstmt.setTimestamp(2, Timestamp.valueOf(startDate));
                }
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Invoice invoice = new Invoice();
                    invoice.setInvoiceId(rs.getInt("InvoiceID"));
                    invoice.setTableId(rs.getInt("TableID"));
                    invoice.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    
                    Timestamp paidDate = rs.getTimestamp("PaidDate");
                    if (paidDate != null) {
                        invoice.setPaidDate(paidDate.toLocalDateTime());
                    }
                    
                    invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                    invoice.setDiscountAmount(rs.getDouble("DiscountAmount"));
                    invoice.setPointUsed(rs.getDouble("PointUsed"));
                    invoice.setStatus(rs.getString("Status"));
                    invoices.add(invoice);
                }
            }
            
            System.out.println("[DB] Lấy " + invoices.size() + " hóa đơn từ " + period);
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi lấy hóa đơn theo ngày: " + e.getMessage());
            e.printStackTrace();
        }
        return invoices;
    }
    
    public void addInvoiceDetail(InvoiceDetail detail) {
        String sql = "INSERT INTO InvoiceDetails (InvoiceID, MenuItemID, Quantity, UnitPrice, Discount, Amount) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, detail.getInvoiceId());
            pstmt.setInt(2, detail.getMenuItemId());
            pstmt.setInt(3, detail.getQuantity());
            pstmt.setDouble(4, detail.getUnitPrice());
            pstmt.setDouble(5, detail.getDiscount());
            pstmt.setDouble(6, detail.getAmount());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<InvoiceDetail> getInvoiceDetails(int invoiceId) {
        List<InvoiceDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM InvoiceDetails WHERE InvoiceID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    InvoiceDetail detail = new InvoiceDetail();
                    detail.setDetailId(rs.getInt("DetailID"));
                    detail.setInvoiceId(rs.getInt("InvoiceID"));
                    detail.setMenuItemId(rs.getInt("MenuItemID"));
                    detail.setQuantity(rs.getInt("Quantity"));
                    detail.setUnitPrice(rs.getDouble("UnitPrice"));
                    detail.setDiscount(rs.getDouble("Discount"));
                    detail.setAmount(rs.getDouble("Amount"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
}
