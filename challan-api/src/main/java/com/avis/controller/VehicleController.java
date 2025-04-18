package com.avis.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import com.avis.entity.Challan;
import com.avis.entity.Vehicle;
import com.avis.exception.NoChallanFoundException;
import com.avis.exception.ServiceUnavailableException;
import com.avis.repository.ChalanRepository;
import com.avis.service.EmailService;
import com.avis.service.ExcelExportService;
import com.avis.service.ExcelService;
import com.avis.service.ZoopService;
import com.avis.util.ChallanDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class VehicleController {

	private static final Logger logger=LoggerFactory.getLogger(VehicleController.class);
	
	@Autowired
    private ZoopService zoopService;
	
	@Autowired
    private ExcelService excelService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private ChalanRepository chalanRepository;
	
	@Autowired
	private ExcelExportService excelExportService;// this is for download challan in excel

    
	
	@GetMapping("/home")
	public String home(Model model, Principal principal) {
		if(principal !=null) {
			model.addAttribute("username", principal.getName());
		}
		return "home";
	}
	
	@GetMapping("/vehicle")
	public String showForm(Model model) {
		model.addAttribute("vehicle", new Vehicle());
		return "vehicleForm";
	}

	
	@PostMapping("/vehicle")
	public String submitForm(@ModelAttribute("vehicle") Vehicle vehicle, Model model, Principal principle) {
		
		ChallanDetails chalanDetails = null;
		try {
			chalanDetails = zoopService.getChallanDetails(vehicle.getNumber());
			
			// Check if API response contains status 113 (wallet balance issue)
	        if (chalanDetails != null && "113".equals(chalanDetails.getStatus())) {
	            logger.warn("Wallet balance is reaching zero.");
	            model.addAttribute("walletWarning", "Wallet balance is low or empty. Please recharge to continue.");
	            return "vehicleForm"; // Return to the form with a warning message
	        }
	        
		} catch (HttpClientErrorException.Forbidden e) { // Handling 403 Forbidden explicitly
	        logger.error("403 Forbidden: Possible wallet balance issue", e);
	        model.addAttribute("walletWarning", "Wallet balance is low or empty. Please recharge to continue.");
	        return "vehicleForm";
		
		}catch(HttpServerErrorException.GatewayTimeout e) {
			logger.error("504 Gateway Timeout: The external service is unavailable", e);
			model.addAttribute("errorMessageGateway", "The service is currently unavailable (504 Gateway Timeout). Please try again later.");
			return "vehicleForm";
		}
		
		catch(JsonMappingException e) {
			logger.error("Error mapping JSON ", e);
			model.addAttribute("errorMessage", "Error mapping JSON: " + e.getMessage());
			return "vehicleForm";
			
		}catch(JsonProcessingException e) {
			logger.error("Error processing JSON ", e);
			model.addAttribute("errorMessage", "Error processing JSON: " + e.getMessage());
			return "vehicleForm";
			
		}catch(ServiceUnavailableException e) {
			logger.error("Service is currently unavailable",e);
			
			 // Check if the exception message is about wallet balance
			if(e.getMessage().contains("Wallet balance too low")) {
				model.addAttribute("walletWarning", "Zoop API wallet balance is low. Please recharge to continue using the service.");
			}else {
				model.addAttribute("errorMessage", e.getMessage());
			}
			
			
			//throw e;   // Propagate to global exception handler
			return "vehicleForm";
		}
		
		catch(Exception e) {
			logger.error("An unexpected error occurred",e);
			model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
			return "vehicleForm";
		}
		
		if(chalanDetails==null || chalanDetails.getResult() == null || chalanDetails.getResult().isEmpty()) {
			model.addAttribute("noChalanMessage", "No challans found for vehicle number: "+ vehicle.getNumber());
		}else {
			model.addAttribute("chalanDetails", chalanDetails);
			
			//commented for skip mail while personal network, it will work on lan cable
			
			//start send email commented
			 // try { // Set the recipient email address
			  
			 // String recipientEmail = "V-birendrakumar@avis.co.in";
				// Use the email from the logged-in user
				  String recipientEmail= principle.getName();
			 // emailService.sendChalanDetailsByEmail(chalanDetails, vehicle.getNumber(), recipientEmail); 
			 // } 
			 // catch (MessagingException e)
			  {
			 // logger.error("Error sending email: {}", e.getMessage());
			 // model.addAttribute("errorMessage", "Error sending email: " + e.getMessage());
			  
			 }
			 
			
		// end of email sender
		}
        
        return "result";
	}
	
	
	
	@GetMapping("/upload")
	public String showUploadForm(Model model) {
		model.addAttribute("file", new Object());
        return "upload";
	}
	
	/*
	
	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model, Principal principal, HttpSession session) {
	    try {
	        // Parse the Excel file and extract vehicle numbers
	      //  List<String> vehicleNumbers = excelService.parseExcelFile(file);
	        List<String> vehicleNumbers = excelService.parseExcelFile(file);

	     // Call processExcelFile method to handle file parsing and processing
	        
	        
	        if (vehicleNumbers.isEmpty()) {
	            model.addAttribute("message", "No vehicle numbers found in the file.");
	            return "upload";
	        }
	        // Store vehicle numbers in the session
	        session.setAttribute("uploadedVehicleNumbers", vehicleNumbers);
	        
	        // Process vehicle numbers and send emails
	        List<String> vehicleNumbersWithChallans = excelService.processVehicleNumbers(vehicleNumbers, principal);
	     // Call processExcelFile method to handle file parsing and processing
	        
	        
	        if (!vehicleNumbersWithChallans.isEmpty()) {
	            model.addAttribute("message", "File uploaded, processed successfully, and emails sent.");
	            model.addAttribute("vehicleNumbersWithChallans", vehicleNumbersWithChallans);
	            
	         // Store the processed vehicle numbers for download
	            session.setAttribute("lastProcessedVehicleNumbers", vehicleNumbersWithChallans);
	            
	            
	         // Store challan details in session for later download
	            session.setAttribute("challanDetails", vehicleNumbersWithChallans);
	            
	         // Provide download link
	            model.addAttribute("downloadLink", "/download-challan-details");
	            
	         // Provide the download link for the last processed vehicle numbers
	            model.addAttribute("downloadLink", "/download-last-processed-challan-details");
	            
	        } else {
	            model.addAttribute("message", "File uploaded and processed successfully. No challans found.");
	        
	        }

	       // model.addAttribute("vehicleNumbersWithChallans", vehicleNumbersWithChallans);
	        model.addAttribute("downloadLink", "/download-challan-details");
	        model.addAttribute("downloadLink", "/download-last-processed-challan-details");

	    } catch (IOException e) {
	        model.addAttribute("errorMessage", "Error processing file: " + e.getMessage());
	    } catch(MessagingException e) {
	    	model.addAttribute("errorMessage", "Error sending email: "+ e.getMessage());
	    }catch(NoChallanFoundException e) {
	    	// Handle the case where no challans are found
	    	model.addAttribute("noChallans", "No challans found for some vehicle numbers." + e.getMessage());
	    }
	     
	    catch (Exception e) {
	        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
	    }
	    return "upload";
	}
	
	*/
	//---duplicate method to self rnd--------
	
	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model, Principal principal, HttpSession session) {
	    try {
	        // Parse the Excel file and extract vehicle numbers
	        List<String> vehicleNumbers = excelService.parseExcelFile(file);
	       // List<Challan> vehicleNumbers = excelService.parseChallanExcelFile(file);

	     // Call processExcelFile method to handle file parsing and processing
	        
	        
	        if (vehicleNumbers.isEmpty()) {
	            model.addAttribute("message", "No vehicle numbers found in the file.");
	            return "upload";
	        }
	        // Store vehicle numbers in the session
	        session.setAttribute("uploadedVehicleNumbers", vehicleNumbers);
	        
	        // Process vehicle numbers and send emails // processVehicleNumbers
	        // List<String> vehicleNumbersWithChallans = excelService.processAndSaveChallans(vehicleNumbers, principal);
	        List<String> vehicleNumbersWithChallans = excelService.processVehicleNumbers(vehicleNumbers, principal);
	     // Call processExcelFile method to handle file parsing and processing
	        
	        
	        if (!vehicleNumbersWithChallans.isEmpty()) {
	            model.addAttribute("message", "File uploaded, processed successfully, and emails sent.");
	            model.addAttribute("vehicleNumbersWithChallans", vehicleNumbersWithChallans);
	            
	         // Store the processed vehicle numbers for download
	            session.setAttribute("lastProcessedVehicleNumbers", vehicleNumbersWithChallans);
	            
	            
	         // Store challan details in session for later download
	            session.setAttribute("challanDetails", vehicleNumbersWithChallans);
	            
	         // Provide download link
	            model.addAttribute("downloadLink", "/download-challan-details");
	            
	         // Provide the download link for the last processed vehicle numbers
	            model.addAttribute("downloadLink", "/download-last-processed-challan-details");
	            
	        } else {
	            model.addAttribute("message", "File uploaded and processed successfully. No challans found.");
	        
	        }

	       // model.addAttribute("vehicleNumbersWithChallans", vehicleNumbersWithChallans);
	        model.addAttribute("downloadLink", "/download-challan-details");
	        model.addAttribute("downloadLink", "/download-last-processed-challan-details");

	    } catch (IOException e) {
	        model.addAttribute("errorMessage", "Error processing file: " + e.getMessage());
	    } catch(MessagingException e) {
	    	model.addAttribute("errorMessage", "Error sending email: "+ e.getMessage());
	    }catch(NoChallanFoundException e) {
	    	// Handle the case where no challans are found
	    	model.addAttribute("noChallans", "No challans found for some vehicle numbers." + e.getMessage());
	    }
	     
	    catch (Exception e) {
	        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
	    }
	    return "upload";
	}
	
	//--------------end----rnd
	
	
	//--------------------------------------------

//hander method for download excel report of challans
	
	@GetMapping("/challan-excel")
    public ResponseEntity<byte[]> downloadChallanExcel() {
        try {
            byte[] excelContent = excelExportService.generateExcelReport();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=Challan_details.xlsx");

            return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
        } catch (IOException e) {
           // e.printStackTrace();
        	logger.error("Error generating Excel report", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } 
    
    //handler method for download uploaded file challan details

        @GetMapping("/download-last-processed-challan-details")
        public ResponseEntity<ByteArrayResource> downloadProcessedFileChallanExcel(HttpServletResponse response) {
            Workbook workbook = excelService.generateProcessedFileChallanExcel();
            
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                byte[] bytes = outputStream.toByteArray();

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=challan_details.xlsx");
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(new ByteArrayResource(bytes));
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            } finally {
                excelService.clearProcessedChalanDetails(); // Clear the stored data after downloading
            }
        }
}
