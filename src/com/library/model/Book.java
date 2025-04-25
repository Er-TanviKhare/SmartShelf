/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.model;

/**
 *
 * @author Mystique
 */
public class Book {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private int publicationYear;
    private String category;
    private String status;
    private String studentId;
    private String barcodePath;
    
    // Constructor
    public Book(String id, String title, String author, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = "AVAILABLE";
    }
    
    // Full constructor
    public Book(String id, String title, String author, String isbn, 
                String publisher, int publicationYear, String category, 
                String status, String studentId, String barcodePath) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.category = category;
        this.status = status;
        this.studentId = studentId;
        this.barcodePath = barcodePath;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getBarcodePath() { return barcodePath; }
    public void setBarcodePath(String barcodePath) { this.barcodePath = barcodePath; }
    
    @Override
    public String toString() {
        return title + " by " + author;
    }
}
