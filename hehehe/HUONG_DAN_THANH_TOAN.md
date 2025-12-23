# Hướng Dẫn Thanh Toán Hóa Đơn Tạm

## Quy Trình Thanh Toán Chi Tiết

### Bước 1: Gọi Món cho Bàn
1. Từ MainFrame, chọn bàn cần thanh toán (trạng thái "Trống" hoặc "Đang phục vụ")
2. Nhấp nút **"Gọi Món"** để mở cửa sổ gọi món
3. Nếu bàn "Đang phục vụ" có hóa đơn tạm sẵn có, hóa đơn sẽ được tải lại

### Bước 2: Thêm/Chỉnh Sửa Món Ăn (Nếu Cần)
**Phía trái - Menu Selection:**
- Chọn loại món từ **"Loại Món"** (Tất cả, Cà Phê, Nước, v.v.)
- Chọn một món từ danh sách
- Nhập **"Số Lượng"** (mặc định 1)
- Nhấp **"Thêm Vào Hóa Đơn"** để thêm món

**Phía phải - Chi Tiết Hóa Đơn:**
- Xem danh sách các món đã chọn với: Tên Món, Đơn Giá, SL, Thành Tiền
- Nhấp vào dòng để chọn món
- Nhấp **"Xóa Món"** để loại bỏ món đã chọn

### Bước 3: Kiểm Tra Tổng Tiền
**Phía dưới bên phải - Tính Tiền:**
- **Tổng Tiền**: Tổng cộng tất cả các món
- **Điểm Tích Lũy**: Số điểm khách hàng sẽ nhận (1 điểm = 1.000 đ)
- **Giảm Giá**: Nhập tỉ lệ giảm giá (0-100%)

### Bước 4: Lưu Hóa Đơn Tạm (Tuỳ Chọn)
- Nhấp **"Lưu Hóa Đơn Tạm"** để lưu các thay đổi
- Thích hợp khi khách hàng chưa sẵn sàng thanh toán
- Bàn sẽ chuyển trạng thái thành "Đang phục vụ"
- Khi quay lại, hóa đơn sẽ được tải lại tự động

### Bước 5: Thanh Toán Hóa Đơn
1. **Nhấp "Thanh Toán"** (nút xanh)
2. Cửa sổ **PaymentFrame** sẽ mở ra với:

#### Thông Tin Thanh Toán:
   - Tổng Tiền: Tổng cộng trước giảm giá
   - Giảm Giá: Số tiền giảm
   - Thành Tiền: Số tiền khách hàng cần trả
   - Điểm Tích Lũy: Số điểm nhận được

#### Thông Tin Khách Hàng:
   - **Số Điện Thoại**: Nhập số điện thoại khách hàng (tùy chọn)
     - Hệ thống sẽ tự động tìm kiếm khách hàng sẵn có
     - Nếu tìm thấy: Hiển thị tên khách hàng và điểm hiện tại
     - Nếu không tìm thấy: Cần nhập tên khách hàng
   
   - **Tên Khách Hàng**: 
     - Nếu khách hàng sẵn có: Tự động điền
     - Nếu khách hàng mới: Nhập tên (bắt buộc nếu có số điện thoại)
   
   - **Điểm Hiện Tại**: Hiển thị tổng điểm của khách hàng
   
   - **Dùng Điểm**: Tích vào nếu muốn dùng điểm để thanh toán
     - Số điểm dùng = min(Điểm khách hàng, Điểm tích lũy từ hóa đơn)
     - Số tiền được trừ = Số điểm dùng × 1.000 đ

### Bước 6: Xác Nhận Thanh Toán
1. Kiểm tra lại:
   - Thông tin khách hàng đúng không
   - Giảm giá và tổng tiền đúng không
   - Có muốn dùng điểm không

2. **Nhấp "Xác Nhận Thanh Toán"** để hoàn tất:
   - Hóa đơn được lưu vào database với trạng thái "Đã thanh toán"
   - Bàn chuyển trạng thái thành "Trống"
   - Khách hàng được cộng điểm (hoặc trừ nếu dùng điểm)
   - Hệ thống yêu cầu in hóa đơn

3. **In Hóa Đơn** (tùy chọn):
   - Nhấp **"Có"** để in/lưu hóa đơn
   - Nhấp **"Không"** để bỏ qua
   - Hóa đơn được lưu vào thư mục `bills/` với tên: `bill_YYYYMMDD_HHMMSS_ID.txt`

4. **Hoàn Tất**:
   - Cửa sổ PaymentFrame đóng
   - Cửa sổ OrderFrame đóng
   - MainFrame refresh lại danh sách bàn

---

## Các Trường Hợp Đặc Biệt

### 1. Hóa Đơn Tạm Mà Không Có Hóa Đơn
- Nếu bàn "Đang phục vụ" nhưng không có hóa đơn tạm trong database:
  - Hệ thống sẽ tạo hóa đơn mới
  - Bàn sẽ được reset về "Trống"
  - Log: `[CLEANUP] Reset bàn #X từ 'Đang phục vụ' về 'Trống' vì không có hóa đơn`

### 2. Thanh Toán Trực Tiếp (Không Lưu Tạm)
- Bạn có thể thanh toán ngay mà không cần lưu tạm trước
- Hóa đơn sẽ được lưu tự động trong lúc xử lý thanh toán

### 3. Chuyển Bàn
- Nếu cần chuyển khách từ bàn này sang bàn khác:
  - Nhấp **"Chuyển Bàn"** (trước thanh toán)
  - Chọn bàn đích
  - Hóa đơn sẽ được chuyển, khách hàng không cần thanh toán bàn cũ

### 4. Khách Hàng Mới
- Không cần có số điện thoại
- Hoặc nhập số điện thoại + tên để lưu thông tin khách hàng mới

### 5. Dùng Điểm
- Tích vào "Dùng Điểm" để trừ tiền dựa trên điểm hiện tại
- Số tiền trừ = số điểm × 1.000 đ
- Số tiền thanh toán = thành tiền - điểm dùng

---

## Các Nút Chức Năng

| Nút | Vị Trí | Chức Năng |
|-----|--------|---------|
| **Gọi Món** | MainFrame | Mở cửa sổ gọi món cho bàn được chọn |
| **Thêm Vào Hóa Đơn** | OrderFrame | Thêm món được chọn vào hóa đơn |
| **Xóa Món** | OrderFrame | Xóa món được chọn khỏi hóa đơn |
| **Lưu Hóa Đơn Tạm** | OrderFrame | Lưu hóa đơn tạm vào database |
| **Thanh Toán** | OrderFrame | Mở cửa sổ thanh toán |
| **Chuyển Bàn** | OrderFrame | Chuyển khách sang bàn khác |
| **Đóng** | OrderFrame | Đóng cửa sổ gọi món |
| **Xác Nhận Thanh Toán** | PaymentFrame | Hoàn tất thanh toán |
| **Hủy** | PaymentFrame | Quay lại mà không thanh toán |

---

## Trạng Thái Bàn

| Trạng Thái | Ý Nghĩa | Có Thể Click? |
|-----------|--------|-------------|
| **Trống** | Bàn không có khách | ✅ Có |
| **Đang phục vụ** | Có hóa đơn tạm, chưa thanh toán | ✅ Có |
| Trạng thái khác | Bàn không khả dụng | ❌ Không |

---

## Mẹo & Lưu Ý

✓ **Lưu tạm thường xuyên** để tránh mất dữ liệu khi thoát vô ý
✓ **Kiểm tra tổng tiền** trước khi thanh toán
✓ **Nhập đúng thông tin khách hàng** để tích lũy điểm chính xác
✓ **Không cần đợi khách thanh toán** - có thể lưu tạm rồi thoát, quay lại sau
✓ **Hóa đơn sẽ tự động tải** khi mở bàn "Đang phục vụ" lần tiếp theo

---

## Lỗi Thường Gặp

| Lỗi | Nguyên Nhân | Cách Xử Lý |
|-----|-----------|---------|
| "Không có món để thanh toán" | Chưa thêm món nào | Thêm ít nhất 1 món trước |
| "Vui lòng nhập tên khách hàng" | Nhập SĐT nhưng không nhập tên | Nhập tên khách hàng mới |
| Bàn "Đang phục vụ" không mở được | Bàn không có hóa đơn tạm | Hệ thống sẽ reset bàn tự động |
| Hóa đơn không hiển thị | Bàn bị lỗi database | Kiểm tra logs để xác định vấn đề |

