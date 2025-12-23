-- DATABASE CREATION SCRIPT FOR CAFE MANAGEMENT SYSTEM
-- SQL Server 2019 or later

-- Create Database
CREATE DATABASE CafeManager;
GO

USE CafeManager;
GO

-- Create Tables Table
CREATE TABLE Tables (
    TableID INT PRIMARY KEY IDENTITY(1,1),
    TableName NVARCHAR(50) NOT NULL,
    Capacity INT NOT NULL,
    Status NVARCHAR(20) NOT NULL DEFAULT 'Trống' -- 'Trống' or 'Đang phục vụ'
);
GO

-- Create MenuItems Table
CREATE TABLE MenuItems (
    MenuItemID INT PRIMARY KEY IDENTITY(1,1),
    ItemName NVARCHAR(100) NOT NULL,
    Category NVARCHAR(50) NOT NULL,
    Price DECIMAL(10, 2) NOT NULL,
    Description NVARCHAR(255),
    Available BIT DEFAULT 1,
    Discount DECIMAL(5, 2) DEFAULT 0,
    TimeDiscountStart TIME,
    TimeDiscountEnd TIME
);
GO

-- Create Customers Table
CREATE TABLE Customers (
    CustomerID INT PRIMARY KEY IDENTITY(1,1),
    PhoneNumber NVARCHAR(20) NOT NULL UNIQUE,
    CustomerName NVARCHAR(100) NOT NULL,
    Points DECIMAL(10, 2) DEFAULT 0
);
GO

-- Create Invoices Table
CREATE TABLE Invoices (
    InvoiceID INT PRIMARY KEY IDENTITY(1,1),
    TableID INT NOT NULL,
    CreatedDate DATETIME NOT NULL,
    PaidDate DATETIME,
    TotalAmount DECIMAL(10, 2) NOT NULL,
    DiscountAmount DECIMAL(10, 2) DEFAULT 0,
    PointUsed DECIMAL(10, 2) DEFAULT 0,
    Status NVARCHAR(20) NOT NULL DEFAULT 'Tạm', -- 'Tạm' or 'Đã thanh toán'
    FOREIGN KEY (TableID) REFERENCES Tables(TableID)
);
GO

-- Create InvoiceDetails Table
CREATE TABLE InvoiceDetails (
    DetailID INT PRIMARY KEY IDENTITY(1,1),
    InvoiceID INT NOT NULL,
    MenuItemID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10, 2) NOT NULL,
    Discount DECIMAL(10, 2) DEFAULT 0,
    Amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID),
    FOREIGN KEY (MenuItemID) REFERENCES MenuItems(MenuItemID)
);
GO

-- Create Users Table
CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY(1,1),
    Username NVARCHAR(50) NOT NULL UNIQUE,
    Password NVARCHAR(100) NOT NULL,
    Role NVARCHAR(20) NOT NULL, -- 'Admin' or 'Nhân viên'
    FullName NVARCHAR(100) NOT NULL,
    Active BIT DEFAULT 1
);
GO

-- Insert sample data

-- Insert Tables
INSERT INTO Tables (TableName, Capacity, Status) VALUES
('Bàn 1', 4, 'Trống'),
('Bàn 2', 4, 'Trống'),
('Bàn 3', 6, 'Trống'),
('Bàn 4', 6, 'Trống'),
('Bàn 5', 8, 'Trống'),
('Bàn 6', 2, 'Trống'),
('Bàn 7', 4, 'Trống'),
('Bàn 8', 4, 'Trống');
GO

-- Insert Menu Items
INSERT INTO MenuItems (ItemName, Category, Price, Description, Available, Discount) VALUES
-- Coffee
('Cà Phê Đen', 'Cà Phê', 20000, 'Cà phê đen nóng', 1, 0),
('Cà Phê Sữa', 'Cà Phê', 22000, 'Cà phê sữa nóng', 1, 0),
('Cà Phê Đen Đá', 'Cà Phê', 18000, 'Cà phê đen lạnh', 1, 0),
('Cà Phê Sữa Đá', 'Cà Phê', 20000, 'Cà phê sữa lạnh', 1, 0),
('Cà Phê Cappuccino', 'Cà Phê', 35000, 'Cappuccino', 1, 0),
('Cà Phê Latte', 'Cà Phê', 35000, 'Latte', 1, 0),
-- Drinks
('Nước Cam', 'Nước Uống', 25000, 'Nước cam tươi', 1, 0),
('Nước Dâu', 'Nước Uống', 30000, 'Nước dâu tây', 1, 0),
('Nước Chanh', 'Nước Uống', 20000, 'Nước chanh lạnh', 1, 0),
('Trà Xanh', 'Nước Uống', 15000, 'Trà xanh nóng', 1, 0),
-- Food
('Bánh Mì', 'Thức Ăn', 25000, 'Bánh mì thơm ngon', 1, 0),
('Phở', 'Thức Ăn', 40000, 'Phở bò/gà', 1, 0),
('Cơm Tấm', 'Thức Ăn', 35000, 'Cơm tấm sườn', 1, 0),
('Xôi Gà', 'Thức Ăn', 30000, 'Xôi với gà', 1, 0),
('Chè Ba Màu', 'Thức Ăn', 20000, 'Chè ba màu', 1, 0);
GO

-- Insert Users
INSERT INTO Users (Username, Password, Role, FullName, Active) VALUES
('admin', '123456789', 'Admin', 'Quản Trị Viên', 1),
('staff1', '123456789', 'Nhân viên', 'Nhân Viên 1', 1),
('staff2', '123456789', 'Nhân viên', 'Nhân Viên 2', 1);
GO

-- Create Indexes for better performance
CREATE INDEX idx_invoice_status ON Invoices(Status);
CREATE INDEX idx_invoice_table ON Invoices(TableID);
CREATE INDEX idx_menuitem_category ON MenuItems(Category);
CREATE INDEX idx_customer_phone ON Customers(PhoneNumber);
GO

PRINT 'Database and tables created successfully!';
