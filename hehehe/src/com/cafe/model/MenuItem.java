package com.cafe.model;

public class MenuItem {
    private int menuItemId;
    private String itemName;
    private String category;
    private double price;
    private String description;
    private boolean available;
    private double discount;
    private String timeDiscountStart;
    private String timeDiscountEnd;
    
    public MenuItem() {
    }
    
    public MenuItem(int menuItemId, String itemName, String category, double price, String description) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.description = description;
        this.available = true;
    }
    
    public int getMenuItemId() {
        return menuItemId;
    }
    
    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public void setDiscount(double discount) {
        this.discount = discount;
    }
    
    public String getTimeDiscountStart() {
        return timeDiscountStart;
    }
    
    public void setTimeDiscountStart(String timeDiscountStart) {
        this.timeDiscountStart = timeDiscountStart;
    }
    
    public String getTimeDiscountEnd() {
        return timeDiscountEnd;
    }
    
    public void setTimeDiscountEnd(String timeDiscountEnd) {
        this.timeDiscountEnd = timeDiscountEnd;
    }
    
    @Override
    public String toString() {
        return itemName;
    }
}
