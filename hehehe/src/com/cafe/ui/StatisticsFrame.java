package com.cafe.ui;

import com.cafe.util.ReportUtils;
import com.cafe.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StatisticsFrame extends JDialog {
    
    public StatisticsFrame() {
        setTitle("Thống Kê");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setModal(true);
        
        initComponents();
        setVisible(true);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tab panel
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Most ordered items
        JPanel mostPanel = new JPanel(new BorderLayout());
        JTextArea mostArea = new JTextArea();
        mostArea.setFont(UIUtils.getFont("Courier New", Font.PLAIN, 11));
        mostArea.setEditable(false);
        
        StringBuilder mostText = new StringBuilder();
        mostText.append("10 MÓN ĐƯỢC GỌI NHIỀU NHẤT\n");
        mostText.append("========================================\n");
        Map<Integer, Integer> mostOrdered = ReportUtils.getMostOrderedItems();
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : mostOrdered.entrySet()) {
            if (count >= 10) break;
            mostText.append(String.format("Món ID %d: %d lần\n", entry.getKey(), entry.getValue()));
            count++;
        }
        
        mostArea.setText(mostText.toString());
        mostPanel.add(new JScrollPane(mostArea), BorderLayout.CENTER);
        tabbedPane.addTab("Được Gọi Nhiều Nhất", mostPanel);
        
        // Least ordered items
        JPanel leastPanel = new JPanel(new BorderLayout());
        JTextArea leastArea = new JTextArea();
        leastArea.setFont(UIUtils.getFont("Courier New", Font.PLAIN, 11));
        leastArea.setEditable(false);
        
        StringBuilder leastText = new StringBuilder();
        leastText.append("10 MÓN ĐƯỢC GỌI ÍT NHẤT\n");
        leastText.append("========================================\n");
        Map<Integer, Integer> leastOrdered = ReportUtils.getLeastOrderedItems();
        count = 0;
        for (Map.Entry<Integer, Integer> entry : leastOrdered.entrySet()) {
            if (count >= 10) break;
            leastText.append(String.format("Món ID %d: %d lần\n", entry.getKey(), entry.getValue()));
            count++;
        }
        
        leastArea.setText(leastText.toString());
        leastPanel.add(new JScrollPane(leastArea), BorderLayout.CENTER);
        tabbedPane.addTab("Được Gọi Ít Nhất", leastPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Đóng");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
}
