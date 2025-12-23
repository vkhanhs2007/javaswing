package com.cafe.ui;

import com.cafe.dao.MenuItemDAO;
import com.cafe.model.MenuItem;
import com.cafe.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MenuManagementFrame extends JDialog {
    private MenuItemDAO menuItemDAO;
    private JTable menuTable;
    private DefaultTableModel tableModel;
    
    public MenuManagementFrame(JFrame parentFrame) {
        super(parentFrame, "Quản Lí Thực Đơn", true);
        
        this.menuItemDAO = new MenuItemDAO();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(parentFrame);
        
        initComponents();
        loadMenuItems();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columns = {"ID", "Tên Món", "Loại", "Giá", "Mô Tả", "Giảm Giá %"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        menuTable = new JTable(tableModel);
        menuTable.setRowHeight(25);
        menuTable.setFont(UIUtils.getFont("Arial", Font.PLAIN, 11));
        menuTable.getTableHeader().setFont(UIUtils.getFont("Arial", Font.BOLD, 11));
        
        JScrollPane scrollPane = new JScrollPane(menuTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton addButton = new JButton("Thêm Món");
        addButton.setPreferredSize(new Dimension(100, 35));
        addButton.addActionListener(e -> showAddDialog());
        buttonPanel.add(addButton);
        
        JButton editButton = new JButton("Sửa Món");
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.addActionListener(e -> showEditDialog());
        buttonPanel.add(editButton);
        
        JButton deleteButton = new JButton("Xóa Món");
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.addActionListener(e -> handleDelete());
        buttonPanel.add(deleteButton);
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadMenuItems() {
        tableModel.setRowCount(0);
        List<MenuItem> items = menuItemDAO.getAllMenuItems();
        for (MenuItem item : items) {
            Object[] row = {
                item.getMenuItemId(),
                item.getItemName(),
                item.getCategory(),
                UIUtils.formatCurrency(item.getPrice()),
                item.getDescription(),
                item.getDiscount()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Tên Món:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);
        
        panel.add(new JLabel("Loại:"));
        JTextField categoryField = new JTextField();
        panel.add(categoryField);
        
        panel.add(new JLabel("Giá:"));
        JTextField priceField = new JTextField();
        panel.add(priceField);
        
        panel.add(new JLabel("Mô Tả:"));
        JTextField descField = new JTextField();
        panel.add(descField);
        
        panel.add(new JLabel("Giảm Giá (%):"));
        JTextField discountField = new JTextField("0");
        panel.add(discountField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm Món", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            MenuItem item = new MenuItem();
            item.setItemName(nameField.getText().trim());
            item.setCategory(categoryField.getText().trim());
            item.setPrice(UIUtils.parseDouble(priceField.getText(), 0));
            item.setDescription(descField.getText().trim());
            item.setDiscount(UIUtils.parseDouble(discountField.getText(), 0));
            item.setAvailable(true);
            
            if (menuItemDAO.addMenuItem(item)) {
                UIUtils.showInfoMessage(this, "Thêm món thành công!", "Thông báo");
                loadMenuItems();
            } else {
                UIUtils.showErrorMessage(this, "Lỗi khi thêm món!", "Lỗi");
            }
        }
    }
    
    private void showEditDialog() {
        int row = menuTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarningMessage(this, "Vui lòng chọn món cần sửa!", "Thông báo");
            return;
        }
        
        int itemId = (Integer) tableModel.getValueAt(row, 0);
        MenuItem item = menuItemDAO.getMenuItemById(itemId);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Tên Món:"));
        JTextField nameField = new JTextField(item.getItemName());
        panel.add(nameField);
        
        panel.add(new JLabel("Loại:"));
        JTextField categoryField = new JTextField(item.getCategory());
        panel.add(categoryField);
        
        panel.add(new JLabel("Giá:"));
        JTextField priceField = new JTextField(String.valueOf(item.getPrice()));
        panel.add(priceField);
        
        panel.add(new JLabel("Mô Tả:"));
        JTextField descField = new JTextField(item.getDescription());
        panel.add(descField);
        
        panel.add(new JLabel("Giảm Giá (%):"));
        JTextField discountField = new JTextField(String.valueOf(item.getDiscount()));
        panel.add(discountField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa Món", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            item.setItemName(nameField.getText().trim());
            item.setCategory(categoryField.getText().trim());
            item.setPrice(UIUtils.parseDouble(priceField.getText(), 0));
            item.setDescription(descField.getText().trim());
            item.setDiscount(UIUtils.parseDouble(discountField.getText(), 0));
            
            if (menuItemDAO.updateMenuItem(item)) {
                UIUtils.showInfoMessage(this, "Cập nhật món thành công!", "Thông báo");
                loadMenuItems();
            } else {
                UIUtils.showErrorMessage(this, "Lỗi khi cập nhật món!", "Lỗi");
            }
        }
    }
    
    private void handleDelete() {
        int row = menuTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarningMessage(this, "Vui lòng chọn món cần xóa!", "Thông báo");
            return;
        }
        
        int itemId = (Integer) tableModel.getValueAt(row, 0);
        if (UIUtils.showConfirmDialog(this, "Bạn chắc chắn muốn xóa món này?", "Xác nhận") == JOptionPane.YES_OPTION) {
            if (menuItemDAO.deleteMenuItem(itemId)) {
                UIUtils.showInfoMessage(this, "Xóa món thành công!", "Thông báo");
                loadMenuItems();
            } else {
                UIUtils.showErrorMessage(this, "Lỗi khi xóa món!", "Lỗi");
            }
        }
    }
}
