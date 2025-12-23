package com.cafe.ui;

import com.cafe.dao.UserDAO;
import com.cafe.model.User;
import com.cafe.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementFrame extends JDialog {
    private UserDAO userDAO;
    private JTable userTable;
    private DefaultTableModel tableModel;
    
    public UserManagementFrame() {
        setTitle("Quản Lí Người Dùng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setModal(true);
        
        try {
            this.userDAO = new UserDAO();
            initComponents();
            loadUsers();
            setVisible(true);
        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi khởi tạo UserManagementFrame: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải quản lí người dùng:\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Danh Sách Người Dùng Trong Hệ Thống");
        titleLabel.setFont(UIUtils.getFont("Arial", Font.BOLD, 14));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Tên Đăng Nhập", "Tên Đầy Đủ", "Vai Trò", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setRowHeight(25);
        userTable.setFont(UIUtils.getFont("Arial", Font.PLAIN, 11));
        userTable.getTableHeader().setFont(UIUtils.getFont("Arial", Font.BOLD, 11));
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton addButton = new JButton("Thêm Người Dùng");
        addButton.setPreferredSize(new Dimension(140, 35));
        addButton.addActionListener(e -> showAddDialog());
        buttonPanel.add(addButton);
        
        JButton editButton = new JButton("Sửa");
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.addActionListener(e -> showEditDialog());
        buttonPanel.add(editButton);
        
        JButton deleteButton = new JButton("Xóa");
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteUser());
        buttonPanel.add(deleteButton);
        
        JButton refreshButton = new JButton("Làm Mới");
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadUsers());
        buttonPanel.add(refreshButton);
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            List<User> users = userDAO.getAllUsers();
            if (users == null) {
                System.err.println("[ERROR] getAllUsers() trả về null");
                return;
            }
            System.out.println("[ADMIN] Tải danh sách người dùng - Tổng cộng: " + users.size() + " người");
            
            for (User user : users) {
                String status = user.isActive() ? "✓ Hoạt động" : "✗ Vô hiệu";
                Object[] row = {
                    user.getUserId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole(),
                    status
                };
                tableModel.addRow(row);
                System.out.println("[ADMIN]   - " + user.getUsername() + " (" + user.getFullName() + ") | " + user.getRole() + " | " + status);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi tải danh sách người dùng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Tên Đăng Nhập:"));
        JTextField usernameField = new JTextField();
        panel.add(usernameField);
        
        panel.add(new JLabel("Mật Khẩu:"));
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);
        
        panel.add(new JLabel("Tên Đầy Đủ:"));
        JTextField fullNameField = new JTextField();
        panel.add(fullNameField);
        
        panel.add(new JLabel("Vai Trò:"));
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Nhân viên", "Admin"});
        panel.add(roleCombo);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm Người Dùng", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            
            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                UIUtils.showWarningMessage(this, "Vui lòng điền tất cả thông tin!", "Thông báo");
                return;
            }
            
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFullName(fullName);
            user.setRole(role);
            user.setActive(true);
            
            if (userDAO.addUser(user)) {
                UIUtils.showInfoMessage(this, "Thêm người dùng thành công!\nTên đăng nhập: " + username, "Thông báo");
                System.out.println("[ADMIN] ✓ Thêm người dùng mới: " + username);
                loadUsers();
            } else {
                UIUtils.showErrorMessage(this, "Lỗi khi thêm người dùng!\n(Tên đăng nhập có thể bị trùng)", "Lỗi");
                System.err.println("[ADMIN] ✗ Lỗi thêm người dùng");
            }
        }
    }
    
    private void showEditDialog() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarningMessage(this, "Vui lòng chọn người dùng cần sửa!", "Thông báo");
            return;
        }
        
        int userId = (Integer) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        String fullName = (String) tableModel.getValueAt(row, 2);
        String role = (String) tableModel.getValueAt(row, 3);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Tên Đăng Nhập:"));
        JTextField usernameField = new JTextField(username);
        usernameField.setEditable(false);
        panel.add(usernameField);
        
        panel.add(new JLabel("Mật Khẩu (để trống = không thay đổi):"));
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);
        
        panel.add(new JLabel("Tên Đầy Đủ:"));
        JTextField fullNameField = new JTextField(fullName);
        panel.add(fullNameField);
        
        panel.add(new JLabel("Vai Trò:"));
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Nhân viên", "Admin"});
        roleCombo.setSelectedItem(role);
        panel.add(roleCombo);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa Người Dùng", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newFullName = fullNameField.getText().trim();
            String newRole = (String) roleCombo.getSelectedItem();
            String newPassword = new String(passwordField.getPassword());
            
            if (newFullName.isEmpty()) {
                UIUtils.showWarningMessage(this, "Tên đầy đủ không được để trống!", "Thông báo");
                return;
            }
            
            User user = new User();
            user.setUserId(userId);
            user.setUsername(username);
            user.setFullName(newFullName);
            user.setRole(newRole);
            
            if (!newPassword.isEmpty()) {
                user.setPassword(newPassword);
            }
            
            if (userDAO.updateUser(user)) {
                UIUtils.showInfoMessage(this, "Cập nhật người dùng thành công!", "Thông báo");
                System.out.println("[ADMIN] ✓ Cập nhật người dùng: " + username);
                loadUsers();
            } else {
                UIUtils.showErrorMessage(this, "Lỗi khi cập nhật người dùng!", "Lỗi");
                System.err.println("[ADMIN] ✗ Lỗi cập nhật người dùng");
            }
        }
    }
    
    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarningMessage(this, "Vui lòng chọn người dùng cần xóa!", "Thông báo");
            return;
        }
        
        int userId = (Integer) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        String fullName = (String) tableModel.getValueAt(row, 2);
        
        if (UIUtils.showConfirmDialog(this, 
            "Bạn chắc chắn muốn xóa người dùng:\n" + fullName + " (" + username + ")?", 
            "Xác nhận xóa") == JOptionPane.YES_OPTION) {
            
            if (userDAO.deleteUser(userId)) {
                UIUtils.showInfoMessage(this, "Xóa người dùng thành công!", "Thông báo");
                System.out.println("[ADMIN] ✓ Xóa người dùng: " + username);
                loadUsers();
            } else {
                UIUtils.showErrorMessage(this, "Lỗi khi xóa người dùng!", "Lỗi");
                System.err.println("[ADMIN] ✗ Lỗi xóa người dùng");
            }
        }
    }
