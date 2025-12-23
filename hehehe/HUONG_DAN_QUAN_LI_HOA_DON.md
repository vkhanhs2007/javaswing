# 📊 HƯỚNG DẪN QUẢN LÍ HÓA ĐƠN

## 📍 Vị Trí Chức Năng

**Menu Chính** → **Quản Lí Hóa Đơn** (hoặc **Quản Lý Hóa Đơn**)

---

## 🔍 CÁC TRẠNG THÁI HÓA ĐƠN

| Status | Ý Nghĩa | Khi Nào Xuất Hiện |
|--------|---------|------------------|
| `Tạm` | Tạm thời (chưa thanh toán) | Sau khi gọi món |
| `Đã thanh toán` | Đã hoàn tất thanh toán | Sau khi nhấn "Xác nhận thanh toán" |

---

## 📋 BƯỚC 1: CHỌN KỲNG (Filter)

Trong quản lí hóa đơn, bạn có thể lọc theo:

- **Ngày** - Hóa đơn được tạo/thanh toán **hôm nay**
- **Tháng** - Hóa đơn trong **tháng này**
- **Quý** - Hóa đơn trong **quý này**
- **Năm** - Hóa đơn trong **năm này**

### Cách Sử Dụng:
1. Chọn dropdown "Thời Kỳ"
2. Chọn khoảng thời gian cần xem
3. **Danh sách tự động cập nhật**

---

## 📊 BƯỚC 2: HIỂN THỊ HÓA ĐƠN

Bảng sẽ hiển thị các cột:

| Cột | Nội Dung | Ví Dụ |
|-----|---------|-------|
| **ID** | Mã hóa đơn | 12345 |
| **Bàn** | Bàn nào gọi | Bàn 3 |
| **Ngày Tạo** | Khi tạo hóa đơn | 2025-12-22 14:30:25 |
| **Ngày Thanh Toán** | Khi thanh toán | 2025-12-22 14:45:10 |
| **Tổng Tiền** | Tính bằng đ | 250.000 đ |
| **Trạng Thái** | Tạm / Đã thanh toán | Đã thanh toán |

### Chú Ý:
- ✅ Mặc định **chỉ hiển thị hóa đơn trong ngày hôm nay**
- ✅ Nếu **không có hóa đơn**, sẽ thông báo: "Không có hóa đơn cho kỳ: ..."
- ✅ **Hóa đơn được sắp xếp mới nhất trước** (CreatedDate DESC)

---

## 👁️ BƯỚC 3: XEM CHI TIẾT

### Cách Xem:
1. **Chọn một hóa đơn** trong bảng (click vào dòng)
2. Nhấn nút **"Xem Chi Tiết"**
3. Cửa sổ popup hiển thị:

```
Hóa Đơn #12345
Bàn: 3
Ngày Tạo: 2025-12-22 14:30:25
Ngày Thanh Toán: 2025-12-22 14:45:10
Trạng Thái: Đã thanh toán
----------------------------------------
Tổng Tiền: 300.000 đ
Giảm Giá: 30.000 đ
Thành Tiền: 270.000 đ
Điểm Sử Dụng: 270 điểm
```

---

## 🐛 TROUBLESHOOTING

### ❌ Vấn Đề: "Không có hóa đơn"

**Nguyên nhân & Giải Pháp:**

| Nguyên Nhân | Giải Pháp |
|------------|----------|
| Chưa gọi món hôm nay | Gọi món → Thanh toán trước |
| Hóa đơn chưa thanh toán | Hóa đơn "Tạm" không hiển thị, phải thanh toán |
| Tìm hóa đơn cũ | Thay đổi **Thời Kỳ** từ "Ngày" → "Tháng"/"Năm" |

### ❌ Vấn Đề: "Lỗi khi xem chi tiết"

**Giải Pháp:**
- Kiểm tra console log để xem lỗi chi tiết
- Kiểm tra Database xem hóa đơn có tồn tại không
- Thử đóng và mở lại chương trình

---

## 🗄️ KIỂM TRA TRONG DATABASE

Nếu không thấy hóa đơn, hãy kiểm tra trực tiếp Database:

### SQL Server (SSMS)

```sql
-- Xem tất cả hóa đơn
SELECT * FROM Invoices ORDER BY CreatedDate DESC;

-- Xem hóa đơn của hôm nay
SELECT * FROM Invoices 
WHERE CAST(CreatedDate AS DATE) = CAST(GETDATE() AS DATE)
ORDER BY CreatedDate DESC;

-- Xem hóa đơn đã thanh toán
SELECT * FROM Invoices 
WHERE Status = 'Đã thanh toán'
ORDER BY CreatedDate DESC;

-- Xem chi tiết một hóa đơn
SELECT i.*, d.*
FROM Invoices i
LEFT JOIN InvoiceDetails d ON i.InvoiceID = d.InvoiceID
WHERE i.InvoiceID = 12345;
```

---

## 📈 CÁC TRƯỜNG HỢP SỬ DỤNG

### Trường Hợp 1: Xem Doanh Thu Ngày
1. Chọn **"Ngày"**
2. Xem tất cả hóa đơn đã thanh toán hôm nay
3. Tính tổng doanh thu = Σ(Tổng Tiền)

### Trường Hợp 2: Xem Báo Cáo Tháng
1. Chọn **"Tháng"**
2. Xem hóa đơn từ đầu tháng đến nay
3. Xuất báo cáo doanh thu

### Trường Hợp 3: Tìm Hóa Đơn Cũ
1. Chọn **"Năm"** (hoặc **"Quý"** / **"Tháng"**)
2. Tìm hóa đơn cần xem
3. Xem chi tiết để kiểm tra

---

## ✅ CHECKLIST

- ✅ Chức năng "Quản Lí Hóa Đơn" có trong menu chính
- ✅ Có thể lọc theo Ngày / Tháng / Quý / Năm
- ✅ Bảng hiển thị đầy đủ thông tin hóa đơn
- ✅ Có nút "Xem Chi Tiết" để xem thêm
- ✅ Có thông báo khi không có hóa đơn
- ✅ Database SQL Server lưu trữ tất cả hóa đơn
- ✅ Console log hiển thị chi tiết các thao tác

---

## 📝 GHI CHÚ

- **Hóa đơn "Tạm"** = Chưa thanh toán → **Không hiển thị trong Quản Lí**
- **Hóa đơn "Đã thanh toán"** = Đã hoàn tất → **Hiển thị trong Quản Lí**
- Để xem hóa đơn tạm, phải vào **OrderFrame** trực tiếp
- Hóa đơn được lưu cả trong **Database** và **File** (nếu in)

---

**Phiên bản:** v1.1  
**Cập nhật:** 2025-12-22  
**Trạng thái:** ✅ Hoạt động bình thường
