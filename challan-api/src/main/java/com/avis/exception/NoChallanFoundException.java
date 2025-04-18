package com.avis.exception;

public class NoChallanFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoChallanFoundException(String message) {
        super(message);
    }
    
    public NoChallanFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
