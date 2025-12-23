package com.cafe.ui;

import com.cafe.dao.InvoiceDAO;
import com.cafe.dao.TableDAO;
import com.cafe.dao.CustomerDAO;
import com.cafe.model.Invoice;
import com.cafe.model.InvoiceDetail;
import com.cafe.model.Customer;
import com.cafe.util.UIUtils;
import com.cafe.util.BillUtils;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentFrame extends JDialog {
    private Invoice invoice;
    private List<InvoiceDetail> details;
    private double discountAmount;
    private InvoiceDAO invoiceDAO;
    private TableDAO tableDAO;
    private CustomerDAO customerDAO;
    private JFrame parentFrame;
    
    public PaymentFrame(Invoice invoice, List<InvoiceDetail> details, double discountAmount, JFrame parentFrame) {
        super(parentFrame, "Thanh Toán", true);
        
        this.invoice = invoice;
        this.details = details;
        this.discountAmount = discountAmount;
        this.parentFrame = parentFrame;
        this.invoiceDAO = new InvoiceDAO();
        this.tableDAO = new TableDAO();
        this.customerDAO = new CustomerDAO();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(parentFrame);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Thanh Toán"));
        
        double totalAmount = invoice.getTotalAmount();
        double finalAmount = totalAmount - discountAmount;
        double points = Math.floor(finalAmount / 1000);
        
        summaryPanel.add(new JLabel("Tổng Tiền:"));
        JLabel totalLabel = new JLabel(UIUtils.formatCurrency(totalAmount) + " đ");
        totalLabel.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        summaryPanel.add(totalLabel);
        
        summaryPanel.add(new JLabel("Giảm Giá:"));
        JLabel discountLabel = new JLabel(UIUtils.formatCurrency(discountAmount) + " đ");
        discountLabel.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        discountLabel.setForeground(Color.RED);
        summaryPanel.add(discountLabel);
        
        summaryPanel.add(new JLabel("Thành Tiền:"));
        JLabel finalLabel = new JLabel(UIUtils.formatCurrency(finalAmount) + " đ");
        finalLabel.setFont(UIUtils.getFont("Arial", Font.BOLD, 14));
        finalLabel.setForeground(Color.BLUE);
        summaryPanel.add(finalLabel);
        
        summaryPanel.add(new JLabel("Điểm Tích Lũy:"));
        JLabel pointsLbl = new JLabel(String.format("%.0f", points) + " điểm");
        pointsLbl.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        summaryPanel.add(pointsLbl);
        
        mainPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Customer info panel
        JPanel customerPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Khách Hàng"));
        
        customerPanel.add(new JLabel("Số Điện Thoại:"));
        JTextField phoneField = new JTextField();
        customerPanel.add(phoneField);
        
        customerPanel.add(new JLabel("Tên Khách Hàng:"));
        JTextField nameField = new JTextField();
        nameField.setEditable(true); // Cho phép nhập tên khách hàng
        customerPanel.add(nameField);
        
        customerPanel.add(new JLabel("Điểm Hiện Tại:"));
        JLabel pointsLabel = new JLabel("0 điểm");
        pointsLabel.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        pointsLabel.setForeground(Color.BLUE);
        customerPanel.add(pointsLabel);
        
        customerPanel.add(new JLabel("Dùng Điểm:"));
        JCheckBox usePointsCheckBox = new JCheckBox("Có");
        customerPanel.add(usePointsCheckBox);
        
        // Add auto-search listener
        phoneField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchCustomer(phoneField.getText().trim(), nameField, pointsLabel);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchCustomer(phoneField.getText().trim(), nameField, pointsLabel);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchCustomer(phoneField.getText().trim(), nameField, pointsLabel);
            }
        });
        
        mainPanel.add(customerPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton confirmButton = new JButton("Xác Nhận Thanh Toán");
        confirmButton.setPreferredSize(new Dimension(150, 40));
        confirmButton.setBackground(Color.GREEN);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.addActionListener(e -> handlePayment(phoneField, nameField, usePointsCheckBox, 
                                                          finalAmount, points));
        buttonPanel.add(confirmButton);
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void searchCustomer(String phoneNumber, JTextField nameField, JLabel pointsLabel) {
        if (phoneNumber.isEmpty()) {
            nameField.setText("");
            pointsLabel.setText("0 điểm");
            return;
        }

        try {
            Customer customer = customerDAO.getCustomerByPhone(phoneNumber);
            if (customer != null) {
                nameField.setText(customer.getCustomerName());
                pointsLabel.setText(customer.getPoints() + " điểm");
                System.out.println("[PAYMENT] Found customer: " + customer.getCustomerName() + ", Points: " + customer.getPoints());
            } else {
                nameField.setText(""); // Để trống để người dùng nhập tên mới
                pointsLabel.setText("0 điểm");
                System.out.println("[PAYMENT] Customer not found for phone: " + phoneNumber);
            }
        } catch (Exception ex) {
            System.out.println("[PAYMENT ERROR] Error searching customer: " + ex.getMessage());
            nameField.setText("");
            pointsLabel.setText("0 điểm");
        }
    }
    
    private void handlePayment(JTextField phoneField, JTextField nameField, 
                               JCheckBox usePointsCheckBox, double finalAmount, double points) {
        // Kiểm tra xem invoice đã được lưu chưa
        if (invoice.getInvoiceId() == 0) {
            UIUtils.showErrorMessage(this, "Lỗi: Hóa đơn chưa được lưu!\nVui lòng lưu hóa đơn trước khi thanh toán.", "Lỗi");
            return;
        }
        
        String phoneNumber = phoneField.getText().trim();
        String customerName = nameField.getText().trim();
        
        // Kiểm tra nếu nhập số điện thoại nhưng không nhập tên
        if (!phoneNumber.isEmpty() && customerName.isEmpty()) {
            UIUtils.showWarningMessage(this, "Vui lòng nhập tên khách hàng!", "Thông báo");
            return;
        }
        
        Customer customer = null;
        if (!phoneNumber.isEmpty()) {
            customer = customerDAO.getCustomerByPhone(phoneNumber);
            if (customer == null) {
                if (!customerName.isEmpty()) {
                    customer = new Customer();
                    customer.setPhoneNumber(phoneNumber);
                    customer.setCustomerName(customerName);
                    int customerId = customerDAO.addCustomer(customer);
                    if (customerId > 0) {
                        customer.setCustomerId(customerId);
                    }
                }
            }
        }
        
        double pointsUsed = 0;
        if (usePointsCheckBox.isSelected() && customer != null) {
            pointsUsed = Math.min(customer.getPoints(), points);
            customer.setPoints(customer.getPoints() - pointsUsed);
        }
        
        // Store original amount for bill printing
        double originalAmount = invoice.getTotalAmount();
        
        // Update invoice
        invoice.setStatus("Đã thanh toán");
        invoice.setPaidDate(LocalDateTime.now());
        invoice.setDiscountAmount(discountAmount);
        invoice.setTotalAmount(finalAmount);
        invoice.setPointUsed(pointsUsed);
        
        try {
            boolean updateSuccess = invoiceDAO.updateInvoice(invoice);
            if (!updateSuccess) {
                System.err.println("[ERROR] UpdateInvoice trả về false cho InvoiceID: " + invoice.getInvoiceId());
                System.err.println("[ERROR] Không thể cập nhật hóa đơn trong database");
                UIUtils.showErrorMessage(this, "Lỗi cập nhật hóa đơn trong database!", "Lỗi");
                return;
            }
            
            System.out.println("[PAYMENT] Hóa đơn #" + invoice.getInvoiceId() + " được cập nhật trạng thái: " + invoice.getStatus());
            
            // Update table status
            boolean tableUpdateSuccess = tableDAO.updateTableStatus(invoice.getTableId(), "Trống");
            if (tableUpdateSuccess) {
                System.out.println("[PAYMENT] Bàn #" + invoice.getTableId() + " được cập nhật trạng thái: Trống");
            } else {
                System.err.println("[WARNING] Không thể cập nhật trạng thái bàn");
            }
            
            // Update customer points
            if (customer != null) {
                customerDAO.updateCustomerPoints(customer.getCustomerId(), (int)points);
                System.out.println("[PAYMENT] Khách hàng #" + customer.getCustomerId() + " được cộng " + (int)points + " điểm");
            }
            
            // Print bill if needed
            int result = UIUtils.showConfirmDialog(this, "Thanh toán thành công!\nBạn có muốn in hóa đơn không?", "Xác nhận");
            if (result == JOptionPane.YES_OPTION) {
                printBill(originalAmount);
            }
            
            UIUtils.showInfoMessage(this, "Hóa đơn #" + invoice.getInvoiceId() + " đã được thanh toán thành công!", "Thông báo");
            
            // Refresh parent frame (MainFrame) trước khi đóng
            if (parentFrame.getParent() instanceof JFrame) {
                JFrame mainFrame = (JFrame) parentFrame.getParent();
                mainFrame.repaint();
            }
            
            dispose();
            parentFrame.dispose();
        } catch (Exception ex) {
            System.err.println("[ERROR] Lỗi thanh toán: " + ex.getMessage());
            UIUtils.showErrorMessage(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi");
            ex.printStackTrace();
        }
    }
    
    private void printBill(double originalAmount) {
        StringBuilder bill = new StringBuilder();
        bill.append("========================================\n");
        bill.append("         HÓA ĐƠN THANH TOÁN\n");
        bill.append("========================================\n");
        bill.append("Bàn: ").append(invoice.getTableId()).append("\n");
        bill.append("Thời gian: ").append(invoice.getCreatedDate()).append("\n");
        bill.append("Hóa đơn số: ").append(invoice.getInvoiceId()).append("\n");
        bill.append("----------------------------------------\n");
        
        for (InvoiceDetail detail : details) {
            bill.append(String.format("%-30s %5d x %8.0f = %10.0f\n",
                "Món",
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getAmount()));
        }
        
        bill.append("----------------------------------------\n");
        bill.append(String.format("Tổng tiền: %20.0f đ\n", originalAmount));
        bill.append(String.format("Giảm giá: %21.0f đ\n", discountAmount));
        bill.append(String.format("Thành tiền: %19.0f đ\n", invoice.getTotalAmount()));
        bill.append("========================================\n");
        bill.append("Cảm ơn quý khách!\n");
        bill.append("========================================\n");
        
        String billContent = bill.toString();
        
        // In ra console
        System.out.println(billContent);
        
        // Lưu ra file
        String billPath = BillUtils.saveBillToFile(invoice.getInvoiceId(), billContent);
        
        // Hiển thị hóa đơn trên dialog
        String message = billContent + "\n\n";
        if (billPath != null) {
            message += "✓ Hóa đơn đã lưu: " + billPath;
        } else {
            message += "⚠ Lỗi lưu hóa đơn!";
        }
        
        UIUtils.showInfoMessage(this, message, "Hóa Đơn");
    }
}
