# PHẦN MỀM QUẢN LÍ QUÁN CAFE
## Java Swing + SQL Server

### ĐẶC ĐIỂM DỰ ÁN
- Giao diện: Java Swing
- Cơ sở dữ liệu: SQL Server 2019 trở lên
- Java Version: Java 11 trở lên
- Lập trình viên: Tự code

### CHỨC NĂNG CHÍNH

#### 1. QUẢN LÍ BÀN ĂN
✓ Xem danh sách bàn ăn  
✓ Thêm bàn mới  
✓ Xóa bàn  
✓ Xem trạng thái bàn (Trống/Đang phục vụ)  
✓ Click chuột phải để thêm/xóa bàn nhanh  
✓ Chuyển bàn (khi gọi món)  

#### 2. QUẢN LÍ THỰC ĐƠN
✓ Xem danh sách món ăn/uống  
✓ Thêm món mới (tên, loại, giá, mô tả)  
✓ Sửa thông tin món  
✓ Xóa món (ẩn đi)  
✓ Áp dụng giảm giá theo khung giờ  
✓ Lọc món theo loại  

#### 3. GỌI MÓN & HÓA ĐƠN
✓ Chọn bàn → Gọi món  
✓ Xem danh sách món theo loại  
✓ Thêm món vào hóa đơn tạm  
✓ Xóa món khỏi hóa đơn  
✓ Tính tổng tiền tự động  
✓ Tính điểm tích lũy (1000đ = 1 điểm)  
✓ Lưu hóa đơn tạm (chưa thanh toán)  
✓ Thanh toán & Lưu hóa đơn  

#### 4. THANH TOÁN
✓ Xem tổng tiền  
✓ Áp dụng giảm giá %  
✓ Nhập thông tin khách hàng  
✓ Dùng điểm tích lũy để giảm giá  
✓ In hóa đơn  
✓ Cập nhật trạng thái bàn thành Trống  

#### 5. QUẢN LÍ HÓA ĐƠN
✓ Xem danh sách hóa đơn  
✓ Lọc theo thời gian (Ngày/Tháng/Quý/Năm)  
✓ Xem chi tiết hóa đơn  
✓ Thống kê doanh thu  

#### 6. THỐNG KÊ & BÁO CÁO
✓ Món được gọi nhiều nhất (Top 10)  
✓ Món được gọi ít nhất (Top 10)  
✓ Tổng doanh thu theo thời gian  

#### 7. ĐỀ MẬT & PHÂN QUYỀN
✓ Đăng nhập người dùng  
✓ 2 vai trò: Admin & Nhân viên  
✓ Admin: Quản lí người dùng, toàn quyền  
✓ Nhân viên: Chỉ dùng chức năng chính  
✓ Đăng xuất  

#### 8. TÍNH NĂNG BỔ SUNG
✓ Mã tích lũy (cứ 1000đ = 1 điểm)  
✓ Sử dụng điểm để mua hàng  
✓ Giảm giá theo khung giờ  
✓ Chuyển bàn  
✓ Right-click để thêm/xóa bàn  
✓ Thống kê món được gọi nhiều/ít  

### CÁCH SỬ DỤNG

#### Bước 1: Cài đặt Database
- Mở SQL Server Management Studio
- Chạy file: database_setup.sql
- Database CafeManager được tạo tự động

#### Bước 2: Compile & Run
```bash
javac -cp "lib/*" -d bin src/com/cafe/*/*.java
java -cp "bin;lib/*" com.cafe.ui.LoginFrame
```

#### Bước 3: Đăng nhập
- Username: admin (hoặc staff1, staff2)
- Password: 123456789

#### Bước 4: Sử dụng
1. Nhấn "Gọi Món" để bắt đầu
2. Chọn bàn → Chọn món → Nhập SL
3. Nhấn "Thêm Vào Hóa Đơn"
4. Xem tổng tiền & điểm
5. Nhấn "Thanh Toán" để hoàn tất

### CẤU TRÚC THƯỞNG CODE

```
src/com/cafe/
├── config/
│   └── DatabaseConfig.java          // Kết nối Database
├── model/
│   ├── Table.java                   // Bàn ăn
│   ├── MenuItem.java                // Món ăn
│   ├── Invoice.java                 // Hóa đơn
│   ├── InvoiceDetail.java           // Chi tiết hóa đơn
│   ├── Customer.java                // Khách hàng
│   └── User.java                    // Người dùng
├── dao/
│   ├── TableDAO.java                // Truy cập bàn ăn
│   ├── MenuItemDAO.java             // Truy cập menu
│   ├── InvoiceDAO.java              // Truy cập hóa đơn
│   ├── CustomerDAO.java             // Truy cập khách hàng
│   └── UserDAO.java                 // Truy cập người dùng
├── ui/
│   ├── LoginFrame.java              // Màn hình đăng nhập
│   ├── MainFrame.java               // Màn hình chính
│   ├── OrderFrame.java              // Màn hình gọi món
│   ├── PaymentFrame.java            // Màn hình thanh toán
│   ├── MenuManagementFrame.java     // Quản lí menu
│   ├── InvoiceManagementFrame.java  // Quản lí hóa đơn
│   ├── StatisticsFrame.java         // Thống kê
│   └── UserManagementFrame.java     // Quản lí người dùng
└── util/
    ├── UIUtils.java                 // Tiện ích UI
    ├── DiscountUtils.java           // Tính giảm giá & điểm
    └── ReportUtils.java             // Tính toán báo cáo
```

### SCHEMA DATABASE

**Tables:**
- TableID, TableName, Capacity, Status

**MenuItems:**
- MenuItemID, ItemName, Category, Price, Description, Available, Discount, TimeDiscountStart, TimeDiscountEnd

**Customers:**
- CustomerID, PhoneNumber, CustomerName, Points

**Invoices:**
- InvoiceID, TableID, CreatedDate, PaidDate, TotalAmount, DiscountAmount, PointUsed, Status

**InvoiceDetails:**
- DetailID, InvoiceID, MenuItemID, Quantity, UnitPrice, Discount, Amount

**Users:**
- UserID, Username, Password, Role, FullName, Active

### YÊU CẦU HỆ THỐNG

- Windows / Linux / MacOS
- Java 11 trở lên
- SQL Server 2019 Express (Free) trở lên
- 2GB RAM tối thiểu
- 500MB ổ cứng

### CÀI ĐẶT CHI TIẾT

Xem file: **HUONG_DAN_SETUP.txt**

### TÀI KHOẢN MẬC ĐỊNH

**Admin:**
- Username: admin
- Password: 123456789

**Staff:**
- Username: staff1 / staff2
- Password: 123456789

### LƯU Ý QUAN TRỌNG

1. **Database Connection:**
   - Server: localhost
   - Port: 1433
   - Database: CafeManager
   - User: sa
   - Password: 123456789

2. **JDBC Driver:**
   - File: mssql-jdbc-xx.x.x.jre11.jar
   - Thư mục: lib/
   - Download: https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server

3. **Data Type:**
   - Giá, tiền: Decimal(10,2)
   - Điểm: Tính tự động (amount/1000)
   - Giảm giá: Theo % hoặc giờ

### TÍNH NĂNG BỔ SUNG CÓ THỂ THÊM

- SMS/Email thông báo cho khách hàng
- QR Code cho bàn ăn
- POS printer support
- Báo cáo doanh thu chi tiết
- Backup database tự động
- Multi-location support
- Loyalty program nâng cao

### LIÊN HỆ & HỖ TRỢ

Nếu gặp vấn đề:
1. Kiểm tra file database_setup.sql đã chạy chưa
2. Kiểm tra JDBC driver trong thư mục lib/
3. Kiểm tra Java version >= 11
4. Kiểm tra SQL Server có chạy không

---
**Phiên bản:** 1.0  
**Cập nhật lần cuối:** 2025  
**Trạng thái:** Hoàn tất
