/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.database;

/**
 *
 * @author Mystique
 */
import com.library.model.Transaction;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    
    private Connection connection;
    
    public TransactionDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    public boolean addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (book_id, student_id, issue_date, due_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, transaction.getBookId());
            stmt.setString(2, transaction.getStudentId());
            stmt.setDate(3, java.sql.Date.valueOf(transaction.getIssueDate()));
            stmt.setDate(4, java.sql.Date.valueOf(transaction.getDueDate()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    transaction.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateTransactionReturn(String bookId, LocalDate returnDate, double fine) {
        String sql = "UPDATE transactions SET return_date = ?, fine = ? " +
                     "WHERE book_id = ? AND return_date IS NULL";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(returnDate));
            stmt.setDouble(2, fine);
            stmt.setString(3, bookId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating transaction: " + e.getMessage());
            return false;
        }
    }
    
    public Transaction getActiveTransactionByBookId(String bookId) {
        String sql = "SELECT * FROM transactions WHERE book_id = ? AND return_date IS NULL";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Transaction> getActiveTransactionsByStudentId(String studentId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE student_id = ? AND return_date IS NULL";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<Transaction> getOverdueTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE return_date IS NULL AND due_date < CURRENT_DATE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting overdue transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<Transaction> getTransactionHistory(String bookId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE book_id = ? ORDER BY issue_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction history: " + e.getMessage());
        }
        
        return transactions;
    }
    
    public List<Transaction> getStudentTransactionHistory(String studentId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE student_id = ? ORDER BY issue_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting student transaction history: " + e.getMessage());
        }
        
        return transactions;
    }
    
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("id"),
            rs.getString("book_id"),
            rs.getString("student_id"),
            rs.getDate("issue_date").toLocalDate(),
            rs.getDate("due_date").toLocalDate(),
            rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
            rs.getDouble("fine")
        );
    }
}