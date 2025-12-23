package com.cafe.ui;

import com.cafe.dao.CustomerDAO;
import com.cafe.dao.MenuDAO;
import com.cafe.model.Customer;
import com.cafe.model.MenuItem;
import com.cafe.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PointRedemptionFrame extends JDialog {
    private CustomerDAO customerDAO;
    private MenuDAO menuDAO;
    private JTextField phoneField;
    private JLabel customerInfoLabel;
    private JTable menuTable;
    private DefaultTableModel menuModel;
    private static final int POINTS_PER_ITEM = 1000;
    
    public PointRedemptionFrame(JFrame parent) {
        super(parent, "Đổi Điểm Lấy Món Ăn", true);
        this.customerDAO = new CustomerDAO();
        this.menuDAO = new MenuDAO();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(parent);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - Customer search
        JPanel searchPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm Khách Hàng"));
        
        searchPanel.add(new JLabel("Số Điện Thoại:"));
        phoneField = new JTextField();
        searchPanel.add(phoneField);
        
        JButton searchButton = new JButton("Tìm Kiếm");
        searchButton.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        searchButton.addActionListener(e -> searchCustomer());
        searchPanel.add(searchButton);
        
        customerInfoLabel = new JLabel("Chưa tìm khách hàng");
        customerInfoLabel.setFont(UIUtils.getFont("Arial", Font.PLAIN, 12));
        customerInfoLabel.setForeground(Color.GRAY);
        searchPanel.add(customerInfoLabel);
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Center panel - Menu list
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Chọn Món Ăn (1000 điểm/món)"));
        
        String[] columns = {"ID", "Tên Món", "Giá", "Loại"};
        menuModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        menuTable = new JTable(menuModel);
        menuTable.setRowHeight(25);
        menuTable.setFont(UIUtils.getFont("Arial", Font.PLAIN, 11));
        menuTable.getTableHeader().setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(menuTable);
        menuPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        
        // Bottom panel - Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        
        JButton redeemButton = new JButton("Xác Nhận Đổi Điểm");
        redeemButton.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        redeemButton.setPreferredSize(new Dimension(150, 35));
        redeemButton.setBackground(UIUtils.getColor(76, 175, 80));
        redeemButton.setForeground(Color.WHITE);
        redeemButton.addActionListener(e -> handleRedemption());
        buttonPanel.add(redeemButton);
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load menu items
        loadMenuItems();
    }
    
    private void loadMenuItems() {
        menuModel.setRowCount(0);
        try {
            List<MenuItem> items = menuDAO.getAllMenuItems();
            for (MenuItem item : items) {
                if ("Còn".equals(item.getStatus())) {  // Only available items
                    Object[] row = {
                        item.getMenuItemId(),
                        item.getItemName(),
                        String.format("%.0f đ", item.getPrice()),
                        item.getCategory()
                    };
                    menuModel.addRow(row);
                }
            }
            System.out.println("[POINTS] Loaded " + menuModel.getRowCount() + " menu items");
        } catch (Exception ex) {
            System.out.println("[POINTS ERROR] Failed to load menu: " + ex.getMessage());
            UIUtils.showErrorMessage(this, "Lỗi tải thực đơn: " + ex.getMessage(), "Lỗi");
        }
    }
    
    private void searchCustomer() {
        String phoneNumber = phoneField.getText().trim();
        if (phoneNumber.isEmpty()) {
            UIUtils.showErrorMessage(this, "Vui lòng nhập số điện thoại", "Lỗi");
            return;
        }
        
        try {
            Customer customer = customerDAO.getCustomerByPhone(phoneNumber);
            if (customer != null) {
                int pointsAvailable = customer.getPoints();
                int itemsCanRedeem = pointsAvailable / POINTS_PER_ITEM;
                customerInfoLabel.setText(String.format(
                    "Khách: %s | Điểm: %d | Có thể đổi: %d món",
                    customer.getCustomerName(),
                    pointsAvailable,
                    itemsCanRedeem
                ));
                customerInfoLabel.setForeground(new Color(76, 175, 80));
                System.out.println("[POINTS] Found customer: " + customer.getCustomerName());
                
                if (itemsCanRedeem == 0) {
                    UIUtils.showInfoMessage(this, "Khách hàng có " + pointsAvailable + " điểm, cần tối thiểu 1000 điểm để đổi", "Thông báo");
                }
            } else {
                customerInfoLabel.setText("Không tìm thấy khách hàng");
                customerInfoLabel.setForeground(Color.RED);
                System.out.println("[POINTS] Customer not found: " + phoneNumber);
            }
        } catch (Exception ex) {
            System.out.println("[POINTS ERROR] " + ex.getMessage());
            UIUtils.showErrorMessage(this, "Lỗi tìm kiếm: " + ex.getMessage(), "Lỗi");
        }
    }
    
    private void handleRedemption() {
        String phoneNumber = phoneField.getText().trim();
        if (phoneNumber.isEmpty()) {
            UIUtils.showErrorMessage(this, "Vui lòng nhập số điện thoại khách hàng", "Lỗi");
            return;
        }
        
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showErrorMessage(this, "Vui lòng chọn một món ăn", "Lỗi");
            return;
        }
        
        try {
            // Get customer
            Customer customer = customerDAO.getCustomerByPhone(phoneNumber);
            if (customer == null) {
                UIUtils.showErrorMessage(this, "Không tìm thấy khách hàng", "Lỗi");
                return;
            }
            
            // Check points
            if (customer.getPoints() < POINTS_PER_ITEM) {
                UIUtils.showErrorMessage(this, 
                    "Khách hàng không đủ điểm!\nHiện có: " + customer.getPoints() + " điểm\nCần: " + POINTS_PER_ITEM + " điểm",
                    "Lỗi");
                return;
            }
            
            // Get selected menu item
            int menuItemId = (int) menuModel.getValueAt(selectedRow, 0);
            String itemName = (String) menuModel.getValueAt(selectedRow, 1);
            
            MenuItem item = menuDAO.getMenuItemById(menuItemId);
            if (item == null) {
                UIUtils.showErrorMessage(this, "Lỗi: Không tìm thấy món ăn", "Lỗi");
                return;
            }
            
            // Confirm redemption
            int confirm = UIUtils.showConfirmDialog(this,
                "Xác nhận đổi điểm?\n\n" +
                "Khách: " + customer.getCustomerName() + "\n" +
                "Món ăn: " + itemName + "\n" +
                "Điểm trừ: " + POINTS_PER_ITEM + "\n" +
                "Điểm còn lại: " + (customer.getPoints() - POINTS_PER_ITEM),
                "Xác Nhận");
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Update customer points
                int newPoints = customer.getPoints() - POINTS_PER_ITEM;
                customerDAO.updateCustomerPoints(customer.getCustomerId(), newPoints);
                
                System.out.println("[POINTS] Redeemed " + itemName + " for customer " + customer.getCustomerName());
                System.out.println("[POINTS] Points: " + customer.getPoints() + " -> " + newPoints);
                
                UIUtils.showInfoMessage(this, 
                    "Đổi điểm thành công!\n\n" +
                    "Khách: " + customer.getCustomerName() + "\n" +
                    "Món nhận: " + itemName + "\n" +
                    "Điểm còn: " + newPoints,
                    "Thành Công");
                
                // Reset and refresh
                phoneField.setText("");
                customerInfoLabel.setText("Chưa tìm khách hàng");
                customerInfoLabel.setForeground(Color.GRAY);
                loadMenuItems();
            }
        } catch (Exception ex) {
            System.out.println("[POINTS ERROR] " + ex.getMessage());
            ex.printStackTrace();
            UIUtils.showErrorMessage(this, "Lỗi: " + ex.getMessage(), "Lỗi");
        }
    }
}
