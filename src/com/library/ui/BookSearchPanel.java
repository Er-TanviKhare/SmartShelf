/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.ui;

/**
 *
 * @author Mystique
 */
import com.library.controller.TransactionController;
import com.library.database.BookDAO;
import com.library.model.Book;
import com.library.model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

public class BookSearchPanel extends JPanel {
    
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JButton searchButton;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    
    private BookDAO bookDAO;
    private TransactionController transactionController;
    
    public BookSearchPanel() {
        bookDAO = new BookDAO();
        transactionController = new TransactionController();
        initComponents();
        loadAllBooks(); // Load all books initially
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create the header
        JLabel headerLabel = new JLabel("Search Books");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Create the search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchField = new JTextField(20);
        String[] filterOptions = {"All Books", "Available", "Issued", "Overdue"};
        filterCombo = new JComboBox<>(filterOptions);
        searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh All");
        
        // Style the buttons
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.MAGENTA);
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter:"));
        searchPanel.add(filterCombo);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        searchPanel.add(refreshButton);
        
        // Create the table
        String[] columnNames = {"ID", "Title", "Author", "ISBN", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Create the details panel (initially empty)
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));
        
        // Add action listeners
        searchButton.addActionListener((ActionEvent e) -> {
            searchBooks();
        });
        
        clearButton.addActionListener((ActionEvent e) -> {
            searchField.setText("");
            filterCombo.setSelectedIndex(0);
            loadAllBooks();
        });
        
        refreshButton.addActionListener((ActionEvent e) -> {
            loadAllBooks();
        });
        
        // Add listener for table selection
        booksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = booksTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String bookId = (String) tableModel.getValueAt(selectedRow, 0);
                    showBookDetails(bookId);
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
    
    private void searchBooks() {
        String searchTerm = searchField.getText().trim();
        String filter = (String) filterCombo.getSelectedItem();
        
        List<Book> books;
        
        if (searchTerm.isEmpty() && "All Books".equals(filter)) {
            loadAllBooks();
            return;
        }
        
        if (!searchTerm.isEmpty()) {
            books = bookDAO.searchBooks(searchTerm);
        } else {
            books = bookDAO.getAllBooks();
        }
        
        // Apply filter if needed
        if (!"All Books".equals(filter)) {
            books.removeIf(book -> {
                switch (filter) {
                    case "Available":
                        return !"AVAILABLE".equals(book.getStatus());
                    case "Issued":
                        return !"ISSUED".equals(book.getStatus());
                    case "Overdue":
                        if (!"ISSUED".equals(book.getStatus())) {
                            return true;
                        }
                        LocalDate dueDate = transactionController.getBookDueDate(book.getId());
                        return dueDate == null || !dueDate.isBefore(LocalDate.now());
                    default:
                        return false;
                }
            });
        }
        
        updateTable(books);
    }
    
    private void loadAllBooks() {
        List<Book> books = bookDAO.getAllBooks();
        updateTable(books);
    }
    
    private void updateTable(List<Book> books) {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Add books to the table
        for (Book book : books) {
            Object[] row = {
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getStatus()
            };
            tableModel.addRow(row);
        }
        
        // Clear details panel
        detailsPanel.removeAll();
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }
    
    private void showBookDetails(String bookId) {
        Book book = bookDAO.getBookById(bookId);
        if (book == null) return;
        
        detailsPanel.removeAll();
        
        // Create book info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(book.getId()));
        
        infoPanel.add(new JLabel("Title:"));
        infoPanel.add(new JLabel(book.getTitle()));
        
        infoPanel.add(new JLabel("Author:"));
        infoPanel.add(new JLabel(book.getAuthor()));
        
        infoPanel.add(new JLabel("ISBN:"));
        infoPanel.add(new JLabel(book.getIsbn()));
        
        infoPanel.add(new JLabel("Publisher:"));
        infoPanel.add(new JLabel(book.getPublisher() != null ? book.getPublisher() : ""));
        
        infoPanel.add(new JLabel("Publication Year:"));
        infoPanel.add(new JLabel(book.getPublicationYear() > 0 ? String.valueOf(book.getPublicationYear()) : ""));
        
        infoPanel.add(new JLabel("Category:"));
        infoPanel.add(new JLabel(book.getCategory() != null ? book.getCategory() : ""));
        
        infoPanel.add(new JLabel("Status:"));
        JLabel statusLabel = new JLabel(book.getStatus());
        if ("ISSUED".equals(book.getStatus())) {
            statusLabel.setForeground(Color.RED);
        }
        infoPanel.add(statusLabel);
        
        // If book is issued, show student info and due date
        if ("ISSUED".equals(book.getStatus())) {
            Student student = transactionController.getBookIssuedTo(book.getId());
            if (student != null) {
                infoPanel.add(new JLabel("Issued To:"));
                infoPanel.add(new JLabel(student.getName() + " (" + student.getId() + ")"));
            }
            
            LocalDate dueDate = transactionController.getBookDueDate(book.getId());
            if (dueDate != null) {
                infoPanel.add(new JLabel("Due Date:"));
                JLabel dueDateLabel = new JLabel(dueDate.toString());
                if (dueDate.isBefore(LocalDate.now())) {
                    dueDateLabel.setForeground(Color.RED);
                    dueDateLabel.setText(dueDate.toString() + " (OVERDUE)");
                }
                infoPanel.add(dueDateLabel);
            }
        }
        
        // Create barcode panel if barcode exists
        JPanel barcodePanel = new JPanel(new BorderLayout());
        if (book.getBarcodePath() != null && !book.getBarcodePath().isEmpty()) {
            try {
                ImageIcon barcodeIcon = new ImageIcon(book.getBarcodePath());
                Image image = barcodeIcon.getImage().getScaledInstance(300, 100, Image.SCALE_SMOOTH);
                JLabel barcodeLabel = new JLabel(new ImageIcon(image));
                barcodePanel.add(barcodeLabel, BorderLayout.CENTER);
            } catch (Exception e) {
                barcodePanel.add(new JLabel("Error loading barcode image"), BorderLayout.CENTER);
            }
        } else {
            barcodePanel.add(new JLabel("No barcode available"), BorderLayout.CENTER);
        }
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton editButton = new JButton("Edit Book");
        JButton printBarcodeButton = new JButton("Print Barcode");
        
        // Add action listeners
        editButton.addActionListener((ActionEvent e) -> {
            // Open edit book dialog (to be implemented)
            JOptionPane.showMessageDialog(this, "Edit book functionality to be implemented");
        });
        
        printBarcodeButton.addActionListener((ActionEvent e) -> {
            // Print barcode functionality
            if (book.getBarcodePath() != null && !book.getBarcodePath().isEmpty()) {
                try {
                    ImageIcon barcodeIcon = new ImageIcon(book.getBarcodePath());
                    JLabel imageLabel = new JLabel(barcodeIcon);
                    
                    JPanel cardPanel = new JPanel(new BorderLayout());
                    cardPanel.add(new JLabel("Book ID: " + book.getId()), BorderLayout.NORTH);
                    cardPanel.add(new JLabel("Title: " + book.getTitle()), BorderLayout.CENTER);
                    cardPanel.add(imageLabel, BorderLayout.SOUTH);
                    
                    JOptionPane.showMessageDialog(this, cardPanel, "Book Barcode", JOptionPane.PLAIN_MESSAGE);
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(this, "Error displaying barcode", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No barcode available for this book", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonsPanel.add(editButton);
        buttonsPanel.add(printBarcodeButton);
        
        // Add panels to details panel
        detailsPanel.add(infoPanel, BorderLayout.NORTH);
        detailsPanel.add(barcodePanel, BorderLayout.CENTER);
        detailsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }
}