package com.cafe.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String SERVER_NAME = "localhost";
    private static final int PORT_NUMBER = 1433;
    private static final String DATABASE_NAME = "CafeManager";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456789";
    private static boolean driverLoaded = false;
    
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            driverLoaded = true;
            System.out.println("[DB] SQL Server Driver tải thành công!");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] SQL Server Driver không được tìm thấy!");
            System.err.println("[ERROR] Vui lòng tải JDBC driver từ: https://docs.microsoft.com/en-us/sql/connect/jdbc");
            e.printStackTrace();
        }
    }
    
    public static boolean isDriverLoaded() {
        return driverLoaded;
    }
    
    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            throw new SQLException("SQL Server Driver chưa được tải!");
        }
        
        String connectionUrl = "jdbc:sqlserver://" + SERVER_NAME + ":" + PORT_NUMBER 
            + ";databaseName=" + DATABASE_NAME 
            + ";user=" + USER 
            + ";password=" + PASSWORD
            + ";encrypt=false"
            + ";trustServerCertificate=true";
        
        try {
            Connection conn = DriverManager.getConnection(connectionUrl);
            System.out.println("[DB] Kết nối cơ sở dữ liệu thành công!");
            return conn;
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            System.err.println("[ERROR] Server: " + SERVER_NAME + ":" + PORT_NUMBER);
            System.err.println("[ERROR] Database: " + DATABASE_NAME);
            System.err.println("[ERROR] Kiểm tra SQL Server có chạy không!");
            throw e;
        }
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[WARNING] Lỗi đóng kết nối: " + e.getMessage());
            }
        }
    }
}
