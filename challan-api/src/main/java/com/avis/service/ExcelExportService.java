package com.avis.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avis.entity.Challan;
import com.avis.repository.ChalanRepository;
import com.avis.util.Offence;


@Service
public class ExcelExportService {

	private final ChalanRepository chalanRepository;
	
	@Autowired
	private ExcelService excelService;
	
	public ExcelExportService(ChalanRepository chalanRepository) {
		this.chalanRepository=chalanRepository;
	}
	
	// Method to generate report for all challan details
	
	public byte[] generateExcelReport() throws IOException {
        List<Challan> challanList = chalanRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Challan Details");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Challan ID", "Vehicle Number", "RcRegistration Number","User Name", "Accused Name", "Challan Number", 
                                "Challan Date", "Challan Status", "Challan Amount", "RC State Code", 
                                "Challan Payment Source","Challan Payment Date", "Offence Name", "Offence Fine", 
                                "Motor Vehicle Act"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Populate rows with challan and offence data
            int rowIdx = 1;
            for (Challan challan : challanList) {
                for (Offence offence : challan.getOffences()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(challan.getId());
                    row.createCell(1).setCellValue(challan.getVehicleNumber());
                    row.createCell(2).setCellValue(challan.getRcRegistrationNumber());
                    
                    row.createCell(3).setCellValue(challan.getUserName());
                    row.createCell(4).setCellValue(challan.getAccusedName());
                    row.createCell(5).setCellValue(challan.getChallanNumber());
                    row.createCell(6).setCellValue(challan.getChallanDate());
                    row.createCell(7).setCellValue(challan.getChallanStatus());
                    row.createCell(8).setCellValue(challan.getChallanAmount());
                    row.createCell(9).setCellValue(challan.getRcStateCode());
                    row.createCell(10).setCellValue(challan.getChallanPaymentSource());
                    row.createCell(11).setCellValue(challan.getChallanPaymentDate());
                    
                    row.createCell(12).setCellValue(offence.getOffenceName());
                    row.createCell(13).setCellValue(offence.getOffenceFine());
                    row.createCell(14).setCellValue(offence.getMotorVehicleAct());
                    
                }
            }

            workbook.write(out);
            return out.toByteArray();
        }
	}
	
	//method for particular uploaded file challans
     // Method to generate Excel report for challans based on provided vehicle numbers
	
	
    }
	
	

