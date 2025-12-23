package com.cafe.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BillUtils {
    private static final String BILL_FOLDER = "bills";
    
    static {
        // Tạo folder bills nếu chưa tồn tại
        File folder = new File(BILL_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
            System.out.println("[BILL] Tạo thư mục bills: " + folder.getAbsolutePath());
        }
    }
    
    /**
     * Lưu hóa đơn ra file text
     * @param invoiceId ID hóa đơn
     * @param billContent Nội dung hóa đơn
     * @return Đường dẫn file đã lưu hoặc null nếu lỗi
     */
    public static String saveBillToFile(int invoiceId, String billContent) {
        if (invoiceId <= 0 || billContent == null || billContent.isEmpty()) {
            System.err.println("[ERROR] InvoiceID hoặc nội dung hóa đơn không hợp lệ");
            return null;
        }
        
        try {
            // Tạo tên file: bill_20251221_143025_12345.txt
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = now.format(formatter);
            String fileName = String.format("bill_%s_%d.txt", timestamp, invoiceId);
            String filePath = BILL_FOLDER + File.separator + fileName;
            
            // Lưu file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(billContent);
                writer.flush();
            }
            
            System.out.println("[BILL] Hóa đơn #" + invoiceId + " đã lưu: " + new File(filePath).getAbsolutePath());
            return filePath;
        } catch (IOException e) {
            System.err.println("[ERROR] Lỗi lưu hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy đường dẫn folder chứa hóa đơn
     */
    public static String getBillFolderPath() {
        return new File(BILL_FOLDER).getAbsolutePath();
    }
}
