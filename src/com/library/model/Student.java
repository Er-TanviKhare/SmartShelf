/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.model;

/**
 *
 * @author Mystique
 */

public class Student {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private double totalFine;
    private String barcodePath;
    
    // Constructor
    public Student(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.totalFine = 0.0;
    }
    
    // Full constructor
    public Student(String id, String name, String email, String phone, 
                  String address, double totalFine, String barcodePath) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.totalFine = totalFine;
        this.barcodePath = barcodePath;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public double getTotalFine() { return totalFine; }
    public void setTotalFine(double totalFine) { this.totalFine = totalFine; }
    
    public String getBarcodePath() { return barcodePath; }
    public void setBarcodePath(String barcodePath) { this.barcodePath = barcodePath; }
    
    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}