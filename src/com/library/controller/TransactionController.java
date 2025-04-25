/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.controller;

/**
 *
 * @author Mystique
 */
import com.library.database.BookDAO;
import com.library.database.StudentDAO;
import com.library.database.TransactionDAO;
import com.library.model.Book;
import com.library.model.Student;
import com.library.model.Transaction;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TransactionController {
    
    private BookDAO bookDAO;
    private StudentDAO studentDAO;
    private TransactionDAO transactionDAO;
    
    // Fine rate per day
    private static final double FINE_RATE_PER_DAY = 1.0; // $1 per day
    
    public TransactionController() {
        bookDAO = new BookDAO();
        studentDAO = new StudentDAO();
        transactionDAO = new TransactionDAO();
    }
    
    /**
     * Issues a book to a student.
     * 
     * @param bookId The ID of the book to issue
     * @param studentId The ID of the student
     * @return A result object containing success status and message
     */
    public TransactionResult issueBook(String bookId, String studentId) {
        // Check if book exists and is available
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            return new TransactionResult(false, "Book not found");
        }
        
        if (!"AVAILABLE".equals(book.getStatus())) {
            return new TransactionResult(false, "Book is not available for issue");
        }
        
        // Check if student exists
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            return new TransactionResult(false, "Student not found");
        }
        
        // Check if student has any unpaid fines
        if (student.getTotalFine() > 0) {
            return new TransactionResult(false, "Student has unpaid fines: $" + student.getTotalFine());
        }
        
        // Check if student has too many books issued
        int issuedBooksCount = studentDAO.getIssuedBooksCount(studentId);
        if (issuedBooksCount >= 3) { // Limit to 3 books per student
            return new TransactionResult(false, "Student has reached the maximum number of books allowed");
        }
        
        // Create transaction
        Transaction transaction = new Transaction(bookId, studentId);
        
        // Add transaction to database
        boolean transactionAdded = transactionDAO.addTransaction(transaction);
        if (!transactionAdded) {
            return new TransactionResult(false, "Failed to create transaction record");
        }
        
        // Update book status
        boolean bookUpdated = bookDAO.updateBookStatus(bookId, "ISSUED", studentId);
        if (!bookUpdated) {
            return new TransactionResult(false, "Failed to update book status");
        }
        
        return new TransactionResult(true, "Book issued successfully");
    }
    
    /**
     * Returns a book and calculates any applicable fine.
     * 
     * @param bookId The ID of the book to return
     * @return A result object containing success status, message, and fine amount
     */
    public TransactionResult returnBook(String bookId) {
        // Check if book exists and is issued
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            return new TransactionResult(false, "Book not found");
        }
        
        if ("AVAILABLE".equals(book.getStatus())) {
            return new TransactionResult(false, "Book is not issued");
        }
        
        // Get the active transaction for this book
        Transaction transaction = transactionDAO.getActiveTransactionByBookId(bookId);
        if (transaction == null) {
            return new TransactionResult(false, "No active transaction found for this book");
        }
        
        // Calculate fine if book is returned late
        LocalDate returnDate = LocalDate.now();
        double fine = 0.0;
        
        if (returnDate.isAfter(transaction.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(transaction.getDueDate(), returnDate);
            fine = daysLate * FINE_RATE_PER_DAY;
        }
        
        // Update transaction with return date and fine
        boolean transactionUpdated = transactionDAO.updateTransactionReturn(bookId, returnDate, fine);
        if (!transactionUpdated) {
            return new TransactionResult(false, "Failed to update transaction record");
        }
        
        // Update book status
        boolean bookUpdated = bookDAO.updateBookStatus(bookId, "AVAILABLE", null);
        if (!bookUpdated) {
            return new TransactionResult(false, "Failed to update book status");
        }
        
        // If there's a fine, update student's total fine
        if (fine > 0) {
            studentDAO.updateStudentFine(transaction.getStudentId(), fine);
            return new TransactionResult(true, "Book returned with fine: $" + fine, fine);
        }
        
        return new TransactionResult(true, "Book returned successfully");
    }
    
    /**
     * Gets the due date for a book.
     * 
     * @param bookId The ID of the book
     * @return The due date or null if the book is not issued
     */
    public LocalDate getBookDueDate(String bookId) {
        Transaction transaction = transactionDAO.getActiveTransactionByBookId(bookId);
        return transaction != null ? transaction.getDueDate() : null;
    }
    
    /**
     * Gets the student who has issued a book.
     * 
     * @param bookId The ID of the book
     * @return The student or null if the book is not issued
     */
    public Student getBookIssuedTo(String bookId) {
        Book book = bookDAO.getBookById(bookId);
        if (book != null && book.getStudentId() != null) {
            return studentDAO.getStudentById(book.getStudentId());
        }
        return null;
    }
    
    /**
     * Result class for transaction operations.
     */
    public static class TransactionResult {
        private boolean success;
        private String message;
        private double fine;
        
        public TransactionResult(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.fine = 0.0;
        }
        
        public TransactionResult(boolean success, String message, double fine) {
            this.success = success;
            this.message = message;
            this.fine = fine;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public double getFine() { return fine; }
    }
}