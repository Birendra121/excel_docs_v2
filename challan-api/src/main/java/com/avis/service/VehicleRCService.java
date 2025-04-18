package com.avis.service;

import java.beans.FeatureDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.avis.entity.VehicleRC;
import com.avis.repository.VehicleRCRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleRCService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRCService.class);
    private final RestTemplate restTemplate;
    private final VehicleRCRepository vehicleRCRepository;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final String POST_API_URL = "http://10.149.6.11/EPLA/AVIS/AddNew?License_No=";
   // private static final String POST_API_URL = "http://10.149.6.11/EPLA/AVIS/Recheck?License_No=";
    private static final String GET_API_URL = "http://10.149.6.11/EPLA/AVIS/GetLeaseVehicles";  
    
    public List<String> uploadVehicleNumbers(MultipartFile file) throws IOException {
        List<String> failedUploads = new ArrayList<>();
        List<String> vehicleNumbers = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;

            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip header row
                }

                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String vehicleNumber = cell.getStringCellValue().trim();
                    if (!vehicleNumber.isEmpty()) {
                        vehicleNumbers.add(vehicleNumber);
                    }
                }
            }
        }

        if (vehicleNumbers.isEmpty()) {
            logger.warn("No valid vehicle numbers found in the uploaded file.");
            return failedUploads;
        }

        for (String vehicleNumber : vehicleNumbers) {
            boolean success = sendPostRequest(vehicleNumber);
            if (!success) {
                failedUploads.add(vehicleNumber);
            }
        }

        return failedUploads;
    }

    private boolean sendPostRequest(String vehicleNumber) {
        String apiUrl = POST_API_URL + vehicleNumber;

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, null, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Successfully sent request for vehicle: {}", vehicleNumber);
                return true;
            } else {
                logger.warn("Failed to add vehicle {}: Response {}", vehicleNumber, response.getStatusCode());
                return false;
            }

        } catch (Exception e) {
            logger.error("Error sending request for vehicle {}: {}", vehicleNumber, e.getMessage());
            return false;
        }
    }
  
    //--------Re-Check RC Details -- 27-Mar-2025
    
    /*
    public boolean recheckTransferRC(String vehicleNumber) {
        String recheckApiUrl = "http://10.149.6.11/EPLA/AVIS/Recheck?License_No=" + vehicleNumber;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(recheckApiUrl, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Successfully rechecked transfer RC for vehicle: {}", vehicleNumber);
                return true;
            } else {
                logger.warn("Failed to recheck transfer RC for vehicle {}: Response {}", vehicleNumber, response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error rechecking transfer RC for vehicle {}: {}", vehicleNumber, e.getMessage());
            return false;
        }
    }

    
    public List<String> uploadVehicleNumbersForReCheck(MultipartFile file) throws IOException {
        List<String> failedUploads = new ArrayList<>();
        List<String> vehicleNumbers = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;

            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip header row
                }

                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String vehicleNumber = cell.getStringCellValue().trim();
                    if (!vehicleNumber.isEmpty()) {
                        vehicleNumbers.add(vehicleNumber);
                    }
                }
            }
        }

        if (vehicleNumbers.isEmpty()) {
            logger.warn("No valid vehicle numbers found in the uploaded file.");
            return failedUploads;
        }

        for (String vehicleNumber : vehicleNumbers) {
            boolean success = recheckTransferRC(vehicleNumber);
            if (!success) {
                failedUploads.add(vehicleNumber);
            }
        }

        return failedUploads;
    }
    */
    
    public boolean recheckTransferRC(String vehicleNumber) {
        String recheckApiUrl = "http://10.149.6.11/EPLA/AVIS/Recheck?License_No=" + vehicleNumber;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(recheckApiUrl, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Successfully rechecked transfer RC for vehicle: {}", vehicleNumber);
                return true;
            } else {
                logger.warn("Failed to recheck transfer RC for vehicle {}: Response {}", vehicleNumber, response.getStatusCode());
                return false;
            }
        } catch (HttpClientErrorException e) {
            logger.error("Client error while rechecking transfer RC for vehicle {}: {} - {}", 
                         vehicleNumber, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            logger.error("Network error while rechecking transfer RC for vehicle {}: {}", vehicleNumber, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while rechecking transfer RC for vehicle {}: {}", vehicleNumber, e.getMessage());
        }
        return false;
    }

    public List<String> uploadVehicleNumbersForReCheck(MultipartFile file) throws IOException {
        List<String> failedUploads = Collections.synchronizedList(new ArrayList<>());
        List<String> vehicleNumbers = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            
            boolean isFirstRow = true;

            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip header row
                }

                Cell cell = row.getCell(0);
                if (cell != null) {
                    String vehicleNumber = extractCellValue(cell, evaluator);
                    if (!vehicleNumber.isEmpty()) {
                        vehicleNumbers.add(vehicleNumber);
                    }
                }
            }
        }

        if (vehicleNumbers.isEmpty()) {
            logger.warn("No valid vehicle numbers found in the uploaded file.");
            return failedUploads;
        }

        for (String vehicleNumber : vehicleNumbers) {
            boolean success = recheckTransferRC(vehicleNumber);
            if (!success) {
                failedUploads.add(vehicleNumber);
            }
        }

        return failedUploads;
    }

    private String extractCellValue(Cell cell, FormulaEvaluator evaluator) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue()); // Handle numbers without decimal
            case FORMULA:
                return evaluator.evaluate(cell).formatAsString();
            default:
                return "";
        }
    }

    // end re-check rc details
    
 // method to processed data store in temp memory and export in excel and save in database also with updated lists--17-Mar-2025
    
    private Set<String> processedVehicleNumbers = new HashSet<>(); // Track unique vehicle numbers
    private Map<String, VehicleRC> processedMap = new LinkedHashMap<>(); // Unique storage for UI display

    public List<VehicleRC> getAllTransferRCDetails() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(GET_API_URL, String.class);
            String jsonResponse = response.getBody();
            logger.info("Raw API Response: {}", jsonResponse);

            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            if (!rootNode.isArray() || rootNode.size() == 0) {
                logger.warn("API returned an empty list.");
                return new ArrayList<>(processedMap.values()); // Return stored unique data
            }

            if (rootNode.get(0).has("Status") && rootNode.get(0).has("ResultText")) {
                logger.error("API returned an error: {}", rootNode.get(0).get("ResultText").asText());
                return new ArrayList<>(processedMap.values()); // Return stored unique data
            }

            List<VehicleRC> vehicleList = objectMapper.readValue(jsonResponse, new TypeReference<List<VehicleRC>>() {});
            List<VehicleRC> newRecords = new ArrayList<>();
            List<VehicleRC> updatedRecords = new ArrayList<>();

            for (VehicleRC vehicle : vehicleList) {
                Optional<VehicleRC> existingVehicleOpt = vehicleRCRepository.findByVehicleNumber(vehicle.getVehicleNumber());

                if (existingVehicleOpt.isPresent()) {
                    VehicleRC existingVehicle = existingVehicleOpt.get();
                    
                    // Update only the changed fields
                    BeanUtils.copyProperties(vehicle, existingVehicle, getNullPropertyNames(vehicle));
                    updatedRecords.add(existingVehicle);
                } else {
                    newRecords.add(vehicle);
                }

                processedVehicleNumbers.add(vehicle.getVehicleNumber()); // Track unique entries
                processedMap.put(vehicle.getVehicleNumber(), vehicle); // Maintain only unique records
            }

            // Save new records
            if (!newRecords.isEmpty()) {
                vehicleRCRepository.saveAll(newRecords);
                logger.info("Saved {} new vehicle records to the database.", newRecords.size());
            }

            // Save updated records
            if (!updatedRecords.isEmpty()) {
                vehicleRCRepository.saveAll(updatedRecords);
                logger.info("Updated {} existing vehicle records in the database.", updatedRecords.size());
            }

            return new ArrayList<>(processedMap.values()); // Return only unique data for UI
        } catch (Exception e) {
            logger.error("Error fetching vehicle RC details", e);
            return new ArrayList<>(processedMap.values()); // Return stored unique data
        }
    }

    // Helper method to get null property names (to avoid overwriting existing values with null)
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                     .map(FeatureDescriptor::getName)
                     .filter(name -> src.getPropertyValue(name) == null)
                     .toArray(String[]::new);
    }

    
    
    //-----------method to export all data from database
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<VehicleRC> vehicleList = vehicleRCRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Vehicle RC Details");

        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Vehicle No", "Owner Name", "Transfer Status", "Chassis Number", "Engine Number", 
                            "RC Expiry Date", "Vehicle Class", "Vehicle Color", "Fuel Type", "Financer", 
                            "Insurance Company", "Insurance Expiry", "Permit Expiry", "Fitness Expiry", "First Check"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(getHeaderCellStyle(workbook));
        }

        int rowNum = 1;
        for (VehicleRC vehicle : vehicleList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(vehicle.getExternalId() != null ? vehicle.getExternalId().toString() : "N/A");
            row.createCell(1).setCellValue(vehicle.getVehicleNumber() != null ? vehicle.getVehicleNumber() : "N/A");
            row.createCell(2).setCellValue(vehicle.getOwnerName() != null ? vehicle.getOwnerName() : "N/A");
            row.createCell(3).setCellValue(vehicle.getTransferStatus() != null ? vehicle.getTransferStatus() : "N/A");
            row.createCell(4).setCellValue(vehicle.getRcChassisNumber() != null ? vehicle.getRcChassisNumber() : "N/A");
            row.createCell(5).setCellValue(vehicle.getRcEngineNumber() != null ? vehicle.getRcEngineNumber() : "N/A");

            // Handling date fields properly
            row.createCell(6).setCellValue(vehicle.getRcExpiryDate() != null ? vehicle.getRcExpiryDate().toString() : "N/A");
            row.createCell(7).setCellValue(vehicle.getVehicleClassDescription() !=null ? vehicle.getVehicleClassDescription().toString() : "N/A");
            row.createCell(12).setCellValue(vehicle.getInsuranceExpiryDate() != null ? vehicle.getInsuranceExpiryDate().toString() : "N/A");
            row.createCell(13).setCellValue(vehicle.getRcPermitExpiryDate() != null ? vehicle.getRcPermitExpiryDate().toString() : "N/A");
            row.createCell(14).setCellValue(vehicle.getRcFitUpto() != null ? vehicle.getRcFitUpto().toString() : "N/A");

            // Ensure numbers are treated correctly
            row.createCell(15).setCellValue(vehicle.getFirstCheck() != null ? vehicle.getFirstCheck() : "N/A");

        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }


  //--------Method to download the processed excel file and download it in excel file-------- with unique data download
    public void exportProcessedListToExcel(HttpServletResponse response) throws IOException {
        if (processedMap.isEmpty()) { // Ensure data is available
            logger.warn("No data available to export.");
            response.setContentType("text/plain");
            response.getWriter().write("No data available to export.");
            return;
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Processed Vehicle RC Details");

        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Vehicle No", "Owner Name", "Transfer Status", "Chassis Number", "Engine Number", 
                            "RC Expiry Date", "Vehicle Class", "Vehicle Color", "Fuel Type", "Financer", 
                            "Insurance Company", "Insurance Expiry", "Permit Expiry", "Fitness Expiry", "First Check", "Billable",
                            "VehicleStatus", "RC PuccExpiry Date"};

        // Set header style
        CellStyle headerStyle = getHeaderCellStyle(workbook);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Populate the sheet with unique vehicle data
        int rowNum = 1;
        for (VehicleRC vehicle : processedMap.values()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(vehicle.getExternalId() != null ? vehicle.getExternalId().toString() : "N/A");
            row.createCell(1).setCellValue(vehicle.getVehicleNumber() != null ? vehicle.getVehicleNumber() : "N/A");
            row.createCell(2).setCellValue(vehicle.getOwnerName() != null ? vehicle.getOwnerName() : "N/A");
            row.createCell(3).setCellValue(vehicle.getTransferStatus() != null ? vehicle.getTransferStatus() : "N/A");
            row.createCell(4).setCellValue(vehicle.getRcChassisNumber() != null ? vehicle.getRcChassisNumber() : "N/A");
            row.createCell(5).setCellValue(vehicle.getRcEngineNumber() != null ? vehicle.getRcEngineNumber() : "N/A");
            row.createCell(6).setCellValue(vehicle.getRcExpiryDate() != null ? vehicle.getRcExpiryDate().toString() : "N/A");
            row.createCell(7).setCellValue(vehicle.getVehicleClassDescription() != null ? vehicle.getVehicleClassDescription() : "N/A");
            row.createCell(8).setCellValue(vehicle.getVehicleColor() != null ? vehicle.getVehicleColor() : "N/A");
            row.createCell(9).setCellValue(vehicle.getVehicleFuelDescription() != null ? vehicle.getVehicleFuelDescription() : "N/A");
            row.createCell(10).setCellValue(vehicle.getFinancer() != null ? vehicle.getFinancer() : "N/A");
            row.createCell(11).setCellValue(vehicle.getInsuranceCompany() != null ? vehicle.getInsuranceCompany() : "N/A");
            row.createCell(12).setCellValue(vehicle.getInsuranceExpiryDate() != null ? vehicle.getInsuranceExpiryDate().toString() : "N/A");
            row.createCell(13).setCellValue(vehicle.getRcPermitExpiryDate() != null ? vehicle.getRcPermitExpiryDate().toString() : "N/A");
            row.createCell(14).setCellValue(vehicle.getRcFitUpto() != null ? vehicle.getRcFitUpto().toString() : "N/A");
            row.createCell(15).setCellValue(vehicle.getFirstCheck() != null ? vehicle.getFirstCheck() : "N/A");
            row.createCell(16).setCellValue(vehicle.getBillable() != null ? vehicle.getBillable() : "N/A");
            row.createCell(17).setCellValue(vehicle.getVehicleStatus() != null ? vehicle.getVehicleStatus() : "N/A");
            row.createCell(18).setCellValue(vehicle.getRcPuccExpiryDate() != null ? vehicle.getRcPuccExpiryDate().toString() : "N/A");
        }

        // Auto-size all columns for better readability
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Set response headers for Excel download
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=VehicleRC_Details.xlsx");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }

        workbook.close();
    }

}
