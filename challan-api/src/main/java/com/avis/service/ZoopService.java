package com.avis.service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.avis.entity.Challan;
import com.avis.exception.ServiceUnavailableException;
import com.avis.repository.ChalanRepository;
import com.avis.repository.OffenceRepository;
import com.avis.util.ChallanDetails;
import com.avis.util.Offence;
import com.avis.util.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Service
public class ZoopService {

	private static final Logger logger= LoggerFactory.getLogger(ZoopService.class);
	
	 	@Value("${zoop.api.url}")
	    private String zoopApiUrl;

	    @Value("${zoop.api.appId}")
	    private String zoopAppId;

	    @Value("${zoop.api.apiKey}")
	    private String zoopApiKey;
	    
	    @Autowired
	    private RestTemplate restTemplate;
	    
	    @Autowired
	    private ObjectMapper objectMapper;
	    
	    @Autowired
	    private ChalanRepository chalanRepository;
	    
	    @Autowired
	    private OffenceRepository offenceRepository;
	    
	    @Autowired
	    private EmailService emailService; // Inject the email service
	  
	    
	  
	    @Retryable(
	            value = {RestClientException.class, ServiceUnavailableException.class},
	            maxAttempts = 5,
	            backoff = @Backoff(delay = 3000, multiplier = 1.5)
	        )
	    @Transactional
	public ChallanDetails getChallanDetails(String vehicleNumber) throws JsonMappingException, JsonProcessingException {
		
		HttpHeaders headers=new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("app-id", zoopAppId);
        headers.set("api-key", zoopApiKey);
        
        Map<String, Object> data = new HashMap<>();
        data.put("vehicle_registration_number", vehicleNumber);
        data.put("consent", "Y");
        data.put("consent_text", "I hear by declare my consent agreement for fetching my information via ZOOP API.");

        Map<String, Object> body = new HashMap<>();
        body.put("mode", "sync");
        body.put("data", data);
       // body.put("task_id", "f26eb21e-4c35-4491-b2d5-41fa0e545a34");
        body.put("task_id", UUID.randomUUID().toString());
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        
        
		ChallanDetails chalanDetails=null;
		try {
			logger.info("Sending request to Zoop API with headers: {}", headers);
			
			
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(zoopApiUrl, entity, String.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				
				String responseBody = responseEntity.getBody();
				logger.info("Received response: {}", responseBody);
				
				// Deserialize JSON to ChalanDetails
				chalanDetails=objectMapper.readValue(responseBody, ChallanDetails.class);
				// saveChalanDetails(chalanDetails, status); // Save to database will be handled in ExcelService
				saveChalanDetails(chalanDetails, "Received"); // Save to database
				
				
				System.out.println(chalanDetails);
				
			}else {
				// logger.error("Failed to get chalan details. Status code: {}",responseEntity.getStatusCodeValue());
				logger.error("Received non-OK status code: {}. Body: {}", responseEntity.getStatusCode(), responseEntity.getBody());
		    
				//if(responseEntity.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
					//throw new ServiceUnavailableException("Service is currently unavailable. Please try again later.");
				
				 // Handle specific timeout error 504 (Gateway Timeout)
	            if (responseEntity.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
	            	logger.error("Zoop API Timeout (504) occurred for vehicle: {}", vehicleNumber);
	                throw new ServiceUnavailableException("Zoop API Timeout: Unable to fetch challan details for vehicle " + vehicleNumber);
	            }
				}
			//}
			
		}catch (HttpClientErrorException e) {
			logger.error("HTTP error occurred: {} Response Body: {}", e.getMessage(), e.getResponseBodyAsString());
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                logger.error("Unauthorized! Check Zoop API credentials.");
            }else if(e.getStatusCode() == HttpStatus.FORBIDDEN){
            	logger.error("Zoop API Error: Wallet balance might be depleted. Please recharge.");

            	throw new ServiceUnavailableException("Zoop API Error: Wallet balance too low. Please recharge.");
            }else {
            	//logger.error("HTTP error occurred: {}", e.getMessage());
            	logger.error("Client error: {}. Response Body: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            
            }
		}
         catch(HttpServerErrorException e) {
        	// Handle server errors (e.g., 5xx)
        	 if(e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
        		 logger.error("Zoop API Timeout (504) error: {}", e.getMessage());
        		 throw new ServiceUnavailableException("Zoop API Timeout: Unable to fetch challan details for vehicle " + vehicleNumber, e);
        	    
        	   // logger.error("Server error from Zoop API: {}. Body: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
         
        	 }
        	 logger.error("Server error from Zoop API: {}. Body: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        	 
		}catch(Exception e) {
			logger.error("Exception occurred while fetching challan details: {}", e.getMessage());
			throw new ServiceUnavailableException("Service is currently unavailable. Please try again later.", e);
		}
		
		return chalanDetails;
		
		
	}
	    
	    //---------------------------------------------
	    	    
	    
	    @Transactional
	    public void saveChalanDetails(ChallanDetails chalanDetails, String status) {
	        if (chalanDetails != null && chalanDetails.getResult() != null && !chalanDetails.getResult().isEmpty()) {
	            List<Result> results =chalanDetails.getResult();
	            for (Result result : results) {
	            	
	            	
	            	
	            	// Check if a record with the same rcRegistrationNumber and challanNumber already exists
	            	Challan existingChalan = chalanRepository.findByRcRegistrationNumberAndChallanNumber(
	                        result.getRcRegistrationNumber(), result.getChallanNumber());
	            	
	            	// If no record exists, save the new record
	            	if(existingChalan ==null) {  
	            		
	            	Challan chalan = new Challan();
	            	
	                chalan.setUserName(result.getUserName());
	                chalan.setAccusedName(result.getAccusedName());
	                chalan.setRcRegistrationNumber(result.getRcRegistrationNumber());
	                chalan.setChallanNumber(result.getChallanNumber());
	                chalan.setChallanDate(result.getChallanDate());
	                chalan.setChallanStatus(result.getChallanStatus());
	                chalan.setChallanAmount(result.getChallanAmount());
	                chalan.setRcStateCode(result.getRcStateCode());
	                
	                chalan.setRtoOfficeName(result.getRtoOfficeName());
	                
	                chalan.setChallanPaymentSource(result.getChallanPaymentSource());
	                chalan.setChallanPaymentDate(result.getChallanPaymentDate());
	                
	             // Setting additional fields (delivery date, tenure, contract termination date)
	                chalan.setDeliveryDate(result.getDeliveryDate());  // Assuming result.getDeliveryDate() returns a LocalDateTime
	                chalan.setTenure(result.getTenure());  // Assuming result.getTenure() returns an Integer
	                chalan.setContractTerminationDate(result.getContractTerminationDate());  // Assuming result.getContractTerminationDate() returns a LocalDateTime

	                // Map the request and response timestamps
	                chalan.setRequestTimestamp(OffsetDateTime.parse(chalanDetails.getRequestTimestamp()));
	                chalan.setResponseTimestamp(OffsetDateTime.parse(chalanDetails.getResponseTimestamp()));
	                
	                
	                logger.info("Saving Challan: {}", chalan);
	             // Save the challan entity first
	                chalan = chalanRepository.save(chalan);
	                
	             // Set and save offences
                    List<Offence> offences = result.getOffences();
                    if(offences != null && !offences.isEmpty()) {
                    	for(Offence offence : offences) {
                    		offence.setChallan(chalan);  // Associate offence with the current challan
                    		offenceRepository.save(offence);
                    	}
                    }else {
                    	logger.warn("No offences found for challan number: {}", result.getChallanNumber());
                    }
                    
	                
                 // Save the challan entity again to ensure that offences are properly linked
	                chalanRepository.save(chalan);
	            	}else {
	            		logger.info("Duplicate challan found for rcRegistrationNumber: {} and challanNumber: {}. Skipping save.",
	            				result.getRcRegistrationNumber(), result.getChallanNumber());
	            	}
	            }
	        } else {
	            logger.warn("Challan details or results are null or empty. Nothing to save.");
	        }
	    }
	    
	  
	    //--------------
	    @Bean
	    public RestTemplate restTemplate() {
	        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
	        factory.setConnectTimeout(5000); // Connection timeout
	        factory.setReadTimeout(10000); // Read timeout
	        return new RestTemplate(factory);
	    }

}
