/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.database;

/**
 *
 * @author Mystique
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class LibraryDatabaseSetup {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
    private static final String USER = "root"; // Change if needed
    private static final String PASS = "root"; // Change to your password

    public static void main(String[] args) {
        String sqlScript = """
            CREATE DATABASE IF NOT EXISTS library_db;
            USE library_db;

            CREATE TABLE IF NOT EXISTS admin (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                password VARCHAR(100) NOT NULL
            );

            CREATE TABLE IF NOT EXISTS books (
                id VARCHAR(20) PRIMARY KEY,
                title VARCHAR(100) NOT NULL,
                author VARCHAR(100) NOT NULL,
                isbn VARCHAR(20) UNIQUE,
                publisher VARCHAR(100),
                publication_year INT,
                category VARCHAR(50),
                status ENUM('AVAILABLE', 'ISSUED', 'LOST') DEFAULT 'AVAILABLE',
                student_id VARCHAR(20) NULL,
                barcode_path VARCHAR(255)
            );

            CREATE TABLE IF NOT EXISTS students (
                id VARCHAR(20) PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE,
                phone VARCHAR(20),
                address VARCHAR(255),
                total_fine DECIMAL(10,2) DEFAULT 0.00,
                barcode_path VARCHAR(255)
            );

            CREATE TABLE IF NOT EXISTS transactions (
                id INT AUTO_INCREMENT PRIMARY KEY,
                book_id VARCHAR(20) NOT NULL,
                student_id VARCHAR(20) NOT NULL,
                issue_date DATE NOT NULL,
                due_date DATE NOT NULL,
                return_date DATE NULL,
                fine DECIMAL(10,2) DEFAULT 0.00,
                FOREIGN KEY (book_id) REFERENCES books(id),
                FOREIGN KEY (student_id) REFERENCES students(id)
            );

            INSERT IGNORE INTO admin (username, password) VALUES ('admin', 'password');
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            for (String query : sqlScript.split(";")) {
                if (!query.trim().isEmpty()) {
                    stmt.execute(query.trim());
                }
            }

            System.out.println("✅ Database and tables created successfully.");
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}
