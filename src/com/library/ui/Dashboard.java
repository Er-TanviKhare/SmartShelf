/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.ui;

/**
 *
 * @author Mystique
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Dashboard extends JFrame {
    
    private JPanel contentPanel;
    
    public Dashboard() {
        initComponents();
        setAppIcon();
        showDashboardPanel(); // Show dashboard by default
    }
    
    private void initComponents() {
        // Set up the frame
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Create the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Create the top bar
        JPanel topBar = createTopBar();
        
        // Create the sidebar
        JPanel sidebar = createSidebar();
        
        // Create the content panel (initially empty)
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Add components to the main panel
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add the main panel to the frame
        add(mainPanel);
    }
    
    private void setAppIcon() {
    try {
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/SmartShelf.png")); // path relative to resources
        setIconImage(icon);
    } catch (Exception e) {
        System.err.println("App icon not found: " + e.getMessage());
    }
}

    private JPanel createTopBar() {
        JPanel topBar = new JPanel();
        topBar.setLayout(new BorderLayout());
        topBar.setBackground(new Color(44, 62, 80));
        topBar.setPreferredSize(new Dimension(0, 50));
        
        JLabel titleLabel = new JLabel("SMARTSHELF - Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(44, 62, 80));
        
        JLabel adminLabel = new JLabel("Admin");
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 14));

        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.RED);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setFocusPainted(false);
        
        logoutButton.addActionListener((ActionEvent e) -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
                this.dispose();
            }
        });
        
        rightPanel.add(adminLabel);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(logoutButton);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        
        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(52, 73, 94));
        sidebar.setPreferredSize(new Dimension(200, 0));
        
        // Create menu items - removed "Barcode Scanner" from the list
        String[] menuItems = {
            "Dashboard", "Books", "Students", "Issue Book", 
            "Return Book", "Search", "Reports", "Settings"
        };
        
        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            sidebar.add(menuButton);
        }
        
        return sidebar;
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 62, 80));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 73, 94));
            }
        });
        
        // Add action listener
        button.addActionListener((ActionEvent e) -> {
            // Handle menu item clicks
            switch (text) {
                case "Dashboard":
                    showDashboardPanel();
                    break;
                case "Books":
                    showBooksPanel();
                    break;
                case "Students":
                    showStudentsPanel();
                    break;
                case "Issue Book":
                    showIssueBookPanel();
                    break;
                case "Return Book":
                    showReturnBookPanel();
                    break;
                case "Search":
                    showSearchPanel();
                    break;
                case "Reports":
                    showReportsPanel();
                    break;
                case "Settings":
                    showSettingsPanel();
                    break;
            }
        });
        
        return button;
    }
    
    // Methods to show different panels
    private void showDashboardPanel() {
        contentPanel.removeAll();
        
        // Create a dashboard panel with statistics
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add statistic cards
        dashboardPanel.add(createStatCard("Total Books", "1,245", new Color(52, 152, 219)));
        dashboardPanel.add(createStatCard("Books Issued", "423", new Color(231, 76, 60)));
        dashboardPanel.add(createStatCard("Total Students", "856", new Color(46, 204, 113)));
        dashboardPanel.add(createStatCard("Overdue Books", "37", new Color(243, 156, 18)));
        
        // Add recent activities panel
        JPanel activitiesPanel = new JPanel(new BorderLayout());
        activitiesPanel.setBorder(BorderFactory.createTitledBorder("Recent Activities"));
        
        String[] activities = {
            "Book 'Java Programming' issued to Student ID: S1001 - 10 minutes ago",
            "Book 'Database Systems' returned by Student ID: S1042 - 1 hour ago",
            "New book 'Machine Learning Basics' added to library - 3 hours ago",
            "Fine of $5.00 collected from Student ID: S1023 - 5 hours ago",
            "Book 'Web Development' issued to Student ID: S1015 - 1 day ago"
        };
        
        JList<String> activityList = new JList<>(activities);
        JScrollPane scrollPane = new JScrollPane(activityList);
        activitiesPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create main dashboard layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(dashboardPanel, BorderLayout.NORTH);
        mainPanel.add(activitiesPanel, BorderLayout.CENTER);
        
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void showBooksPanel() {
        contentPanel.removeAll();
        
        // Create tabs for different book operations
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add book registration panel
        tabbedPane.addTab("Register Book", new BookRegistrationPanel());
        
        // Add book search panel
        tabbedPane.addTab("Search Books", new BookSearchPanel());
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showStudentsPanel() {
        contentPanel.removeAll();
        
        // Create tabs for different student operations
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add student registration panel
        tabbedPane.addTab("Register Student", new StudentRegistrationPanel());
        
        // Add student search panel
        tabbedPane.addTab("Search Students", new StudentSearchPanel());
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showIssueBookPanel() {
        contentPanel.removeAll();
        contentPanel.add(new BookIssuePanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showReturnBookPanel() {
        contentPanel.removeAll();
        contentPanel.add(new BookReturnPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showSearchPanel() {
        contentPanel.removeAll();
        
        // Create tabs for different search operations
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add book search panel
        tabbedPane.addTab("Search Books", new BookSearchPanel());
        
        // Add student search panel
        tabbedPane.addTab("Search Students", new StudentSearchPanel());
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showReportsPanel() {
        contentPanel.removeAll();
        
        // Create a simple reports panel (to be enhanced later)
        JPanel reportsPanel = new JPanel(new BorderLayout());
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        
        JButton issuedBooksButton = new JButton("Currently Issued Books");
        issuedBooksButton.setBackground(new Color(52, 152, 219));
        issuedBooksButton.setForeground(Color.WHITE);
        issuedBooksButton.setFocusPainted(false);
        issuedBooksButton.setOpaque(true);                       // ✅ force background to be painted
        issuedBooksButton.setBorderPainted(false);
        issuedBooksButton.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton overdueBooksButton = new JButton("Overdue Books");
        overdueBooksButton.setBackground(new Color(231, 76, 60));
        overdueBooksButton.setForeground(Color.WHITE);
        overdueBooksButton.setFocusPainted(false);
        overdueBooksButton.setOpaque(true);                       // ✅ force background to be painted
        overdueBooksButton.setBorderPainted(false);
        overdueBooksButton.setFont(new Font("Arial", Font.BOLD, 18));
                
        JButton fineReportButton = new JButton("Fine Collection Report");
        fineReportButton.setBackground(new Color(241, 196, 15));
        fineReportButton.setForeground(Color.WHITE);
        fineReportButton.setFocusPainted(false);
        fineReportButton.setOpaque(true);
        fineReportButton.setBorderPainted(false);
        fineReportButton.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton popularBooksButton = new JButton("Popular Books");
        popularBooksButton.setBackground(new Color(46, 204, 113));
        popularBooksButton.setForeground(Color.WHITE);
        popularBooksButton.setFocusPainted(false);
        popularBooksButton.setOpaque(true);                       // ✅ force background to be painted
        popularBooksButton.setBorderPainted(false);
        popularBooksButton.setFont(new Font("Arial", Font.BOLD, 18));
        
        
        JButton studentActivityButton = new JButton("Student Activity");
        studentActivityButton.setBackground(new Color(155, 89, 182));
        studentActivityButton.setForeground(Color.WHITE);
        studentActivityButton.setFocusPainted(false);
        studentActivityButton.setOpaque(true);                       // ✅ force background to be painted
        studentActivityButton.setBorderPainted(false);
        studentActivityButton.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton inventoryButton = new JButton("Book Inventory");
        inventoryButton.setBackground(new Color(52, 73, 94));
        inventoryButton.setForeground(Color.WHITE);
        inventoryButton.setFocusPainted(false);
        inventoryButton.setOpaque(true);                       // ✅ force background to be painted
        inventoryButton.setBorderPainted(false);
        
        buttonsPanel.add(issuedBooksButton);
        buttonsPanel.add(overdueBooksButton);
        buttonsPanel.add(fineReportButton);
        buttonsPanel.add(popularBooksButton);
        buttonsPanel.add(studentActivityButton);
        buttonsPanel.add(inventoryButton);
        
        reportsPanel.add(titleLabel, BorderLayout.NORTH);
        reportsPanel.add(buttonsPanel, BorderLayout.CENTER);
        
        contentPanel.add(reportsPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showSettingsPanel() {
        contentPanel.removeAll();
        
        // Create a simple settings panel (to be enhanced later)
        JPanel settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        formPanel.add(new JLabel("Library Name:"));
        formPanel.add(new JTextField("Academic Library"));
        
        formPanel.add(new JLabel("Fine Rate (per day):"));
        formPanel.add(new JTextField("1.00"));
        
        formPanel.add(new JLabel("Max Books Per Student:"));
        formPanel.add(new JTextField("3"));
        
        formPanel.add(new JLabel("Loan Period (days):"));
        formPanel.add(new JTextField("30"));
        
        JButton saveButton = new JButton("Save Settings");
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        
        settingsPanel.add(titleLabel, BorderLayout.NORTH);
        settingsPanel.add(formPanel, BorderLayout.CENTER);
        settingsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(settingsPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}