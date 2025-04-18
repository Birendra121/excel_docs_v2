package com.avis.service;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelHelper {

	public static List<String> extractVehicleNumbers(MultipartFile file) {
        List<String> vehicleNumbers = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Cell cell = currentRow.getCell(0); // Assuming vehicle numbers are in the first column
                if (cell != null) {
                    vehicleNumbers.add(cell.getStringCellValue().trim());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading Excel file: " + e.getMessage());
        }
        return vehicleNumbers;
    }

   
    
}
