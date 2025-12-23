package com.cafe.model;

public class Customer {
    private int customerId;
    private String phoneNumber;
    private String customerName;
    private double points;
    
    public Customer() {
    }
    
    public Customer(int customerId, String phoneNumber, String customerName) {
        this.customerId = customerId;
        this.phoneNumber = phoneNumber;
        this.customerName = customerName;
        this.points = 0;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public double getPoints() {
        return points;
    }
    
    public void setPoints(double points) {
        this.points = points;
    }
    
    public void addPoints(double amount) {
        this.points += (int)(amount / 1000);
    }
}
