/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.util;

/**
 *
 * @author Mystique
 */
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class BarcodeUtil {
    
    private static final String BARCODE_DIR = "barcodes/";
    
    static {
        // Create barcode directory if it doesn't exist
        java.io.File directory = new java.io.File(BARCODE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    public static String generateBookBarcode(String bookId) {
        String filePath = BARCODE_DIR + "book_" + bookId + ".png";
        generateBarcode(bookId, filePath, 300, 100);
        return filePath;
    }
    
    public static String generateStudentBarcode(String studentId) {
        String filePath = BARCODE_DIR + "student_" + studentId + ".png";
        generateBarcode(studentId, filePath, 300, 100);
        return filePath;
    }
    
    private static void generateBarcode(String barcodeText, String filePath, int width, int height) {
        try {
            Code128Writer barcodeWriter = new Code128Writer();
            BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.CODE_128, width, height);
            
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            
            System.out.println("Barcode generated successfully at: " + filePath);
        } catch (Exception e) {
            System.err.println("Error generating barcode: " + e.getMessage());
            e.printStackTrace();
        }
    }
}