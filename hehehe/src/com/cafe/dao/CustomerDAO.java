package com.cafe.dao;

import com.cafe.config.DatabaseConfig;
import com.cafe.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    public Customer getCustomerByPhone(String phoneNumber) {
        String sql = "SELECT * FROM Customers WHERE PhoneNumber = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phoneNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("CustomerID"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                    customer.setCustomerName(rs.getString("CustomerName"));
                    customer.setPoints(rs.getDouble("Points"));
                    return customer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int addCustomer(Customer customer) {
        String sql = "INSERT INTO Customers (PhoneNumber, CustomerName, Points) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, customer.getPhoneNumber());
            pstmt.setString(2, customer.getCustomerName());
            pstmt.setDouble(3, customer.getPoints());
            
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
    
    public boolean updateCustomerPoints(int customerId, double points) {
        String sql = "UPDATE Customers SET Points = Points + ? WHERE CustomerID = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, points);
            pstmt.setInt(2, customerId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers ORDER BY CustomerName";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("CustomerID"));
                customer.setPhoneNumber(rs.getString("PhoneNumber"));
                customer.setCustomerName(rs.getString("CustomerName"));
                customer.setPoints(rs.getDouble("Points"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
}
