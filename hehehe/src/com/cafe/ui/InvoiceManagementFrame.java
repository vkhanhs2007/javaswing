package com.cafe.ui;

import com.cafe.dao.InvoiceDAO;
import com.cafe.model.Invoice;
import com.cafe.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceManagementFrame extends JDialog {
    private InvoiceDAO invoiceDAO;
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> periodCombo;
    
    public InvoiceManagementFrame() {
        setTitle("Quản Lí Hóa Đơn");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setModal(true);
        
        this.invoiceDAO = new InvoiceDAO();
        
        initComponents();
        loadInvoices("Ngày");
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Thời Kỳ:"));
        
        periodCombo = new JComboBox<>(new String[]{"Ngày", "Tháng", "Quý", "Năm"});
        periodCombo.addActionListener(e -> loadInvoices((String) periodCombo.getSelectedItem()));
        filterPanel.add(periodCombo);
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Bàn", "Ngày Tạo", "Ngày Thanh Toán", "Tổng Tiền", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        invoiceTable = new JTable(tableModel);
        invoiceTable.setRowHeight(25);
        invoiceTable.setFont(UIUtils.getFont("Arial", Font.PLAIN, 11));
        invoiceTable.getTableHeader().setFont(UIUtils.getFont("Arial", Font.BOLD, 11));
        
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton viewButton = new JButton("Xem Chi Tiết");
        viewButton.setPreferredSize(new Dimension(120, 35));
        viewButton.addActionListener(e -> viewDetails());
        buttonPanel.add(viewButton);
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadInvoices(String period) {
        tableModel.setRowCount(0);
        LocalDateTime now = LocalDateTime.now();
        
        System.out.println("[UI] Lấy hóa đơn kỳ: " + period);
        List<Invoice> invoices = invoiceDAO.getInvoicesByDate(now, now, period);
        
        if (invoices == null || invoices.isEmpty()) {
            System.out.println("[UI] Không có hóa đơn cho kỳ: " + period);
            UIUtils.showInfoMessage(this, "Không có hóa đơn cho kỳ: " + period, "Thông báo");
            return;
        }
        
        System.out.println("[UI] Hiển thị " + invoices.size() + " hóa đơn");
        for (Invoice invoice : invoices) {
            Object[] row = {
                invoice.getInvoiceId(),
                invoice.getTableId(),
                invoice.getCreatedDate(),
                invoice.getPaidDate() != null ? invoice.getPaidDate() : "N/A",
                UIUtils.formatCurrency(invoice.getTotalAmount()),
                invoice.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void viewDetails() {
        int row = invoiceTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarningMessage(this, "Vui lòng chọn hóa đơn!", "Thông báo");
            return;
        }
        
        int invoiceId = (Integer) tableModel.getValueAt(row, 0);
        System.out.println("[UI] Xem chi tiết hóa đơn #" + invoiceId);
        
        Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
        
        if (invoice == null) {
            UIUtils.showErrorMessage(this, "Không tìm thấy hóa đơn #" + invoiceId, "Lỗi");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Hóa Đơn #").append(invoiceId).append("\n");
        details.append("Bàn: ").append(invoice.getTableId()).append("\n");
        details.append("Ngày Tạo: ").append(invoice.getCreatedDate()).append("\n");
        
        if (invoice.getPaidDate() != null) {
            details.append("Ngày Thanh Toán: ").append(invoice.getPaidDate()).append("\n");
        }
        
        details.append("Trạng Thái: ").append(invoice.getStatus()).append("\n");
        details.append("----------------------------------------\n");
        details.append("Tổng Tiền: ").append(UIUtils.formatCurrency(invoice.getTotalAmount())).append(" đ\n");
        details.append("Giảm Giá: ").append(UIUtils.formatCurrency(invoice.getDiscountAmount())).append(" đ\n");
        details.append("Thành Tiền: ").append(UIUtils.formatCurrency(invoice.getTotalAmount() - invoice.getDiscountAmount())).append(" đ\n");
        details.append("Điểm Sử Dụng: ").append((int)invoice.getPointUsed()).append(" điểm\n");
        
        UIUtils.showInfoMessage(this, details.toString(), "Chi Tiết Hóa Đơn");
    }
}
