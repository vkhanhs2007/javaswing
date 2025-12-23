package com.cafe.ui;

import com.cafe.dao.TableDAO;
import com.cafe.model.Table;
import com.cafe.model.User;
import com.cafe.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainFrame extends JFrame {
    private User currentUser;
    private TableDAO tableDAO;
    private JPanel tablePanel;
    private JTable tableListTable;
    private DefaultTableModel tableModel;
    private JMenuBar menuBar;
    
    public MainFrame(User user) {
        this.currentUser = user;
        this.tableDAO = new TableDAO();
        
        setTitle("Quản Lí Quán Cafe - " + user.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        initComponents();
        loadTables();
        setVisible(true);
    }
    
    private void initComponents() {
        // Create menu bar
        createMenuBar();
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(UIUtils.getColor(70, 130, 180));
        topPanel.setPreferredSize(new Dimension(0, 50));
        JLabel titleLabel = new JLabel("QUẢN LÍ BÀN ĂN");
        titleLabel.setFont(UIUtils.getFont("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Table list
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"ID", "Tên Bàn", "Sức Chứa", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableListTable = new JTable(tableModel);
        tableListTable.setRowHeight(30);
        tableListTable.setFont(UIUtils.getFont("Arial", Font.PLAIN, 12));
        tableListTable.getTableHeader().setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        tableListTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick(e);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableListTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        
        JButton orderButton = new JButton("Gọi Món");
        orderButton.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        orderButton.setPreferredSize(new Dimension(120, 35));
        orderButton.addActionListener(e -> handleOrderClick());
        buttonPanel.add(orderButton);
        
        JButton addTableButton = new JButton("Thêm Bàn");
        addTableButton.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        addTableButton.setPreferredSize(new Dimension(120, 35));
        addTableButton.addActionListener(e -> showAddTableDialog());
        buttonPanel.add(addTableButton);
        
        JButton deleteTableButton = new JButton("Xóa Bàn");
        deleteTableButton.setFont(UIUtils.getFont("Arial", Font.BOLD, 12));
        deleteTableButton.setPreferredSize(new Dimension(120, 35));
        deleteTableButton.addActionListener(e -> handleDeleteTable());
        buttonPanel.add(deleteTableButton);
        
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Menu Chức năng
        JMenu functionMenu = new JMenu("Chức Năng");
        functionMenu.setFont(UIUtils.getFont("Arial", Font.PLAIN, 12));
        
        JMenuItem tableManagementItem = new JMenuItem("Quản Lí Bàn");
        tableManagementItem.addActionListener(e -> refreshTableList());
        functionMenu.add(tableManagementItem);
        
        JMenuItem menuManagementItem = new JMenuItem("Quản Lí Thực Đơn");
        menuManagementItem.addActionListener(e -> new MenuManagementFrame(this));
        functionMenu.add(menuManagementItem);
        
        JMenuItem invoiceManagementItem = new JMenuItem("Quản Lí Hóa Đơn");
        invoiceManagementItem.addActionListener(e -> new InvoiceManagementFrame());
        functionMenu.add(invoiceManagementItem);
        
        JMenuItem pointRedemptionItem = new JMenuItem("Đổi Điểm Lấy Món Ăn");
        pointRedemptionItem.addActionListener(e -> new PointRedemptionFrame(this));
        functionMenu.add(pointRedemptionItem);
        
        functionMenu.addSeparator();
        JMenuItem logoutItem = new JMenuItem("Đăng Xuất");
        logoutItem.addActionListener(e -> logout());
        functionMenu.add(logoutItem);
        
        menuBar.add(functionMenu);
        
        // Menu Báo cáo
        JMenu reportMenu = new JMenu("Báo Cáo");
        reportMenu.setFont(UIUtils.getFont("Arial", Font.PLAIN, 12));
        
        JMenuItem statisticsItem = new JMenuItem("Thống Kê");
        statisticsItem.addActionListener(e -> new StatisticsFrame());
        reportMenu.add(statisticsItem);
        
        menuBar.add(reportMenu);
        
        // Menu Admin
        if ("Admin".equals(currentUser.getRole())) {
            JMenu adminMenu = new JMenu("Admin");
            adminMenu.setFont(UIUtils.getFont("Arial", Font.PLAIN, 12));
            
            JMenuItem userManagementItem = new JMenuItem("Quản Lí Người Dùng");
            userManagementItem.addActionListener(e -> new UserManagementFrame());
            adminMenu.add(userManagementItem);
            
            menuBar.add(adminMenu);
        }
        
        setJMenuBar(menuBar);
    }
    
    public void loadTables() {
        tableModel.setRowCount(0);
        List<Table> tables = tableDAO.getAllTables();
        for (Table table : tables) {
            Object[] row = {
                table.getTableId(),
                table.getTableName(),
                table.getCapacity(),
                table.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void refreshTableList() {
        loadTables();
        UIUtils.showInfoMessage(this, "Danh sách bàn đã được cập nhật!", "Thông báo");
    }
    
    private void handleTableClick(MouseEvent e) {
        if (e.getClickCount() == 1) {
            int row = tableListTable.getSelectedRow();
            if (row >= 0 && e.getButton() == MouseEvent.BUTTON3) {
                // Right click context menu
                showContextMenu(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    
    private void showContextMenu(Component comp, int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem addItem = new JMenuItem("Thêm Bàn");
        addItem.addActionListener(e -> showAddTableDialog());
        popupMenu.add(addItem);
        
        JMenuItem deleteItem = new JMenuItem("Xóa Bàn");
        deleteItem.addActionListener(e -> handleDeleteTable());
        popupMenu.add(deleteItem);
        
        popupMenu.show(comp, x, y);
    }
    
    private void showAddTableDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Tên Bàn:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);
        panel.add(new JLabel("Sức Chứa:"));
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(4, 2, 20, 1));
        panel.add(capacitySpinner);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm Bàn Mới", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Table table = new Table();
            table.setTableName(nameField.getText().trim());
            table.setCapacity((Integer) capacitySpinner.getValue());
            table.setStatus("Trống");
            
            if (tableDAO.addTable(table)) {
                UIUtils.showInfoMessage(this, "Thêm bàn thành công!", "Thông báo");
                loadTables();
            } else {
                UIUtils.showErrorMessage(this, "Lỗi khi thêm bàn!", "Lỗi");
            }
        }
    }
    
    private void handleDeleteTable() {
        int row = tableListTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarningMessage(this, "Vui lòng chọn bàn cần xóa!", "Thông báo");
            return;
        }
        
        int tableId = (Integer) tableModel.getValueAt(row, 0);
        if (UIUtils.showConfirmDialog(this, "Bạn chắc chắn muốn xóa bàn này?", "Xác nhận") == JOptionPane.YES_OPTION) {
            if (tableDAO.deleteTable(tableId)) {
                UIUtils.showInfoMessage(this, "Xóa bàn thành công!", "Thông báo");
                loadTables();
            } else {
                UIUtils.showErrorMessage(this, "Lỗi khi xóa bàn!", "Lỗi");
            }
        }
    }
    
    private void handleOrderClick() {
        int row = tableListTable.getSelectedRow();
        if (row < 0) {
            UIUtils.showWarningMessage(this, "Vui lòng chọn bàn!", "Thông báo");
            return;
        }
        
        int tableId = (Integer) tableModel.getValueAt(row, 0);
        String tableStatus = (String) tableModel.getValueAt(row, 3);
        
        System.out.println("[MAIN] Click vào bàn #" + tableId + " | Trạng thái: " + tableStatus);
        
        // Cho phép click vào bàn Trống hoặc Đang phục vụ
        if ("Trống".equals(tableStatus)) {
            System.out.println("[MAIN] → Mở OrderFrame với hóa đơn mới");
            new OrderFrame(tableId, this);
        } else if ("Đang phục vụ".equals(tableStatus)) {
            System.out.println("[MAIN] → Mở OrderFrame để tiếp tục phục vụ/thanh toán");
            // Kiểm tra xem bàn có hóa đơn tạm hay không
            // Nếu không có, reset bàn về Trống
            new OrderFrame(tableId, this);
        } else {
            UIUtils.showWarningMessage(this, "Bàn này không thể phục vụ!\nTrạng thái: " + tableStatus, "Thông báo");
        }
    }
    
    private void logout() {
        if (UIUtils.showConfirmDialog(this, "Bạn chắc chắn muốn đăng xuất?", "Xác nhận") == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}
