package com.avis.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import javax.persistence.Column;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChallanDetails {
	
	
	 @JsonProperty("result")
	    private List<Result> result;

	    @JsonProperty("request_timestamp")
	    private String requestTimestamp;

	    @JsonProperty("response_timestamp")
	    private String responseTimestamp;

	    private String vehicleNumber; // New field for vehicle number

	    private String deliveryDate;
	    private String tenure;
	    
	    private String contractTerminationDate;
	    
	   private Integer status;
	   
	   

		public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

		public String getDeliveryDate() {
			return deliveryDate;
		}

		public void setDeliveryDate(String deliveryDate) {
			this.deliveryDate = deliveryDate;
		}

		public String getTenure() {
			return tenure;
		}

		public void setTenure(String tenure) {
			this.tenure = tenure;
		}

		public String getContractTerminationDate() {
			return contractTerminationDate;
		}

		public void setContractTerminationDate(String contractTerminationDate) {
			this.contractTerminationDate = contractTerminationDate;
		}

		public List<Result> getResult() {
			return result;
		}

		public void setResult(List<Result> result) {
			this.result = result;
		}

		public String getRequestTimestamp() {
			return requestTimestamp;
		}

		public void setRequestTimestamp(String requestTimestamp) {
			this.requestTimestamp = requestTimestamp;
		}

		public String getResponseTimestamp() {
			return responseTimestamp;
		}

		public void setResponseTimestamp(String responseTimestamp) {
			this.responseTimestamp = responseTimestamp;
		}

		public String getVehicleNumber() {
			return vehicleNumber;
		}

		public void setVehicleNumber(String vehicleNumber) {
			this.vehicleNumber = vehicleNumber;
		}

    
}

    
    
    
	
	
	
    

