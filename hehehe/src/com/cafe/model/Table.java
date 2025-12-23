package com.cafe.model;

import java.time.LocalDateTime;

public class Table {
    private int tableId;
    private String tableName;
    private int capacity;
    private String status; // "Trống", "Đang phục vụ"
    private LocalDateTime occupiedTime;
    
    public Table() {
    }
    
    public Table(int tableId, String tableName, int capacity, String status) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.capacity = capacity;
        this.status = status;
    }
    
    public int getTableId() {
        return tableId;
    }
    
    public void setTableId(int tableId) {
        this.tableId = tableId;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getOccupiedTime() {
        return occupiedTime;
    }
    
    public void setOccupiedTime(LocalDateTime occupiedTime) {
        this.occupiedTime = occupiedTime;
    }
    
    @Override
    public String toString() {
        return tableName;
    }
}
