package com.cafe.ui;

import com.cafe.dao.MenuItemDAO;
import com.cafe.dao.InvoiceDAO;
import com.cafe.dao.TableDAO;
import com.cafe.model.MenuItem;
import com.cafe.model.Invoice;
import com.cafe.model.InvoiceDetail;
import com.cafe.model.Table;
import com.cafe.util.UIUtils;
import com.cafe.util.DiscountUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class OrderFrame extends JFrame {
    private int tableId;
    private JFrame parentFrame;
    private MenuItemDAO menuItemDAO;
    private InvoiceDAO invoiceDAO;
    private TableDAO tableDAO;
    private Invoice currentInvoice;
    private List<InvoiceDetail> orderDetails;
    private JComboBox<String> categoryCombo;
    private JList<MenuItem> menuList;
    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private JLabel totalLabel;
    private JLabel pointsLabel;
    private JSpinner quantitySpinner;
    
    public OrderFrame(int tableId, JFrame parentFrame) {
        this.tableId = tableId;
        this.parentFrame = parentFrame;
        this.menuItemDAO = new MenuItemDAO();
        this.invoiceDAO = new InvoiceDAO();
        this.tableDAO = new TableDAO();
        this.orderDetails = new ArrayList<>();
        
        loadCurrentInvoice();
        
        // Cập nhật tiêu đề dựa trên trạng thái hóa đơn
        String title = "Gọi Món - Bàn " + tableId;
        if (currentInvoice.getInvoiceId() > 0) {
            title += " [Hóa Đơn #" + currentInvoice.getInvoiceId() + " - Chờ Thanh Toán]";
        } else {
            title += " [Hóa Đơn Mới]";
        }
        
        setTitle(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Thêm listener để refresh MainFrame khi OrderFrame đóng
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (parentFrame != null && parentFrame.isVisible()) {
                    // Gọi loadTables nếu MainFrame có method này
                    if (parentFrame instanceof MainFrame) {
                        MainFrame mainFrame = (MainFrame) parentFrame;
                        mainFrame.loadTables();
                        System.out.println("[ORDER] Refresh danh sách bàn trong MainFrame");
                    }
                }
            }
        });
        
        initComponents();
        setVisible(true);
        
        // Verify - Kiểm tra xem hóa đơn đã được tải/tạo đúng không
        System.out.println("[ORDER] ═══════════════════════════════════════════");
        System.out.println("[ORDER] VERIFY: Hóa đơn được tải/tạo");
        System.out.println("[ORDER] ═══════════════════════════════════════════");
        System.out.println("[ORDER] Bàn: #" + tableId);
        System.out.println("[ORDER] Hóa đơn ID: " + currentInvoice.getInvoiceId());
        System.out.println("[ORDER] Status: " + currentInvoice.getStatus());
        System.out.println("[ORDER] Tổng tiền: " + currentInvoice.getTotalAmount() + " đ");
        System.out.println("[ORDER] Chi tiết: " + orderDetails.size() + " mục");
        System.out.println("[ORDER] ═══════════════════════════════════════════");
        
        if (orderDetails.size() > 0) {
            System.out.println("[ORDER] Chi tiết hóa đơn:");
            for (com.cafe.model.InvoiceDetail detail : orderDetails) {
                System.out.println("[ORDER]   - Món #" + detail.getMenuItemId() 
                                 + " | SL: " + detail.getQuantity()
                                 + " | Giá: " + detail.getUnitPrice()
                                 + " | Thành tiền: " + detail.getAmount());
            }
        }
    }
    
    private void loadCurrentInvoice() {
        // Tìm hóa đơn tạm (chưa thanh toán) cho bàn này
        System.out.println("[ORDER] Đang tải hóa đơn cho bàn #" + tableId);
        currentInvoice = invoiceDAO.getInvoiceByTableId(tableId);
        
        if (currentInvoice != null) {
            // Tải lại chi tiết hóa đơn từ database
            orderDetails = invoiceDAO.getInvoiceDetails(currentInvoice.getInvoiceId());
            System.out.println("[ORDER] ✓ Tải hóa đơn tạm #" + currentInvoice.getInvoiceId() + " cho bàn #" + tableId 
                             + " | Status: " + currentInvoice.getStatus() 
                             + " | Tổng tiền: " + currentInvoice.getTotalAmount() + " đ"
                             + " | Chi tiết: " + orderDetails.size() + " mục");
        } else {
            // Hóa đơn không tồn tại hoặc đã thanh toán, tạo hóa đơn mới
            System.out.println("[ORDER] ⚠ Không tìm thấy hóa đơn tạm cho bàn #" + tableId);
            
            // Debug: Kiểm tra tất cả hóa đơn của bàn
            java.util.List<com.cafe.model.Invoice> allInvoices = invoiceDAO.getAllInvoicesByTableId(tableId);
            if (!allInvoices.isEmpty()) {
                System.out.println("[ORDER] DEBUG: Tất cả hóa đơn của bàn #" + tableId + ":");
                for (com.cafe.model.Invoice inv : allInvoices) {
                    System.out.println("  - Hóa đơn #" + inv.getInvoiceId() + " | Status: " + inv.getStatus() 
                                     + " | Tổng tiền: " + inv.getTotalAmount() + " đ");
                }
            } else {
                System.out.println("[ORDER] DEBUG: Bàn #" + tableId + " không có hóa đơn nào trong database");
            }
            
            System.out.println("[ORDER] → Tạo hóa đơn mới");
            currentInvoice = new Invoice();
            currentInvoice.setTableId(tableId);
            currentInvoice.setCreatedDate(LocalDateTime.now());
            currentInvoice.setStatus("Tạm");
            currentInvoice.setTotalAmount(0);
            orderDetails.clear();
        }
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Left panel - Menu selection
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        
        // Category selection
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.add(new JLabel("Loại Món:"), BorderLayout.WEST);
        
        categoryCombo = new JComboBox<>();
        categoryCombo.addItem("Tất cả");
        for (String category : menuItemDAO.getAllCategories()) {
            categoryCombo.addItem(category);
        }
        categoryCombo.addActionListener(e -> loadMenuItems());
        categoryPanel.add(categoryCombo, BorderLayout.CENTER);
        leftPanel.add(categoryPanel, BorderLayout.NORTH);
        
        // Menu list
        menuList = new JList<>();
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane menuScrollPane = new JScrollPane(menuList);
        menuScrollPane.setBorder(BorderFactory.createTitledBorder("Thực Đơn"));
        leftPanel.add(menuScrollPane, BorderLayout.CENTER);
        
        // Quantity and Add button
        JPanel quantityPanel = new JPanel(new FlowLayout());
        quantityPanel.add(new JLabel("Số Lượng:"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantityPanel.add(quantitySpinner);
        
        JButton addButton = new JButton("Thêm Vào Hóa Đơn");
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.addActionListener(e -> addToOrder());
        quantityPanel.add(addButton);
        
        leftPanel.add(quantityPanel, BorderLayout.SOUTH);
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Right panel - Order details
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        
        // Order table
        String[] columns = {"Tên Món", "Đơn Giá", "SL", "Thành Tiền"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(25);
        orderTable.setFont(UIUtils.getFont("Arial", Font.PLAIN, 11));
        orderTable.getTableHeader().setFont(UIUtils.getFont("Arial", Font.BOLD, 11));
        
        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        orderScrollPane.setBorder(BorderFactory.createTitledBorder("Chi Tiết Hóa Đơn"));
        rightPanel.add(orderScrollPane, BorderLayout.CENTER);
        
        // Total panel
        JPanel totalPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        totalPanel.setBorder(BorderFactory.createTitledBorder("Tính Tiền"));
        
        totalPanel.add(new JLabel("Tổng Tiền:"));
        totalLabel = new JLabel("0 đ");
        totalLabel.setFont(UIUtils.getFont("Arial", Font.BOLD, 14));
        totalLabel.setForeground(Color.RED);
        totalPanel.add(totalLabel);
        
        totalPanel.add(new JLabel("Điểm Tích Lũy:"));
        pointsLabel = new JLabel("0 điểm");
        pointsLabel.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        totalPanel.add(pointsLabel);
        
        totalPanel.add(new JLabel("Giảm Giá:"));
        JSpinner discountSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0, 100, 1));
        totalPanel.add(discountSpinner);
        
        rightPanel.add(totalPanel, BorderLayout.SOUTH);
        
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton removeButton = new JButton("Xóa Món");
        removeButton.setPreferredSize(new Dimension(100, 35));
        removeButton.addActionListener(e -> removeFromOrder());
        buttonPanel.add(removeButton);
        
        JButton saveButton = new JButton("Lưu Hóa Đơn Tạm");
        saveButton.setPreferredSize(new Dimension(150, 35));
        saveButton.addActionListener(e -> saveTempInvoice());
        buttonPanel.add(saveButton);
        
        JButton paymentButton = new JButton("Thanh Toán");
        paymentButton.setPreferredSize(new Dimension(100, 35));
        paymentButton.setBackground(Color.GREEN);
        paymentButton.setForeground(Color.WHITE);
        paymentButton.addActionListener(e -> handlePayment(discountSpinner));
        buttonPanel.add(paymentButton);
        
        JButton transferButton = new JButton("Chuyển Bàn");
        transferButton.setPreferredSize(new Dimension(100, 35));
        transferButton.addActionListener(e -> handleTransferTable());
        buttonPanel.add(transferButton);
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        loadMenuItems();
        refreshOrderTable();
    }
    
    private void loadMenuItems() {
        DefaultListModel<MenuItem> model = new DefaultListModel<>();
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        
        List<MenuItem> items;
        if ("Tất cả".equals(selectedCategory)) {
            items = menuItemDAO.getAllMenuItems();
        } else {
            items = menuItemDAO.getMenuItemsByCategory(selectedCategory);
        }
        
        for (MenuItem item : items) {
            model.addElement(item);
        }
        menuList.setModel(model);
    }
    
    private void addToOrder() {
        MenuItem selectedItem = menuList.getSelectedValue();
        if (selectedItem == null) {
            UIUtils.showWarningMessage(this, "Vui lòng chọn món!", "Thông báo");
            return;
        }
        
        int quantity = (Integer) quantitySpinner.getValue();
        double price = selectedItem.getPrice();
        
        // Check if item already in order
        boolean found = false;
        for (InvoiceDetail detail : orderDetails) {
            if (detail.getMenuItemId() == selectedItem.getMenuItemId()) {
                detail.setQuantity(detail.getQuantity() + quantity);
                detail.setAmount(detail.getQuantity() * detail.getUnitPrice());
                found = true;
                break;
            }
        }
        
        if (!found) {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setMenuItemId(selectedItem.getMenuItemId());
            detail.setQuantity(quantity);
            detail.setUnitPrice(price);
            detail.setAmount(quantity * price);
            orderDetails.add(detail);
        }
        
        refreshOrderTable();
        quantitySpinner.setValue(1);
    }
    
    private void removeFromOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarningMessage(this, "Vui lòng chọn món cần xóa!", "Thông báo");
            return;
        }
        
        orderDetails.remove(row);
        refreshOrderTable();
    }
    
    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        double total = 0;
        
        for (InvoiceDetail detail : orderDetails) {
            MenuItem item = menuItemDAO.getMenuItemById(detail.getMenuItemId());
            if (item == null) {
                System.err.println("[WARNING] Không tìm thấy món ăn có ID: " + detail.getMenuItemId());
                continue;
            }
            
            Object[] row = {
                item.getItemName(),
                UIUtils.formatCurrency(detail.getUnitPrice()),
                detail.getQuantity(),
                UIUtils.formatCurrency(detail.getAmount())
            };
            orderTableModel.addRow(row);
            total += detail.getAmount();
        }
        
        currentInvoice.setTotalAmount(total);
        totalLabel.setText(UIUtils.formatCurrency(total) + " đ");
        pointsLabel.setText(((int)DiscountUtils.calculatePoints(total)) + " điểm");
    }
    
    private void saveTempInvoice() {
        if (orderDetails.isEmpty()) {
            UIUtils.showWarningMessage(this, "Vui lòng thêm món trước!", "Thông báo");
            return;
        }
        
        System.out.println("[SAVE] ═══════════════════════════════════════════");
        System.out.println("[SAVE] Bắt đầu lưu hóa đơn tạm cho bàn #" + tableId);
        System.out.println("[SAVE] ═══════════════════════════════════════════");
        
        // Bước 1: Đảm bảo tổng tiền được tính đúng
        double total = 0;
        for (InvoiceDetail detail : orderDetails) {
            total += detail.getAmount();
        }
        currentInvoice.setTotalAmount(total);
        currentInvoice.setStatus("Tạm");
        System.out.println("[SAVE] Step 1: Tính tổng tiền = " + total + " đ");
        
        // Bước 2: Thêm/Cập nhật hóa đơn
        if (currentInvoice.getInvoiceId() == 0) {
            System.out.println("[SAVE] Step 2: Hóa đơn mới - Lưu vào database");
            int invoiceId = invoiceDAO.addInvoice(currentInvoice);
            if (invoiceId > 0) {
                currentInvoice.setInvoiceId(invoiceId);
                System.out.println("[SAVE]        ✓ Hóa đơn mới được tạo với ID: #" + invoiceId);
            } else {
                System.err.println("[SAVE]        ✗ Lỗi tạo hóa đơn mới");
                UIUtils.showErrorMessage(this, "Lỗi lưu hóa đơn!", "Lỗi");
                return;
            }
        } else {
            System.out.println("[SAVE] Step 2: Hóa đơn cũ - Cập nhật ID: #" + currentInvoice.getInvoiceId());
            // Cập nhật hóa đơn hiện tại
            boolean updateSuccess = invoiceDAO.updateInvoice(currentInvoice);
            if (updateSuccess) {
                System.out.println("[SAVE]        ✓ Hóa đơn được cập nhật");
            } else {
                System.err.println("[SAVE]        ✗ Lỗi cập nhật hóa đơn");
            }
        }
        
        // Bước 3: Lưu chi tiết hóa đơn
        System.out.println("[SAVE] Step 3: Lưu chi tiết hóa đơn");
        int detailsSaved = 0;
        for (InvoiceDetail detail : orderDetails) {
            if (detail.getDetailId() == 0) {
                detail.setInvoiceId(currentInvoice.getInvoiceId());
                invoiceDAO.addInvoiceDetail(detail);
                detailsSaved++;
                System.out.println("[SAVE]        - Lưu chi tiết: Món ID " + detail.getMenuItemId() 
                                 + " x " + detail.getQuantity() + " = " + detail.getAmount() + " đ");
            }
        }
        System.out.println("[SAVE]        ✓ Lưu tổng cộng " + detailsSaved + " chi tiết");
        
        // Bước 4: Đảm bảo trạng thái hóa đơn là "Tạm"
        System.out.println("[SAVE] Step 4: Đảm bảo trạng thái hóa đơn = 'Tạm'");
        currentInvoice.setStatus("Tạm");
        invoiceDAO.updateInvoice(currentInvoice);
        System.out.println("[SAVE]        ✓ Status được set thành 'Tạm'");
        
        // Bước 5: Cập nhật trạng thái bàn
        System.out.println("[SAVE] Step 5: Cập nhật trạng thái bàn");
        boolean tableUpdateSuccess = tableDAO.updateTableStatus(tableId, "Đang phục vụ");
        if (tableUpdateSuccess) {
            System.out.println("[SAVE]        ✓ Bàn #" + tableId + " được cập nhật trạng thái: Đang phục vụ");
        } else {
            System.err.println("[SAVE]        ✗ Lỗi cập nhật trạng thái bàn");
        }
        
        System.out.println("[SAVE] ═══════════════════════════════════════════");
        System.out.println("[SAVE] ✓ HOÀN TẤT: Hóa đơn #" + currentInvoice.getInvoiceId() 
                         + " được lưu với tổng tiền " + total + " đ");
        System.out.println("[SAVE] ═══════════════════════════════════════════");
        
        UIUtils.showInfoMessage(this, "Lưu hóa đơn tạm thành công!\nHóa đơn #" + currentInvoice.getInvoiceId() 
                                     + "\nTổng tiền: " + UIUtils.formatCurrency(total) + " đ", "Thông báo");
    }
    
    private void handlePayment(JSpinner discountSpinner) {
        if (orderDetails.isEmpty()) {
            UIUtils.showWarningMessage(this, "Không có món để thanh toán!", "Thông báo");
            return;
        }
        
        // Lưu hóa đơn tạm trước khi thanh toán (nếu chưa lưu)
        if (currentInvoice.getInvoiceId() == 0) {
            int invoiceId = invoiceDAO.addInvoice(currentInvoice);
            if (invoiceId > 0) {
                currentInvoice.setInvoiceId(invoiceId);
                System.out.println("[ORDER] Hóa đơn mới được tạo: #" + invoiceId);
            } else {
                UIUtils.showErrorMessage(this, "Lỗi lưu hóa đơn!", "Lỗi");
                return;
            }
            
            // Lưu các chi tiết hóa đơn
            for (InvoiceDetail detail : orderDetails) {
                if (detail.getDetailId() == 0) {
                    detail.setInvoiceId(currentInvoice.getInvoiceId());
                    invoiceDAO.addInvoiceDetail(detail);
                }
            }
            
            // Cập nhật trạng thái bàn
            tableDAO.updateTableStatus(tableId, "Đang phục vụ");
            System.out.println("[ORDER] Chi tiết hóa đơn đã được lưu");
        }
        
        double discountPercent = (Double) discountSpinner.getValue();
        double totalAmount = currentInvoice.getTotalAmount();
        double discountAmount = (totalAmount * discountPercent) / 100;
        
        new PaymentFrame(currentInvoice, orderDetails, discountAmount, this);
    }
    
    private void handleTransferTable() {
        List<Table> tables = tableDAO.getAllTables();
        List<String> tableNames = new ArrayList<>();
        Map<String, Integer> tableMap = new HashMap<>();
        
        for (Table table : tables) {
            if (table.getTableId() != tableId) {
                tableNames.add(table.getTableName());
                tableMap.put(table.getTableName(), table.getTableId());
            }
        }
        
        if (tableNames.isEmpty()) {
            UIUtils.showWarningMessage(this, "Không có bàn khác để chuyển!", "Thông báo");
            return;
        }
        
        String[] options = tableNames.toArray(new String[0]);
        String selectedTable = (String) JOptionPane.showInputDialog(
            this, "Chọn bàn cần chuyển đến:", "Chuyển Bàn",
            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (selectedTable != null) {
            Integer newTableId = tableMap.get(selectedTable);
            if (newTableId != null && tableDAO.transferTable(tableId, newTableId)) {
                UIUtils.showInfoMessage(this, "Chuyển bàn thành công!", "Thông báo");
                dispose();
            } else {
                UIUtils.showErrorMessage(this, "Lỗi khi chuyển bàn!", "Lỗi");
            }
        }
    }
}
