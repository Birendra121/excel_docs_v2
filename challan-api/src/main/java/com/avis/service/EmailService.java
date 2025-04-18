package com.avis.service;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.avis.util.ChallanDetails;
import com.avis.util.Result;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	@Autowired
	private JavaMailSender emailSender;
	
	 @Value("${spring.mail.from}")
	    private String fromEmail;
	 

	 
	 
	 public boolean sendChalanDetailsByEmail(ChallanDetails chalanDetails, String vehicleNumber, String recipientEmail) throws MessagingException {
		 
	 List<Result> results= chalanDetails.getResult();
	 
	// Check if results is null or empty
     if (results == null || results.isEmpty()) {
         logger.error("No results found in challanDetails for vehicle number: {}", vehicleNumber);
         return false;
     }
		 MimeMessage message = emailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true indicates multipart message

	        try {
	            helper.setFrom(fromEmail);
	            helper.setTo(recipientEmail);
	            helper.setSubject("Challan Details for Vehicle Number " + vehicleNumber);

	            StringBuilder sb = new StringBuilder();
	            sb.append("<html>")
	              .append("<head>")
	              .append("<style>")
	              .append("table { width: 100%; border-collapse: collapse; }")
	              .append("table, th, td { border: 1px solid black; }")
	              .append("th, td { padding: 10px; text-align: left; }")
	              .append("th { background-color: #f2f2f2; }")
	              .append("</style>")
	              .append("</head>")
	              .append("<body>")
	              .append("<h2>Challan Details</h2>")
	              .append("<table>")
	              .append("<tr>")
	              .append("<th>Registration Number</th>")
	              .append("<th>User Name</th>")
	              .append("<th>Challan Number</th>")
	              .append("<th>Challan Amount</th>")
	              .append("<th>Challan Date</th>")
	              .append("<th>Challan Status</th>")
	              .append("<th>RC State Code</th>")
	              .append("<th>Challan Payment Source</th>")
	              .append("<th>Offence Name</th>")
	              .append("</tr>");

	           
	            	
	            
	          //  List<Result> results = chalanDetails.getResult();
	            for (Result result : chalanDetails.getResult()) {
	                sb.append("<tr>")
	                  .append("<td>").append(result.getRcRegistrationNumber()).append("</td>")
	                  .append("<td>").append(result.getUserName()).append("</td>")
	                  .append("<td>").append(result.getChallanNumber()).append("</td>")
	                  .append("<td>").append(result.getChallanAmount()).append("</td>")
	                  .append("<td>").append(result.getChallanDate()).append("</td>")
	                  .append("<td>").append(result.getChallanStatus()).append("</td>")
	                  .append("<td>").append(result.getRcStateCode()).append("</td>")
	                  .append("<td>").append(result.getChallanPaymentSource()).append("</td>")
	                  .append("<td>").append(result.getOffences()).append("</td>")
	                  .append("</tr>");
	            }
	            

	            sb.append("</table>")
	              .append("</body>")
	              .append("</html>");

	            helper.setText(sb.toString(), true); // true indicates HTML content

	            emailSender.send(message);
	            logger.info("Email sent successfully with challan details for vehicle number: {}", vehicleNumber);
	            return true;
	        } catch (MessagingException e) {
	            logger.error("Failed to send email to {}: {}. Subject: {}", 
	            		recipientEmail, e.getMessage(), "Challan Details for Vehicle Number " + vehicleNumber);
	            throw e;
	        }
	       
	    }
	 

	 public void sendChalanDetailsEmails(List<ChallanDetails> chalanDetailsList, String recipientEmail) throws MessagingException {
		    for (ChallanDetails chalanDetails : chalanDetailsList) {
		        // Assuming the recipient email is the same for all
		        sendChalanDetailsByEmail(chalanDetails, "Vehicle Number Placeholder", recipientEmail);
		    }
}
}