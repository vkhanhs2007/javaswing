package com.cafe.model;

import java.time.LocalDateTime;

public class Invoice {
    private int invoiceId;
    private int tableId;
    private LocalDateTime createdDate;
    private LocalDateTime paidDate;
    private double totalAmount;
    private double discountAmount;
    private double pointUsed;
    private String status; // "Tạm", "Đã thanh toán"
    private String notes;
    
    public Invoice() {
    }
    
    public Invoice(int tableId, LocalDateTime createdDate, double totalAmount, String status) {
        this.tableId = tableId;
        this.createdDate = createdDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }
    
    public int getInvoiceId() {
        return invoiceId;
    }
    
    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public int getTableId() {
        return tableId;
    }
    
    public void setTableId(int tableId) {
        this.tableId = tableId;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getPaidDate() {
        return paidDate;
    }
    
    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public double getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public double getPointUsed() {
        return pointUsed;
    }
    
    public void setPointUsed(double pointUsed) {
        this.pointUsed = pointUsed;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
