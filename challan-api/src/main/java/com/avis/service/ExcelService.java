package com.avis.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

import com.avis.entity.Challan;
import com.avis.exception.NoChallanFoundException;
import com.avis.repository.ChalanRepository;
import com.avis.util.ChallanDetails;
import com.avis.util.Offence;
import com.avis.util.Result;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class ExcelService {

	private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

	@Autowired
	private ZoopService zoopService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ChalanRepository challanRepository;

	
	
	public ExcelService(@Lazy ZoopService zoopService) {
        this.zoopService = zoopService;
    }
	
	// old method to parse Excel file when upload

	public List<String> parseExcelFile(MultipartFile file) throws IOException {
		List<String> vehicleNumbers = new ArrayList<>();

		try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {

			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				vehicleNumbers.add(row.getCell(0).getStringCellValue());
				logger.info("Read vehicle number: {}", row.getCell(0).getStringCellValue());
			}

		} catch (IOException e) {
			logger.error("Error reading Excel file", e);
			throw e;
		}

		return vehicleNumbers;
	}
 
	
//---new method to parse excel file---------
	
	public List<Challan> parseChallanExcelFile(MultipartFile file) throws IOException {
	    List<Challan> challans = new ArrayList<>();
	    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	    try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {
	        Sheet sheet = workbook.getSheetAt(0);

	        for (Row row : sheet) {
	            if (row.getRowNum() == 0) continue; // Skip header row

	            Challan challan = new Challan();

	            // Vehicle number
	            challan.setVehicleNumber(getCellValueAsString(row.getCell(0)));

	            // Delivery date
	            String deliveryDateStr = getCellValueAsString(row.getCell(1));
	            LocalDateTime deliveryDate = LocalDateTime.parse(deliveryDateStr, dateTimeFormatter);
	            challan.setDeliveryDate(deliveryDate);

	            // Tenure
	            String tenure = getCellValueAsString(row.getCell(2));
	            challan.setTenure(tenure);

	            // Contract termination date
	            String terminationDateStr = getCellValueAsString(row.getCell(3));
	            LocalDateTime contractTerminationDate = LocalDateTime.parse(terminationDateStr, dateTimeFormatter);
	            challan.setContractTerminationDate(contractTerminationDate);

	            challans.add(challan);
	            logger.info("Parsed Challan Details: {}", challan);

	        }
	    } catch (IOException e) {
	        logger.error("Error reading Excel file", e);
	        throw e;
	    }
	    return challans;
	}

	
	
	private String getCellValueAsString(Cell cell) {
	    if (cell == null) {
	        return "";
	    }
	    switch (cell.getCellType()) {
	        case STRING:
	            return cell.getStringCellValue();
	        case NUMERIC:
	            if (DateUtil.isCellDateFormatted(cell)) {
	                // For date cells
	                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	                return dateFormat.format(cell.getDateCellValue());
	            } else {
	                // For numeric cells
	                return String.valueOf((int) cell.getNumericCellValue());
	            }
	        case BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());
	        case FORMULA:
	            return cell.getCellFormula();
	        default:
	            return "";
	    }
	}

	
//---------------
	private List<ChallanDetails> lastProcessedChalanDetails = new ArrayList<>();

	public List<String> processVehicleNumbers(List<String> vehicleNumbers, Principal principle)
			throws MessagingException {
		List<String> vehicleNumbersWithChallans = new ArrayList<>();
		String recipientEmail = principle.getName();

		// Temporary variables to hold additional fields

		for (String vehicleNumber : vehicleNumbers) {
			try {
				// Fetch challan details for each vehicle number
				ChallanDetails chalanDetails = zoopService.getChallanDetails(vehicleNumber);

				if (chalanDetails != null && chalanDetails.getResult() != null
						&& !chalanDetails.getResult().isEmpty()) {
					 // Challans found for the vehicle number, so add it to the list
					vehicleNumbersWithChallans.add(vehicleNumber);

					// Save to database
					String status = "Challans Found";
					// zoopService.saveChalanDetails(chalanDetails, status);
					zoopService.saveChalanDetails(chalanDetails, status);

					// Store in lastProcessedChalanDetails for later use
					lastProcessedChalanDetails.add(chalanDetails);
                    
					// Send email
					// String recipientEmail = "V-birendrakumar@avis.co.in"; // Set the appropriate
					// recipient email
					// principle will get email by logged in user email

					// start-- comment for not send email

					// emailService.sendChalanDetailsByEmail(chalanDetails, vehicleNumber,
					// recipientEmail);
					logger.info("Email sent successfully for vehicle number: {}", vehicleNumber);
				} else {
					logger.info("No challans found for vehicle number: {}", vehicleNumber);
				}

			} catch (HttpStatusCodeException e) {
				if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
					// Log the 503 error and continue processing the next vehicle number
					logger.warn("Service Unavailable (503) for vehicle number {}: {}", vehicleNumber, e.getMessage());
					continue; // Skip to the next vehicle number
				} else {
					// Log other HTTP errors
					logger.error("HTTP error while processing vehicle number {}: {}", vehicleNumber, e.getMessage());
				}
			} catch (JsonProcessingException e) {
				logger.error("Error processing vehicle number {}: {}", vehicleNumber, e.getMessage());
			} catch (NoChallanFoundException e) {
				// Handle the case where no challans are found
				logger.warn("No challans found for vehicle number {}: {}", vehicleNumber, e.getMessage());
			}

		}
		return vehicleNumbersWithChallans;
	}

//-----------start process and save excel file------
	
	public List<String> processAndSaveChallans(List<Challan> challans, Principal principal) throws MessagingException {
	    List<String> vehicleNumbersWithChallans = new ArrayList<>();
	    String recipientEmail = principal.getName();

	    for (Challan challan : challans) {
	        try {
	            String vehicleNumber = challan.getVehicleNumber();

	            // Fetch challan details from the Zoop API
	            ChallanDetails chalanDetails = zoopService.getChallanDetails(vehicleNumber);

	            if (chalanDetails != null && chalanDetails.getResult() != null && !chalanDetails.getResult().isEmpty()) {
	                vehicleNumbersWithChallans.add(vehicleNumber);

	                for (Result result : chalanDetails.getResult()) {
	                    // Populate additional fields
	                    Challan enrichedChallan = new Challan();
	                    enrichedChallan.setVehicleNumber(vehicleNumber);
	                    enrichedChallan.setUserName(result.getUserName());
	                    enrichedChallan.setAccusedName(result.getAccusedName());
	                    enrichedChallan.setRcRegistrationNumber(result.getRcRegistrationNumber());
	                    enrichedChallan.setChallanNumber(result.getChallanNumber());
	                    enrichedChallan.setChallanDate(result.getChallanDate());
	                    enrichedChallan.setChallanStatus(result.getChallanStatus());
	                    enrichedChallan.setChallanAmount(result.getChallanAmount());
	                    enrichedChallan.setRcStateCode(result.getRcStateCode());
	                    enrichedChallan.setRtoOfficeName(result.getRtoOfficeName());
	                    enrichedChallan.setChallanPaymentSource(result.getChallanPaymentSource());
	                    enrichedChallan.setChallanPaymentDate(result.getChallanPaymentDate());

	                    // Enrich with Excel metadata
	                    enrichedChallan.setDeliveryDate(challan.getDeliveryDate());
	                    enrichedChallan.setTenure(challan.getTenure());
	                    enrichedChallan.setContractTerminationDate(challan.getContractTerminationDate());

	                    // Save enriched challan to the database
	                    challanRepository.save(enrichedChallan);
	                }

	                logger.info("Processed and saved challans for vehicle number: {}", vehicleNumber);
	            } else {
	                logger.info("No challans found for vehicle number: {}", vehicleNumber);
	            }
	        } catch (Exception e) {
	            logger.error("Error processing vehicle number {}: {}", challan.getVehicleNumber(), e.getMessage());
	        }
	    }
	    return vehicleNumbersWithChallans;
	}

	
	//-----------end process and save excel file------
    
    //--------------
	
	public void clearProcessedChalanDetails() {
		lastProcessedChalanDetails.clear(); // Call this after downloading the file to reset the state
	}

	
	
	public Workbook generateProcessedFileChallanExcel() {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Challan Details");

		// Create header row
		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Challan ID");
		// headerRow.createCell(1).setCellValue("Vehicle Number");
		headerRow.createCell(1).setCellValue("RcRegistration Number");
		headerRow.createCell(2).setCellValue("User Name");
		headerRow.createCell(3).setCellValue("Accused Name");
		headerRow.createCell(4).setCellValue("Challan Number");
		headerRow.createCell(5).setCellValue("Challan Date");
		headerRow.createCell(6).setCellValue("Challan Status");
		headerRow.createCell(7).setCellValue("Challan Amount");
		headerRow.createCell(8).setCellValue("RC State Code");
		headerRow.createCell(9).setCellValue("RTO Office Name");
		headerRow.createCell(10).setCellValue("Challan Payment Source");
		headerRow.createCell(11).setCellValue("Challan Payment Date");
		headerRow.createCell(12).setCellValue("Offence Name");
		headerRow.createCell(13).setCellValue("Offence Fine");
		headerRow.createCell(14).setCellValue("Motor Vehicle Act");

		int rowNum = 1;
		int serialNumber = 1; // Initialize serial number
		// Assuming 'lastProcessedChalanDetails' is a List of 'Challan' entities that
		// contains 'Offence' details as well.
		for (ChallanDetails chalanDetails : lastProcessedChalanDetails) {
			for (Result result : chalanDetails.getResult()) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(serialNumber++); // Serial number column
				// row.createCell(0).setCellValue(result.getChallanId()); // Assuming Challan ID
				// exists
				// row.createCell(1).setCellValue(result.ge);
				row.createCell(1).setCellValue(result.getRcRegistrationNumber());
				row.createCell(2).setCellValue(result.getUserName()); // Assuming User Name exists
				row.createCell(3).setCellValue(result.getAccusedName()); // Assuming Accused Name exists
				row.createCell(4).setCellValue(result.getChallanNumber());
				row.createCell(5).setCellValue(result.getChallanDate().toString());
				row.createCell(6).setCellValue(result.getChallanStatus());
				row.createCell(7).setCellValue(result.getChallanAmount());
				row.createCell(8).setCellValue(result.getRcStateCode()); // Assuming RC State Code exists
				row.createCell(9).setCellValue(result.getRtoOfficeName());
				row.createCell(10).setCellValue(result.getChallanPaymentSource()); // Assuming Payment Source exists
				row.createCell(11).setCellValue(result.getChallanPaymentDate().toString()); // Assuming Payment Date
																							// exists

				// Loop over offences
				List<Offence> offences = result.getOffences();
				if (offences != null && !offences.isEmpty()) {
					for (Offence offence : offences) {
						row.createCell(12).setCellValue(offence.getOffenceName());
						row.createCell(13).setCellValue(offence.getOffenceFine());
						row.createCell(14).setCellValue(offence.getMotorVehicleAct());
					}
				}

			}
		}

		return workbook;
	}

}
