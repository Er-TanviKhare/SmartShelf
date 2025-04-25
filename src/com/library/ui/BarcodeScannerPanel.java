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
import com.library.database.StudentDAO;
import com.library.model.Book;
import com.library.model.Student;
import com.library.model.Transaction;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BarcodeScannerPanel extends JPanel {
    
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private JTextArea resultTextArea;
    private JButton startButton;
    private JButton stopButton;
    private JComboBox<String> scanModeCombo;
    private JPanel infoPanel;
    
    private ScheduledExecutorService executor;
    private boolean isScanning = false;
    
    private BookDAO bookDAO;
    private StudentDAO studentDAO;
    private TransactionController transactionController;
    
    // Mode constants
    private static final String MODE_BOOK_ISSUE = "Book Issue";
    private static final String MODE_BOOK_RETURN = "Book Return";
    
    // Current scan state
    private String currentMode;
    private Book scannedBook;
    private Student scannedStudent;
    
    public BarcodeScannerPanel() {
        bookDAO = new BookDAO();
        studentDAO = new StudentDAO();
        transactionController = new TransactionController();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the header
        JLabel headerLabel = new JLabel("Barcode Scanner");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Create the control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel modeLabel = new JLabel("Scan Mode:");
        String[] modes = {MODE_BOOK_ISSUE, MODE_BOOK_RETURN};
        scanModeCombo = new JComboBox<>(modes);
        
        startButton = new JButton("Start Camera");
        stopButton = new JButton("Stop Camera");
        stopButton.setEnabled(false);
        
        // Style the buttons
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        stopButton.setBackground(new Color(231, 76, 60));
        stopButton.setForeground(Color.WHITE);
        
        controlPanel.add(modeLabel);
        controlPanel.add(scanModeCombo);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        
        // Create the webcam panel (initially empty)
        JPanel webcamContainer = new JPanel(new BorderLayout());
        webcamContainer.setBorder(BorderFactory.createTitledBorder("Camera"));
        webcamContainer.setPreferredSize(new Dimension(640, 480));
        
        JLabel placeholderLabel = new JLabel("Camera not started");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        webcamContainer.add(placeholderLabel, BorderLayout.CENTER);
        
        // Create the result panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Scan Result"));
        
        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setRows(5);
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create the info panel
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        infoPanel.setPreferredSize(new Dimension(0, 200));
        
        JLabel infoLabel = new JLabel("Scan a book or student barcode to see information");
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        // Add action listeners
        startButton.addActionListener((ActionEvent e) -> {
            startCamera();
        });
        
        stopButton.addActionListener((ActionEvent e) -> {
            stopCamera();
        });
        
        scanModeCombo.addActionListener((ActionEvent e) -> {
            currentMode = (String) scanModeCombo.getSelectedItem();
            resetScanState();
            resultTextArea.append("Mode changed to: " + currentMode + "\n");
        });
        
        // Add components to the main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(webcamContainer, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(resultPanel, BorderLayout.NORTH);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Initialize the current mode
        currentMode = (String) scanModeCombo.getSelectedItem();
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
            Component comp = getComponent(1); // The webcam container is the second component
            if (comp instanceof JPanel) {
                JPanel container = (JPanel) comp;
                container.removeAll();
                container.add(webcamPanel, BorderLayout.CENTER);
                container.revalidate();
                container.repaint();
            }
            
            // Start scanning
            startScanning();
            
            // Update UI
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            scanModeCombo.setEnabled(false);
            
            resultTextArea.append("Camera started. Ready to scan barcodes.\n");
            resultTextArea.append("Current mode: " + currentMode + "\n");
            
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
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        scanModeCombo.setEnabled(true);
        
        // Reset scan state
        resetScanState();
        
        // Clear the webcam panel
        Component comp = getComponent(1); // The webcam container is the second component
        if (comp instanceof JPanel) {
            JPanel container = (JPanel) comp;
            container.removeAll();
            JLabel placeholderLabel = new JLabel("Camera stopped");
            placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
            container.add(placeholderLabel, BorderLayout.CENTER);
            container.revalidate();
            container.repaint();
        }
        
        resultTextArea.append("Camera stopped.\n");
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
        resultTextArea.append("Barcode detected: " + barcode + "\n");
        
        // Determine if this is a book or student barcode
        if (barcode.startsWith("B")) {
            // Book barcode
            processBookBarcode(barcode);
        } else if (barcode.startsWith("S")) {
            // Student barcode
            processStudentBarcode(barcode);
        } else {
            resultTextArea.append("Unknown barcode format\n");
        }
    }
    
    private void processBookBarcode(String barcode) {
        Book book = bookDAO.getBookById(barcode);
        
        if (book == null) {
            resultTextArea.append("Book not found in database\n");
            return;
        }
        
        scannedBook = book;
        resultTextArea.append("Book found: " + book.getTitle() + " by " + book.getAuthor() + "\n");
        
        // Update info panel with book details
        updateInfoPanel(book, null);
        
        // Process based on current mode
        if (MODE_BOOK_ISSUE.equals(currentMode)) {
            if (!"AVAILABLE".equals(book.getStatus())) {
                resultTextArea.append("Book is not available for issue\n");
                return;
            }
            
            resultTextArea.append("Please scan student ID to issue this book\n");
        } else if (MODE_BOOK_RETURN.equals(currentMode)) {
            if ("AVAILABLE".equals(book.getStatus())) {
                resultTextArea.append("Book is already available (not issued)\n");
                return;
            }
            
            // For return, we don't need to scan student ID
            processBookReturn(book);
        }
    }
    
    private void processStudentBarcode(String barcode) {
        Student student = studentDAO.getStudentById(barcode);
        
        if (student == null) {
            resultTextArea.append("Student not found in database\n");
            return;
        }
        
        scannedStudent = student;
        resultTextArea.append("Student found: " + student.getName() + "\n");
        
        // Update info panel with student details
        updateInfoPanel(null, student);
        
        // Process based on current mode and previous scans
        if (MODE_BOOK_ISSUE.equals(currentMode) && scannedBook != null) {
            processBookIssue(scannedBook, student);
        } else if (MODE_BOOK_ISSUE.equals(currentMode)) {
            resultTextArea.append("Please scan book barcode first\n");
        } else if (MODE_BOOK_RETURN.equals(currentMode)) {
            resultTextArea.append("For book return, please scan book barcode only\n");
        }
    }
    
    private void processBookIssue(Book book, Student student) {
        // Issue the book
        resultTextArea.append("Processing book issue...\n");
        
        TransactionController.TransactionResult result = transactionController.issueBook(book.getId(), student.getId());
        
        if (result.isSuccess()) {
            resultTextArea.append("SUCCESS: " + result.getMessage() + "\n");
            
            // Update book status in UI
            book.setStatus("ISSUED");
            book.setStudentId(student.getId());
            
            // Update info panel
            updateInfoPanel(book, student);
            
            // Reset scan state after successful operation
            resetScanState();
        } else {
            resultTextArea.append("ERROR: " + result.getMessage() + "\n");
        }
    }
    
    private void processBookReturn(Book book) {
        // Return the book
        resultTextArea.append("Processing book return...\n");
        
        TransactionController.TransactionResult result = transactionController.returnBook(book.getId());
        
        if (result.isSuccess()) {
            if (result.getFine() > 0) {
                resultTextArea.append("SUCCESS: " + result.getMessage() + " Fine charged: $" + result.getFine() + "\n");
            } else {
                resultTextArea.append("SUCCESS: " + result.getMessage() + "\n");
            }
            
            // Update book status in UI
            book.setStatus("AVAILABLE");
            book.setStudentId(null);
            
            // Update info panel
            updateInfoPanel(book, null);
            
            // Reset scan state after successful operation
            resetScanState();
        } else {
            resultTextArea.append("ERROR: " + result.getMessage() + "\n");
        }
    }
    
    private void updateInfoPanel(Book book, Student student) {
        infoPanel.removeAll();
        
        if (book != null && student != null) {
            // Both book and student info
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            panel.add(new JLabel("Book ID:"));
            panel.add(new JLabel(book.getId()));
            
            panel.add(new JLabel("Title:"));
            panel.add(new JLabel(book.getTitle()));
            
            panel.add(new JLabel("Status:"));
            panel.add(new JLabel(book.getStatus()));
            
            panel.add(new JLabel("Student ID:"));
            panel.add(new JLabel(student.getId()));
            
            panel.add(new JLabel("Student Name:"));
            panel.add(new JLabel(student.getName()));
            
            infoPanel.add(panel, BorderLayout.CENTER);
        } else if (book != null) {
            // Book info only
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            panel.add(new JLabel("Book ID:"));
            panel.add(new JLabel(book.getId()));
            
            panel.add(new JLabel("Title:"));
            panel.add(new JLabel(book.getTitle()));
            
            panel.add(new JLabel("Author:"));
            panel.add(new JLabel(book.getAuthor()));
            
            panel.add(new JLabel("ISBN:"));
            panel.add(new JLabel(book.getIsbn()));
            
            panel.add(new JLabel("Status:"));
            panel.add(new JLabel(book.getStatus()));
            
            if (book.getStudentId() != null && !book.getStudentId().isEmpty()) {
                panel.add(new JLabel("Issued To:"));
                panel.add(new JLabel(book.getStudentId()));
            }
            
            infoPanel.add(panel, BorderLayout.CENTER);
        } else if (student != null) {
            // Student info only
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            panel.add(new JLabel("Student ID:"));
            panel.add(new JLabel(student.getId()));
            
            panel.add(new JLabel("Name:"));
            panel.add(new JLabel(student.getName()));
            
            panel.add(new JLabel("Email:"));
            panel.add(new JLabel(student.getEmail()));
            
            panel.add(new JLabel("Fine Balance:"));
            panel.add(new JLabel("$" + student.getTotalFine()));
            
            infoPanel.add(panel, BorderLayout.CENTER);
        } else {
            // No info
            JLabel infoLabel = new JLabel("Scan a book or student barcode to see information");
            infoLabel.setHorizontalAlignment(JLabel.CENTER);
            infoPanel.add(infoLabel, BorderLayout.CENTER);
        }
        
        infoPanel.revalidate();
        infoPanel.repaint();
    }
    
    private void resetScanState() {
        scannedBook = null;
        scannedStudent = null;
    }
}