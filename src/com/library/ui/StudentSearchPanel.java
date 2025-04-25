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
import com.library.database.TransactionDAO;
import com.library.model.Student;
import com.library.model.Transaction;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class StudentSearchPanel extends JPanel {
    
    private JTextField searchField;
    private JButton searchButton;
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    
    private StudentDAO studentDAO;
    private TransactionDAO transactionDAO;
    
    public StudentSearchPanel() {
        studentDAO = new StudentDAO();
        transactionDAO = new TransactionDAO();
        initComponents();
        loadAllStudents(); // Load all students initially
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the header
        JLabel headerLabel = new JLabel("Search Students");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Create the search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh All");
        
        // Style the buttons
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.MAGENTA);
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        searchPanel.add(refreshButton);
        
        // Create the table
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Fine"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        studentsTable = new JTable(tableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane tableScrollPane = new JScrollPane(studentsTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Create the details panel (initially empty)
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        
        // Add action listeners
        searchButton.addActionListener((ActionEvent e) -> {
            searchStudents();
        });
        
        clearButton.addActionListener((ActionEvent e) -> {
            searchField.setText("");
            loadAllStudents();
        });
        
        refreshButton.addActionListener((ActionEvent e) -> {
            loadAllStudents();
        });
        
        // Add listener for table selection
        studentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = studentsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String studentId = (String) tableModel.getValueAt(selectedRow, 0);
                    showStudentDetails(studentId);
                }
            }
        });
        
        // Add components to the main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(detailsPanel, BorderLayout.SOUTH);
    }
    
    private void searchStudents() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadAllStudents();
            return;
        }
        
        List<Student> students = studentDAO.searchStudents(searchTerm);
        updateTable(students);
    }
    
    private void loadAllStudents() {
        List<Student> students = studentDAO.getAllStudents();
        updateTable(students);
    }
    
    private void updateTable(List<Student> students) {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Add students to the table
        for (Student student : students) {
            Object[] row = {
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getPhone(),
                String.format("$%.2f", student.getTotalFine())
            };
            tableModel.addRow(row);
        }
        
        // Clear details panel
        detailsPanel.removeAll();
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }
    
    private void showStudentDetails(String studentId) {
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) return;
        
        detailsPanel.removeAll();
        
        // Create student info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(student.getId()));
        
        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(student.getName()));
        
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel(student.getEmail()));
        
        infoPanel.add(new JLabel("Phone:"));
        infoPanel.add(new JLabel(student.getPhone() != null ? student.getPhone() : ""));
        
        infoPanel.add(new JLabel("Address:"));
        infoPanel.add(new JLabel(student.getAddress() != null ? student.getAddress() : ""));
        
        infoPanel.add(new JLabel("Total Fine:"));
        JLabel fineLabel = new JLabel(String.format("$%.2f", student.getTotalFine()));
        if (student.getTotalFine() > 0) {
            fineLabel.setForeground(Color.RED);
        }
        infoPanel.add(fineLabel);
        
        // Get active transactions for this student
        List<Transaction> activeTransactions = transactionDAO.getActiveTransactionsByStudentId(studentId);
        
        // Create transactions panel
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBorder(BorderFactory.createTitledBorder("Current Borrowed Books"));
        
        if (activeTransactions.isEmpty()) {
            transactionsPanel.add(new JLabel("No books currently borrowed"), BorderLayout.CENTER);
        } else {
            String[] transactionColumns = {"Book ID", "Issue Date", "Due Date", "Status"};
            DefaultTableModel transactionModel = new DefaultTableModel(transactionColumns, 0);
            
            for (Transaction transaction : activeTransactions) {
                String status = transaction.getDueDate().isBefore(java.time.LocalDate.now()) ? 
                                "Overdue" : "Borrowed";
                
                Object[] row = {
                    transaction.getBookId(),
                    transaction.getIssueDate().toString(),
                    transaction.getDueDate().toString(),
                    status
                };
                transactionModel.addRow(row);
            }
            
            JTable transactionsTable = new JTable(transactionModel);
            transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            transactionsTable.getTableHeader().setReorderingAllowed(false);
            
            JScrollPane scrollPane = new JScrollPane(transactionsTable);
            scrollPane.setPreferredSize(new Dimension(600, 150));
            
            transactionsPanel.add(scrollPane, BorderLayout.CENTER);
        }
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton editButton = new JButton("Edit Student");
        JButton payFineButton = new JButton("Pay Fine");
        JButton printCardButton = new JButton("Print ID Card");
        
        // Disable pay fine button if no fine
        payFineButton.setEnabled(student.getTotalFine() > 0);
        
        // Add action listeners
        editButton.addActionListener((ActionEvent e) -> {
            // Open edit student dialog (to be implemented)
            JOptionPane.showMessageDialog(this, "Edit student functionality to be implemented");
        });
        
        payFineButton.addActionListener((ActionEvent e) -> {
            // Open pay fine dialog (to be implemented)
            if (student.getTotalFine() > 0) {
                boolean confirmed = JOptionPane.showConfirmDialog(
                    this,
                    "Confirm payment of $" + student.getTotalFine() + " for " + student.getName() + "?",
                    "Confirm Payment",
                    JOptionPane.YES_NO_OPTION
                ) == JOptionPane.YES_OPTION;
                
                if (confirmed) {
                    boolean cleared = studentDAO.clearStudentFine(student.getId());
                    if (cleared) {
                        JOptionPane.showMessageDialog(this, "Fine payment processed successfully");
                        // Refresh the student details
                        showStudentDetails(student.getId());
                        // Refresh the table
                        int selectedRow = studentsTable.getSelectedRow();
                        if (selectedRow >= 0) {
                            tableModel.setValueAt("$0.00", selectedRow, 4);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Error processing payment", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        printCardButton.addActionListener((ActionEvent e) -> {
            // Print ID card functionality (to be implemented)
            if (student.getBarcodePath() != null && !student.getBarcodePath().isEmpty()) {
                try {
                    ImageIcon barcodeIcon = new ImageIcon(student.getBarcodePath());
                    JLabel imageLabel = new JLabel(barcodeIcon);
                    
                    JPanel cardPanel = new JPanel(new BorderLayout());
                    cardPanel.add(new JLabel("Student ID Card: " + student.getId()), BorderLayout.NORTH);
                    cardPanel.add(new JLabel("Name: " + student.getName()), BorderLayout.CENTER);
                    cardPanel.add(imageLabel, BorderLayout.SOUTH);
                    
                    JOptionPane.showMessageDialog(this, cardPanel, "Student ID Card", JOptionPane.PLAIN_MESSAGE);
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(this, "Error displaying ID card", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No barcode available for this student", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonsPanel.add(editButton);
        buttonsPanel.add(payFineButton);
        buttonsPanel.add(printCardButton);
        
        // Add panels to details panel
        detailsPanel.add(infoPanel, BorderLayout.NORTH);
        detailsPanel.add(transactionsPanel, BorderLayout.CENTER);
        detailsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }
}