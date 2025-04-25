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

public class LoginForm extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    public LoginForm() {
        initComponents();
        setAppIcon();
    }
    
    private void initComponents() {
        // Set up the frame
        setTitle("SmartShelf - LMS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create the main panel with a gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(36, 47, 65);
                Color color2 = new Color(58, 83, 155);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
//        
//        // Create the header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/SmartShelf12.png"));
            Image scaledImage = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            headerPanel.add(logoLabel, BorderLayout.CENTER);
        } catch (Exception ex) {
            System.err.println("Logo could not be loaded: " + ex.getMessage());
        }
        
        // Create the form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        // Username panel
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 0));
        usernamePanel.setOpaque(false);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(Box.createVerticalStrut(5), BorderLayout.CENTER);
        usernamePanel.add(usernameField, BorderLayout.SOUTH);
        
        // Password panel
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        passwordPanel.setOpaque(false);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(Box.createVerticalStrut(5), BorderLayout.CENTER);
        passwordPanel.add(passwordField, BorderLayout.SOUTH);
        
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);
        
        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Add hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(39, 174, 96));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(46, 204, 113));
            }
        });
        
        buttonPanel.add(loginButton);
        
        // Create the status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setForeground(new Color(231, 76, 60));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        // Add action listener to login button
        loginButton.addActionListener((ActionEvent e) -> {
            login();
        });
        
        // Add key listener to password field
        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });
        
        // Add components to the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(statusPanel, BorderLayout.PAGE_END);
        
        // Add the main panel to the frame
        add(mainPanel);
        
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }
    
    private void setAppIcon() {
    try {
        ImageIcon icon = new ImageIcon(getClass().getResource("/SmartShelflogolight.png")); // relative path to src
        setIconImage(icon.getImage());
    } catch (Exception e) {
        System.err.println("App icon could not be set: " + e.getMessage());
    }
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // For demonstration purposes, use a simple hardcoded admin/admin credential
        // In a real application, you would check against a database
        if ("admin".equals(username) && "admin".equals(password)) {
            // Login successful
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
            
            this.dispose();
        } else {
            // Login failed
            statusLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
    }
}