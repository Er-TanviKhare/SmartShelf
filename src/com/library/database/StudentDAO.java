/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.database;

/**
 *
 * @author Mystique
 */
import com.library.model.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student entity.
 * Handles all database operations related to students.
 */
public class StudentDAO {
    
    private Connection connection;
    
    /**
     * Constructor that initializes the database connection.
     */
    public StudentDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    /**
     * Adds a new student to the database.
     * 
     * @param student The student object to add
     * @return true if the operation was successful, false otherwise
     */
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (id, name, email, phone, address, total_fine, barcode_path) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPhone());
            stmt.setString(5, student.getAddress());
            stmt.setDouble(6, student.getTotalFine());
            stmt.setString(7, student.getBarcodePath());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves a student by their ID.
     * 
     * @param id The student ID to search for
     * @return The student object if found, null otherwise
     */
    public Student getStudentById(String id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Retrieves all students from the database.
     * 
     * @return A list of all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting students: " + e.getMessage());
        }
        
        return students;
    }
    
    /**
     * Searches for students based on a search term.
     * Searches in name, email, and ID fields.
     * 
     * @param searchTerm The term to search for
     * @return A list of matching students
     */
    public List<Student> searchStudents(String searchTerm) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE name LIKE ? OR email LIKE ? OR id LIKE ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String term = "%" + searchTerm + "%";
            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setString(3, term);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
        }
        
        return students;
    }
    
    /**
     * Updates a student's information in the database.
     * 
     * @param student The student object with updated information
     * @return true if the operation was successful, false otherwise
     */
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getPhone());
            stmt.setString(4, student.getAddress());
            stmt.setString(5, student.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates a student's fine amount.
     * 
     * @param studentId The ID of the student
     * @param fine The fine amount to add
     * @return true if the operation was successful, false otherwise
     */
    public boolean updateStudentFine(String studentId, double fine) {
        String sql = "UPDATE students SET total_fine = total_fine + ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, fine);
            stmt.setString(2, studentId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating student fine: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Clears a student's fine (sets it to zero).
     * 
     * @param studentId The ID of the student
     * @return true if the operation was successful, false otherwise
     */
    public boolean clearStudentFine(String studentId) {
        String sql = "UPDATE students SET total_fine = 0 WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error clearing student fine: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deletes a student from the database.
     * 
     * @param studentId The ID of the student to delete
     * @return true if the operation was successful, false otherwise
     */
    public boolean deleteStudent(String studentId) {
        String sql = "DELETE FROM students WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the number of books currently issued to a student.
     * 
     * @param studentId The ID of the student
     * @return The number of books issued to the student
     */
    public int getIssuedBooksCount(String studentId) {
        String sql = "SELECT COUNT(*) FROM books WHERE student_id = ? AND status = 'ISSUED'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting issued books count: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Checks if a student has any overdue books.
     * 
     * @param studentId The ID of the student
     * @return true if the student has overdue books, false otherwise
     */
    public boolean hasOverdueBooks(String studentId) {
        String sql = "SELECT COUNT(*) FROM transactions " +
                     "WHERE student_id = ? AND return_date IS NULL AND due_date < CURRENT_DATE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking for overdue books: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Extracts a Student object from a ResultSet.
     * 
     * @param rs The ResultSet containing student data
     * @return A Student object
     * @throws SQLException If there's an error accessing the ResultSet
     */
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        return new Student(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getDouble("total_fine"),
            rs.getString("barcode_path")
        );
    }
}
