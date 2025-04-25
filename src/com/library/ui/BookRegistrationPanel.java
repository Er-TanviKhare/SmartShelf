/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.ui;

/**
 *
 * @author Mystique
 */
import com.library.database.BookDAO;
import com.library.model.Book;
import com.library.util.BarcodeUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BookRegistrationPanel extends JPanel {
    
    private JTextField idField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField publisherField;
    private JTextField yearField;
    private JComboBox<String> categoryCombo;
    private JButton registerButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private JLabel barcodeImageLabel;
    
    private BookDAO bookDAO;
    
    public BookRegistrationPanel() {
        bookDAO = new BookDAO();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the header
        JLabel headerLabel = new JLabel("Register New Book");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Create the form panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        
        JLabel idLabel = new JLabel("Book ID:");
        idField = new JTextField();
        
        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField();
        
        JLabel authorLabel = new JLabel("Author:");
        authorField = new JTextField();
        
        JLabel isbnLabel = new JLabel("ISBN:");
        isbnField = new JTextField();
        
        JLabel publisherLabel = new JLabel("Publisher:");
        publisherField = new JTextField();
        
        JLabel yearLabel = new JLabel("Publication Year:");
        yearField = new JTextField();
        
        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Textbook", "Fiction", "Non-Fiction", "Reference", "Magazine", "Other"};
        categoryCombo = new JComboBox<>(categories);
        
        // Add components to the form panel
        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(titleLabel);
        formPanel.add(titleField);
        formPanel.add(authorLabel);
        formPanel.add(authorField);
        formPanel.add(isbnLabel);
        formPanel.add(isbnField);
        formPanel.add(publisherLabel);
        formPanel.add(publisherField);
        formPanel.add(yearLabel);
        formPanel.add(yearField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);
        
        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        clearButton = new JButton("Clear");
        registerButton = new JButton("Register Book");
        
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
        barcodePanel.setBorder(BorderFactory.createTitledBorder("Book Barcode"));
        barcodeImageLabel = new JLabel("Barcode will appear here after registration");
        barcodeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        barcodePanel.add(barcodeImageLabel, BorderLayout.CENTER);
        
        // Add action listeners
        registerButton.addActionListener((ActionEvent e) -> {
            registerBook();
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
    
    private void registerBook() {
        // Validate input
        if (idField.getText().isEmpty() || titleField.getText().isEmpty() || 
            authorField.getText().isEmpty() || isbnField.getText().isEmpty()) {
            statusLabel.setText("Please fill all required fields");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        try {
            // Create a new book object
            Book book = new Book(
                idField.getText(),
                titleField.getText(),
                authorField.getText(),
                isbnField.getText()
            );
            
            // Set additional properties
            book.setPublisher(publisherField.getText());
            if (!yearField.getText().isEmpty()) {
                book.setPublicationYear(Integer.parseInt(yearField.getText()));
            }
            book.setCategory(categoryCombo.getSelectedItem().toString());
            
            // Generate barcode
            String barcodePath = BarcodeUtil.generateBookBarcode(book.getId());
            book.setBarcodePath(barcodePath);
            
            // Save to database
            if (bookDAO.addBook(book)) {
                statusLabel.setText("Book registered successfully!");
                statusLabel.setForeground(new Color(0, 128, 0));
                
                // Display barcode
                displayBarcode(barcodePath);
                
                // Clear form after successful registration
                clearForm();
            } else {
                statusLabel.setText("Error registering book");
                statusLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid year format");
            statusLabel.setForeground(Color.RED);
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
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        publisherField.setText("");
        yearField.setText("");
        categoryCombo.setSelectedIndex(0);
        barcodeImageLabel.setIcon(null);
        barcodeImageLabel.setText("Barcode will appear here after registration");
    }
}