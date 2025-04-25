/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.model;

/**
 *
 * @author Mystique
 */
import java.time.LocalDate;

public class Transaction {
    private int id;
    private String bookId;
    private String studentId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    
    // Constructor for new transaction
    public Transaction(String bookId, String studentId) {
        this.bookId = bookId;
        this.studentId = studentId;
        this.issueDate = LocalDate.now();
        this.dueDate = issueDate.plusMonths(1); // Default due date is 1 month from issue date
        this.fine = 0.0;
    }
    
    // Full constructor
    public Transaction(int id, String bookId, String studentId, LocalDate issueDate, 
                      LocalDate dueDate, LocalDate returnDate, double fine) {
        this.id = id;
        this.bookId = bookId;
        this.studentId = studentId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
    
    @Override
    public String toString() {
        return "Transaction{" + "id=" + id + ", bookId=" + bookId + ", studentId=" + studentId + 
               ", issueDate=" + issueDate + ", dueDate=" + dueDate + 
               ", returnDate=" + returnDate + ", fine=" + fine + '}';
    }
}
