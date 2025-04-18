package com.avis.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	/*
	 * @ExceptionHandler(ServiceUnavailableException.class) public String
	 * handleServiceUnavailable(ServiceUnavailableException e, Model model) {
	 * logger.error("ServiceUnavailableException: {}", e.getMessage(), e);
	 * model.addAttribute("errorMessage",
	 * "The service is currently unavailable. Please try again later.");
	 * 
	 * return "errorPage";
	 */	
	//}
	
	@ExceptionHandler(NoChallanFoundException.class)
    public String handleNoChallanFound(NoChallanFoundException e, Model model) {
		logger.warn("NoChallanFoundException: {}", e.getMessage());
        model.addAttribute("noChalanMessage", "No challan found for the given details.");
        return "vehicleForm"; // Redirect back to the form with no-challan message
    }
	
	@ExceptionHandler(Exception.class)
	public String handleGenericException(Exception e, Model model) {
		 logger.error("Exception: {}", e.getMessage());
		model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
		return "errorPage";
	}
}
