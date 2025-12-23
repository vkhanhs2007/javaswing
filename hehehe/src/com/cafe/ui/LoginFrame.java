package com.cafe.ui;

import com.cafe.dao.UserDAO;
import com.cafe.model.User;
import com.cafe.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private User currentUser;
    
    public LoginFrame() {
        setTitle("Đăng Nhập - Quản Lí Quán Cafe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("Đăng Nhập Hệ Thống");
        titleLabel.setFont(UIUtils.getFont("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Username label
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(UIUtils.getFont("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(usernameLabel, gbc);
        
        // Username field
        usernameField = new JTextField(20);
        usernameField.setFont(UIUtils.getFont("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(usernameField, gbc);
        
        // Password label
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(UIUtils.getFont("Arial", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);
        
        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setFont(UIUtils.getFont("Arial", Font.PLAIN, 12));
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(passwordField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        loginButton = new JButton("Đăng Nhập");
        loginButton.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.addActionListener(this::handleLogin);
        buttonPanel.add(loginButton);
        
        exitButton = new JButton("Thoát");
        exitButton.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        exitButton.setPreferredSize(new Dimension(100, 35));
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            UIUtils.showWarningMessage(this, "Vui lòng nhập tên đăng nhập và mật khẩu!", "Thông báo");
            return;
        }
        
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByUsername(username);
            
            if (user != null && user.getPassword().equals(password)) {
                currentUser = user;
                UIUtils.showInfoMessage(this, "Đăng nhập thành công!", "Thông báo");
                dispose();
                new MainFrame(user);
            } else {
                UIUtils.showErrorMessage(this, "Tên đăng nhập hoặc mật khẩu sai!", "Lỗi");
                passwordField.setText("");
            }
        } catch (Exception ex) {
            System.err.println("[ERROR] Lỗi đăng nhập: " + ex.getMessage());
            UIUtils.showErrorMessage(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi");
            ex.printStackTrace();
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public static void main(String[] args) {
        // Kiểm tra Driver trước khi bắt đầu
        if (!com.cafe.config.DatabaseConfig.isDriverLoaded()) {
            JOptionPane.showMessageDialog(null,
                "Lỗi: SQL Server JDBC Driver không được tải!\n" +
                "Vui lòng kiểm tra file jar trong thư mục lib/",
                "Lỗi Khởi Động", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
