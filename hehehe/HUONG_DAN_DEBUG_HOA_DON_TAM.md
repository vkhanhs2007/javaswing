# Hướng Dẫn Xử Lý Vấn Đề: Hóa Đơn Tạm Biến Mất

## Vấn Đề
Khi lưu hóa đơn tạm, rồi click lại vào bàn "Đang phục vụ", hóa đơn biến mất không thể thanh toán.

## Nguyên Nhân Có Thể

### 1. **Hóa đơn không được lưu vào database**
- Kiểm tra logs: Tìm `[SAVE] ✓ Hóa đơn mới được tạo với ID: #X`
- Nếu không thấy → Hóa đơn không được lưu

### 2. **Status hóa đơn không phải "Tạm"**
- Kiểm tra logs: Tìm `[SAVE] Step 4: Đảm bảo trạng thái hóa đơn = 'Tạm'`
- Nếu không thấy → Status không được set đúng

### 3. **Chi tiết hóa đơn (Món) không được lưu**
- Kiểm tra logs: Tìm `[SAVE] Step 3: Lưu chi tiết hóa đơn`
- Nếu không thấy "Lưu chi tiết" → Chi tiết không được lưu

### 4. **Database query không tìm thấy hóa đơn**
- Kiểm tra logs: Tìm `[DB] ✓ Tìm thấy hóa đơn tạm`
- Nếu thấy `[DB] ⚠ Không tìm thấy hóa đơn tạm` → Query không tìm thấy

## Cách Debug

### Bước 1: Mở Console/Terminal
Chạy chương trình và mở Java output logs

### Bước 2: Làm Theo Quy Trình Lưu

**Lần thứ 1 - Tạo và lưu:**
```
1. Click "Gọi Món" → Bàn X
2. Thêm 1-2 món
3. Click "Lưu Hóa Đơn Tạm"
```

**Logs mong đợi:**
```
[SAVE] ═══════════════════════════════════════════
[SAVE] Bắt đầu lưu hóa đơn tạm cho bàn #X
[SAVE] ═══════════════════════════════════════════
[SAVE] Step 1: Tính tổng tiền = XXXXX đ
[SAVE] Step 2: Hóa đơn mới - Lưu vào database
[SAVE]        ✓ Hóa đơn mới được tạo với ID: #YYY
[SAVE] Step 3: Lưu chi tiết hóa đơn
[SAVE]        - Lưu chi tiết: Món ID ... x ... = ... đ
[SAVE]        - Lưu chi tiết: Món ID ... x ... = ... đ
[SAVE]        ✓ Lưu tổng cộng 2 chi tiết
[SAVE] Step 4: Đảm bảo trạng thái hóa đơn = 'Tạm'
[SAVE]        ✓ Status được set thành 'Tạm'
[SAVE] Step 5: Cập nhật trạng thái bàn
[SAVE]        ✓ Bàn #X được cập nhật trạng thái: Đang phục vụ
[SAVE] ═══════════════════════════════════════════
[SAVE] ✓ HOÀN TẤT: Hóa đơn #YYY được lưu với tổng tiền XXXXX đ
[SAVE] ═══════════════════════════════════════════
```

**Đóng OrderFrame sau khi lưu**

### Bước 3: Click Lại Vào Bàn

**Lần thứ 2 - Tải hóa đơn:**
```
1. MainFrame: Click vào Bàn X (trạng thái "Đang phục vụ")
2. Click "Gọi Món"
```

**Logs mong đợi:**
```
[MAIN] Click vào bàn #X | Trạng thái: Đang phục vụ
[MAIN] → Mở OrderFrame để tiếp tục phục vụ/thanh toán

[ORDER] Đang tải hóa đơn cho bàn #X
[DB] ✓ Tìm thấy hóa đơn tạm cho bàn #X | ID: #YYY | Status: 'Tạm' | Tổng tiền: XXXXX
[ORDER] ✓ Tải hóa đơn tạm #YYY cho bàn #X | Status: Tạm | Tổng tiền: XXXXX đ | Chi tiết: 2 mục

[ORDER] ═══════════════════════════════════════════
[ORDER] VERIFY: Hóa đơn được tải/tạo
[ORDER] ═══════════════════════════════════════════
[ORDER] Bàn: #X
[ORDER] Hóa đơn ID: YYY
[ORDER] Status: Tạm
[ORDER] Tổng tiền: XXXXX đ
[ORDER] Chi tiết: 2 mục
[ORDER] Chi tiết hóa đơn:
[ORDER]   - Món #1 | SL: 1 | Giá: 20000 | Thành tiền: 20000
[ORDER]   - Món #2 | SL: 1 | Giá: 25000 | Thành tiền: 25000
[ORDER] ═══════════════════════════════════════════
```

## Nếu Không Tìm Thấy Hóa Đơn

**Logs sẽ hiển thị:**
```
[ORDER] Đang tải hóa đơn cho bàn #X
[DB] ⚠ Không tìm thấy hóa đơn tạm (Status='Tạm') cho bàn #X
[ORDER] ⚠ Không tìm thấy hóa đơn tạm cho bàn #X
[ORDER] DEBUG: Tất cả hóa đơn của bàn #X:
[ORDER]   - Hóa đơn #YYY | Status: XXX | Tổng tiền: ...
[ORDER] → Tạo hóa đơn mới
```

### Phân Tích
Nếu bạn thấy:
- `Status: Đã thanh toán` → Hóa đơn đã thanh toán rồi (bình thường)
- `Status: Tạm` nhưng không tìm thấy → Vấn đề với SQL query
- Không có hóa đơn nào → Hóa đơn không được lưu vào database

## Giải Pháp Từng Trường Hợp

### Trường Hợp 1: Hóa đơn không được lưu
**Kiểm tra:**
- Logs có `[SAVE] ✗ Lỗi tạo hóa đơn mới`?
- Database connection có bị lỗi không?

**Xử lý:**
- Kiểm tra kết nối SQL Server
- Restart ứng dụng
- Xóa hóa đơn test, tạo lại

### Trường Hợp 2: Status không phải "Tạm"
**Kiểm tra:**
- Logs hiển thị status là gì?

**Xử lý:**
- Mở SQL Server Management Studio
- Chạy: `SELECT * FROM Invoices WHERE TableID = X ORDER BY CreatedDate DESC`
- Kiểm tra Status của hóa đơn

### Trường Hợp 3: Chi tiết không được lưu
**Kiểm tra:**
- Logs có `[SAVE] Step 3: Lưu chi tiết hóa đơn`?
- Có bao nhiêu chi tiết được lưu?

**Xử lý:**
- Nếu 0 chi tiết → Vấn đề với `orderDetails` list
- Nếu < số món thêm → Một số chi tiết không được lưu
- Kiểm tra database: `SELECT * FROM InvoiceDetails WHERE InvoiceID = YYY`

### Trường Hợp 4: Database query không tìm thấy
**Kiểm tra:**
- Logs: Status của hóa đơn là gì?

**Xử lý:**
- Nếu Status = "Tạm" nhưng không tìm → Query có vấn đề
- Chạy SQL trực tiếp:
  ```sql
  SELECT * FROM Invoices 
  WHERE TableID = 5 AND Status = N'Tạm' 
  ORDER BY CreatedDate DESC
  ```
- So sánh logs với database

## Tóm Tắt Kiểm Tra

| Logs | Ý Nghĩa | Xử Lý |
|------|---------|-------|
| `[SAVE] ✗ Lỗi tạo hóa đơn` | Hóa đơn không lưu | Kiểm tra DB |
| `[SAVE] Step 3: Lưu 0 chi tiết` | Không có món | Thêm món lại |
| `[DB] ⚠ Không tìm thấy` + `Status: Tạm` | Query lỗi | Kiểm tra SQL |
| `[ORDER] ✓ Tải hóa đơn` | OK - Có thể thanh toán | ✓ Bình thường |

## Thực Hiện Kiểm Tra Database (SQL Server Management Studio)

```sql
-- 1. Xem tất cả hóa đơn
SELECT * FROM Invoices ORDER BY CreatedDate DESC;

-- 2. Xem hóa đơn của bàn cụ thể
SELECT * FROM Invoices WHERE TableID = 5;

-- 3. Xem hóa đơn tạm của bàn
SELECT * FROM Invoices WHERE TableID = 5 AND Status = N'Tạm';

-- 4. Xem chi tiết hóa đơn
SELECT * FROM InvoiceDetails WHERE InvoiceID = 1;

-- 5. Xóa hóa đơn test (nếu cần)
DELETE FROM InvoiceDetails WHERE InvoiceID = 1;
DELETE FROM Invoices WHERE InvoiceID = 1;
```

## Cách Tạo Báo Cáo Lỗi

Nếu vấn đề vẫn xảy ra, hãy cung cấp:
1. **Console logs completes** (từ khi thêm món đến khi click lại)
2. **SQL output** (kết quả từ câu query #2 và #3 trên)
3. **Bàn số nào** gặp vấn đề
4. **Bao nhiêu món** được thêm

Ví dụ:
```
Bàn: 5
Món: 2 (Cà Phê Đen + Nước Cam)
Logs từ console: [... dán tất cả logs ...]
SQL output: [... dán kết quả SELECT ...]
```
