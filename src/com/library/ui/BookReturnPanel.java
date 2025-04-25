/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.ui;

/**
 *
 * @author Mystique
 */
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.library.controller.TransactionController;
import com.library.database.BookDAO;
import com.library.model.Book;
import com.library.model.Student;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BookReturnPanel extends JPanel {
    
    private JPanel cameraPanel;
    private JPanel infoPanel;
    private JButton startScanButton;
    private JButton stopScanButton;
    private JButton returnButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private JLabel scanInstructionLabel;
    
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private ScheduledExecutorService executor;
    private boolean isScanning = false;
    
    private BookDAO bookDAO;
    private TransactionController transactionController;
    
    // Scanned data
    private Book scannedBook;
    
    public BookReturnPanel() {
        bookDAO = new BookDAO();
        transactionController = new TransactionController();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the header
        JLabel headerLabel = new JLabel("Return Book - Barcode Scanning");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Create the camera panel
        cameraPanel = new JPanel(new BorderLayout());
        cameraPanel.setBorder(BorderFactory.createTitledBorder("Camera"));
        cameraPanel.setPreferredSize(new Dimension(640, 480));
        
        JLabel placeholderLabel = new JLabel("Camera not started");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        cameraPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        // Create the control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        startScanButton = new JButton("Start Scanning");
        stopScanButton = new JButton("Stop Scanning");
        returnButton = new JButton("Return Book");
        clearButton = new JButton("Clear");
        
        // Style the buttons
        startScanButton.setBackground(new Color(46, 204, 113));
        startScanButton.setForeground(Color.BLACK);
        startScanButton.setFont(new Font("Arial", Font.BOLD, 14));
        startScanButton.setFocusPainted(false);
        
        stopScanButton.setBackground(new Color(231, 76, 60));
        stopScanButton.setForeground(Color.BLACK);
        stopScanButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopScanButton.setFocusPainted(false);
        stopScanButton.setEnabled(false);
        
        returnButton.setBackground(new Color(52, 152, 219));
        returnButton.setForeground(Color.BLACK);
        returnButton.setFont(new Font("Arial", Font.BOLD, 14));
        returnButton.setFocusPainted(false);
        returnButton.setEnabled(false);
        
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.MAGENTA);
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setFocusPainted(false);
        
        controlPanel.add(startScanButton);
        controlPanel.add(stopScanButton);
        controlPanel.add(returnButton);
        controlPanel.add(clearButton);
        
        // Create the info panel
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Scan Information"));
        
        scanInstructionLabel = new JLabel("Please start scanning and scan a book barcode");
        scanInstructionLabel.setHorizontalAlignment(JLabel.CENTER);
        scanInstructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(scanInstructionLabel, BorderLayout.CENTER);
        
        // Create the status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        // Add action listeners
        startScanButton.addActionListener((ActionEvent e) -> {
            startCamera();
        });
        
        stopScanButton.addActionListener((ActionEvent e) -> {
            stopCamera();
        });
        
        returnButton.addActionListener((ActionEvent e) -> {
            returnBook();
        });
        
        clearButton.addActionListener((ActionEvent e) -> {
            clearAll();
        });
        
        // Add components to the main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(cameraPanel, BorderLayout.CENTER);
        centerPanel.add(infoPanel, BorderLayout.SOUTH);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(controlPanel, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void startCamera() {
        try {
            // Initialize webcam
            webcam = Webcam.getDefault();
            
            if (webcam == null) {
                JOptionPane.showMessageDialog(this, 
                    "No webcam detected. Please connect a webcam and try again.",
                    "Webcam Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcam.open();
            
            // Create webcam panel
            webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setFPSDisplayed(true);
            webcamPanel.setDisplayDebugInfo(true);
            webcamPanel.setImageSizeDisplayed(true);
            webcamPanel.setMirrored(false);
            
            // Add webcam panel to the container
            cameraPanel.removeAll();
            cameraPanel.add(webcamPanel, BorderLayout.CENTER);
            cameraPanel.revalidate();
            cameraPanel.repaint();
            
            // Start scanning
            startScanning();
            
            // Update UI
            startScanButton.setEnabled(false);
            stopScanButton.setEnabled(true);
            
            // Reset scan state
            scannedBook = null;
            
            statusLabel.setText("Camera started. Ready to scan book barcode.");
            statusLabel.setForeground(new Color(46, 204, 113));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error starting camera: " + e.getMessage(),
                "Camera Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void stopCamera() {
        // Stop scanning
        stopScanning();
        
        // Update UI
        startScanButton.setEnabled(true);
        stopScanButton.setEnabled(false);
        
        // Clear the webcam panel
        cameraPanel.removeAll();
        JLabel placeholderLabel = new JLabel("Camera stopped");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        cameraPanel.add(placeholderLabel, BorderLayout.CENTER);
        cameraPanel.revalidate();
        cameraPanel.repaint();
        
        statusLabel.setText("Camera stopped.");
        statusLabel.setForeground(Color.BLACK);
    }
    
    private void startScanning() {
        isScanning = true;
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::scanBarcode, 0, 100, TimeUnit.MILLISECONDS);
    }
    
    private void stopScanning() {
        isScanning = false;
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
        
        if (webcam != null && webcam.isOpen()) {
            webcamPanel.stop();
            webcam.close();
        }
    }
    
    private void scanBarcode() {
        if (!isScanning || webcam == null) return;
        
        try {
            BufferedImage image = webcam.getImage();
            if (image != null) {
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                
                MultiFormatReader reader = new MultiFormatReader();
                Result result = null;
                
                try {
                    result = reader.decode(bitmap);
                } catch (NotFoundException e) {
                    // No barcode found in this frame
                    return;
                }
                
                if (result != null) {
                    String barcodeText = result.getText();
                    
                    // Process the barcode on the EDT
                    SwingUtilities.invokeLater(() -> {
                        processBarcode(barcodeText);
                    });
                    
                    // Pause briefly after successful scan to avoid multiple scans of the same barcode
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            System.err.println("Error scanning barcode: " + e.getMessage());
        }
    }
    
    private void processBarcode(String barcode) {
        statusLabel.setText("Barcode detected: " + barcode);
        statusLabel.setForeground(new Color(52, 152, 219));
        
        Book book = bookDAO.getBookById(barcode);
        if (book != null) {
            processBookBarcode(barcode);
        } else {
        showError("Book not found in database. Please scan a valid book barcode.");
        }

    }
    
    private void processBookBarcode(String barcode) {
        Book book = bookDAO.getBookById(barcode);
        
        if (book == null) {
            showError("Book not found in database");
            return;
        }
        
        if ("AVAILABLE".equals(book.getStatus())) {
            showError("This book is not issued to any student");
            return;
        }
        
        scannedBook = book;
        showSuccess("Book found: " + book.getTitle() + " by " + book.getAuthor());
        
        // Update info panel with book details
        updateInfoPanel();
        
        // Update scan instruction
        scanInstructionLabel.setText("Book ready to return. Click 'Return Book' button");
        
        // Enable return button
        returnButton.setEnabled(true);
    }
    
    private void returnBook() {
        if (scannedBook == null) {
            showError("Book information required");
            return;
        }
        
        // Return the book
        TransactionController.TransactionResult result = transactionController.returnBook(scannedBook.getId());
        
        if (result.isSuccess()) {
            if (result.getFine() > 0) {
                showSuccess(result.getMessage() + " Fine charged: $" + result.getFine());
            } else {
                showSuccess(result.getMessage());
            }
            
            // Stop camera after successful return
            stopCamera();
            
            // Clear form
            clearAll();
        } else {
            showError(result.getMessage());
        }
    }
    
    private void updateInfoPanel() {
        infoPanel.removeAll();
        
        if (scannedBook != null) {
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            panel.add(createBoldLabel("Book ID:"));
            panel.add(new JLabel(scannedBook.getId()));
            
            panel.add(createBoldLabel("Title:"));
            panel.add(new JLabel(scannedBook.getTitle()));
            
            panel.add(createBoldLabel("Author:"));
            panel.add(new JLabel(scannedBook.getAuthor()));
            
            panel.add(createBoldLabel("Status:"));
            JLabel statusLabel = new JLabel(scannedBook.getStatus());
            if (!"AVAILABLE".equals(scannedBook.getStatus())) {
                statusLabel.setForeground(Color.RED);
            }
            panel.add(statusLabel);
            
            // If book is issued, show student info and due date
            if ("ISSUED".equals(scannedBook.getStatus())) {
                Student student = transactionController.getBookIssuedTo(scannedBook.getId());
                if (student != null) {
                    panel.add(createBoldLabel("Issued To:"));
                    panel.add(new JLabel(student.getName() + " (" + student.getId() + ")"));
                }
                
                LocalDate dueDate = transactionController.getBookDueDate(scannedBook.getId());
                if (dueDate != null) {
                    panel.add(createBoldLabel("Due Date:"));
                    JLabel dueDateLabel = new JLabel(dueDate.toString());
                    if (dueDate.isBefore(LocalDate.now())) {
                        dueDateLabel.setForeground(Color.RED);
                        dueDateLabel.setText(dueDate.toString() + " (OVERDUE)");
                    }
                    panel.add(dueDateLabel);
                }
            }
            
            infoPanel.add(panel, BorderLayout.CENTER);
        }
        
        infoPanel.revalidate();
        infoPanel.repaint();
    }
    
    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }
    
    private void clearAll() {
        // Reset scan state
        scannedBook = null;
        
        // Clear info panel
        infoPanel.removeAll();
        scanInstructionLabel = new JLabel("Please start scanning and scan a book barcode");
        scanInstructionLabel.setHorizontalAlignment(JLabel.CENTER);
        scanInstructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(scanInstructionLabel, BorderLayout.CENTER);
        infoPanel.revalidate();
        infoPanel.repaint();
        
        // Disable return button
        returnButton.setEnabled(false);
        
        // Clear status
        statusLabel.setText("");
    }
    
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(231, 76, 60));
    }
    
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(46, 204, 113));
    }
}