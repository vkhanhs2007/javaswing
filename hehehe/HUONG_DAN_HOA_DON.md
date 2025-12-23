# 📄 HƯỚNG DẪN IN & LƯU HÓA ĐƠN

## 📍 HÓA ĐƠN ĐƯỢC LƯU Ở ĐÂU?

### 1. **Cơ Sở Dữ Liệu (Database)**
   - **Nơi:** SQL Server - Table `Invoices`
   - **Status:** `Đã thanh toán`
   - **Thông tin lưu:** 
     - InvoiceID, TableID, TotalAmount
     - DiscountAmount, PointUsed, PaidDate
     - CreatedDate, Status
   - **Mục đích:** Lưu trữ lâu dài, báo cáo doanh thu

### 2. **File Văn Bản (.txt)**
   - **Vị trí:** Thư mục `bills/` (tại thư mục chạy ứng dụng)
   - **Tên file:** `bill_YYYYMMdd_hhmmss_[InvoiceID].txt`
   - **Ví dụ:** `bill_20251221_143025_12345.txt`
   - **Mục đích:** Lưu bản in hóa đơn, dễ xem lại

### 3. **Console / Terminal**
   - **Vị trí:** Output của chương trình (terminal chạy LoginFrame)
   - **Mục đích:** Debug, xem log thanh toán

### 4. **Dialog Thông Báo**
   - **Vị trí:** Cửa sổ popup trên giao diện
   - **Nội dung:** Hóa đơn + đường dẫn file đã lưu
   - **Mục đích:** Xác nhận cho người dùng

---

## 🔄 WORKFLOW THANH TOÁN (Chi Tiết)

```
┌─────────────────────────────────────────────────────┐
│ 1. OrderFrame: Gọi Món                              │
│    └─ Thêm các món vào orderDetails                 │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│ 2. OrderFrame: Auto-save Hóa Đơn (khi thanh toán)  │
│    ├─ invoiceDAO.addInvoice()                       │
│    │  └─ Tạo mới hóa đơn → InvoiceID               │
│    └─ invoiceDAO.addInvoiceDetail()                 │
│       └─ Lưu chi tiết các món                       │
│       └─ Database: Invoices + InvoiceDetails        │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│ 3. PaymentFrame: Nhập Thông Tin                     │
│    ├─ Số điện thoại khách hàng                      │
│    ├─ Tên khách hàng (tùy chọn)                     │
│    └─ Dùng điểm tích lũy (checkbox)                 │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│ 4. PaymentFrame: Xác Nhận Thanh Toán                │
│    ├─ invoiceDAO.updateInvoice()                    │
│    │  └─ Status: "Tạm" → "Đã thanh toán"           │
│    │  └─ PaidDate: LocalDateTime.now()              │
│    │  └─ Database: Update                           │
│    │                                                 │
│    ├─ tableDAO.updateTableStatus()                  │
│    │  └─ Status: "Đang phục vụ" → "Trống"          │
│    │  └─ Database: Update                           │
│    │                                                 │
│    └─ customerDAO.updateCustomerPoints()            │
│       └─ Cộng điểm tích lũy                         │
│       └─ Database: Update                           │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│ 5. PaymentFrame: In Hóa Đơn (Tùy Chọn)              │
│    ├─ Console: System.out.println()                 │
│    │  └─ Xem trong Terminal                         │
│    │                                                 │
│    ├─ File: BillUtils.saveBillToFile()              │
│    │  └─ Lưu vào bills/bill_*.txt                   │
│    │                                                 │
│    └─ Dialog: UIUtils.showInfoMessage()             │
│       └─ Hiển thị hóa đơn + đường dẫn file          │
└─────────────────────────────────────────────────────┘
```

---

## 🎯 KIỂM TRA HÓA ĐƠN

### Cách 1: Xem Trong Database (SSMS)
```sql
-- Xem tất cả hóa đơn đã thanh toán
SELECT * FROM Invoices WHERE Status = 'Đã thanh toán' ORDER BY PaidDate DESC;

-- Xem chi tiết một hóa đơn
SELECT i.*, 
       d.MenuItemID, d.Quantity, d.UnitPrice, d.Amount
FROM Invoices i
LEFT JOIN InvoiceDetails d ON i.InvoiceID = d.InvoiceID
WHERE i.InvoiceID = 12345;
```

### Cách 2: Xem File Hóa Đơn
```
C:\Users\...\project\bills\bill_20251221_143025_12345.txt
```

### Cách 3: Xem Console Log
```
[DB] Cập nhật hóa đơn #12345 thành công
[PAYMENT] Hóa đơn #12345 được cập nhật trạng thái: Đã thanh toán
[PAYMENT] Bàn #1 được cập nhật trạng thái: Trống
[BILL] Hóa đơn #12345 đã lưu: C:\...\bills\bill_20251221_143025_12345.txt
```

---

## ✅ CÓ GÌ THAY ĐỔI

| Phiên Bản | Cải Tiến |
|----------|---------|
| v1.0 | Hóa đơn chỉ in trên console |
| **v1.1** | ✅ Lưu hóa đơn ra file text |
| **v1.1** | ✅ Hiển thị đường dẫn file trong dialog |
| **v1.1** | ✅ Cải thiện logging trong thanh toán |
| **v1.1** | ✅ Auto-save hóa đơn trước thanh toán |

---

## 🚀 HƯỚNG DẪN SỬ DỤNG

### Bước 1: Gọi Món
1. Đăng nhập
2. Chọn bàn
3. Chọn thực đơn → Thêm món → Chọn số lượng → "Thêm Món"

### Bước 2: Thanh Toán
1. Nhấn nút "Thanh Toán"
2. Nhập thông tin khách hàng (số điện thoại, tên)
3. Nhấn "Xác Nhận Thanh Toán"
4. **Hóa đơn tự động lưu vào Database + File**

### Bước 3: Xem Hóa Đơn
- ✅ Popup trên giao diện: Xem nội dung + đường dẫn
- ✅ File text: Mở `bills/bill_*.txt` để xem chi tiết
- ✅ Database: Truy vấn table `Invoices` để kiểm tra

---

## ⚠️ TROUBLESHOOTING

### Lỗi: "Hóa đơn chưa được lưu"
❌ **Nguyên nhân:** Chưa nhấn "Thanh Toán"
✅ **Cách sửa:** Hãy nhấn "Thanh Toán" để lưu hóa đơn

### Lỗi: "Không tìm thấy file hóa đơn"
❌ **Nguyên nhân:** File chưa được tạo
✅ **Cách sửa:** Kiểm tra thư mục `bills/` tại vị trí chạy ứng dụng

### Lỗi: "Lỗi cập nhật hóa đơn"
❌ **Nguyên nhân:** Database có vấn đề
✅ **Cách sửa:** 
- Kiểm tra SQL Server có chạy không
- Kiểm tra log console để xem lỗi chi tiết
- Kiểm tra InvoiceID > 0

### Lỗi: "InvoiceID = 0"
❌ **Nguyên nhân:** Hóa đơn chưa được lưu trong database
✅ **Cách sửa:** 
- Kiểm tra `addInvoice()` có thành công không
- Kiểm tra log: "[DB] Cập nhật hóa đơn #..."

---

**Phiên bản:** v1.1  
**Cập nhật:** 2025-12-21  
**Trạng thái:** ✅ Đã cải thiện
