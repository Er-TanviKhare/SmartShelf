/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.ui;

/**
 *
 * @author Mystique
 */
import com.library.database.StudentDAO;
import com.library.model.Student;
import com.library.util.BarcodeUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StudentRegistrationPanel extends JPanel {
    
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JButton registerButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private JLabel barcodeImageLabel;
    
    private StudentDAO studentDAO;
    
    public StudentRegistrationPanel() {
        studentDAO = new StudentDAO();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the header
        JLabel headerLabel = new JLabel("Register New Student");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Create the form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        JLabel idLabel = new JLabel("Student ID:");
        idField = new JTextField();
        
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        
        JLabel phoneLabel = new JLabel("Phone:");
        phoneField = new JTextField();
        
        JLabel addressLabel = new JLabel("Address:");
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        
        // Add components to the form panel
        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
        formPanel.add(addressLabel);
        formPanel.add(addressScrollPane);
        
        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        clearButton = new JButton("Clear");
        registerButton = new JButton("Register Student");
        
        // Style the buttons
        clearButton.setBackground(new Color(192, 192, 192));
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.MAGENTA);
        
        buttonPanel.add(clearButton);
        buttonPanel.add(registerButton);
        
        // Create the status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        // Create the barcode panel
        JPanel barcodePanel = new JPanel(new BorderLayout());
        barcodePanel.setBorder(BorderFactory.createTitledBorder("Student ID Card Barcode"));
        barcodeImageLabel = new JLabel("Barcode will appear here after registration");
        barcodeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        barcodePanel.add(barcodeImageLabel, BorderLayout.CENTER);
        
        // Add action listeners
        registerButton.addActionListener((ActionEvent e) -> {
            registerStudent();
        });
        
        clearButton.addActionListener((ActionEvent e) -> {
            clearForm();
        });
        
        // Add components to the main panel
        add(headerLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(statusPanel, BorderLayout.CENTER);
        southPanel.add(barcodePanel, BorderLayout.SOUTH);
        
        add(southPanel, BorderLayout.SOUTH);
    }
    
    private void registerStudent() {
        // Validate input
        if (idField.getText().isEmpty() || nameField.getText().isEmpty() || 
            emailField.getText().isEmpty()) {
            statusLabel.setText("Please fill all required fields");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        try {
            // Create a new student object
            Student student = new Student(
                idField.getText(),
                nameField.getText(),
                emailField.getText()
            );
            
            // Set additional properties
            student.setPhone(phoneField.getText());
            student.setAddress(addressArea.getText());
            
            // Generate barcode
            String barcodePath = BarcodeUtil.generateStudentBarcode(student.getId());
            student.setBarcodePath(barcodePath);
            
            // Save to database
            if (studentDAO.addStudent(student)) {
                statusLabel.setText("Student registered successfully!");
                statusLabel.setForeground(new Color(0, 128, 0));
                
                // Display barcode
                displayBarcode(barcodePath);
                
                // Clear form after successful registration
                clearForm();
            } else {
                statusLabel.setText("Error registering student");
                statusLabel.setForeground(Color.RED);
            }
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
    
    private void displayBarcode(String barcodePath) {
        try {
            ImageIcon barcodeIcon = new ImageIcon(barcodePath);
            Image image = barcodeIcon.getImage().getScaledInstance(300, 100, Image.SCALE_SMOOTH);
            barcodeImageLabel.setIcon(new ImageIcon(image));
            barcodeImageLabel.setText("");
        } catch (Exception e) {
            barcodeImageLabel.setIcon(null);
            barcodeImageLabel.setText("Error loading barcode image");
        }
    }
    
    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        barcodeImageLabel.setIcon(null);
        barcodeImageLabel.setText("Barcode will appear here after registration");
    }
}