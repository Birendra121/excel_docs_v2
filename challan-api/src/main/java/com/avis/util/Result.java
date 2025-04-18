package com.avis.util;


import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {

		@JsonProperty("user_name")
	    private String userName;
		
	    @JsonProperty("accused_name")
	    private String accusedName;
	    
	    @JsonProperty("accused_father_name")
	    private String accusedFatherName;
	    
	    @JsonProperty("rc_registration_number")
	    private String rcRegistrationNumber;
	    
	    @JsonProperty("challan_number")
	    private String challanNumber;
	    
	    @JsonProperty("challan_id")
	    private int challanId;
	    
	    @JsonProperty("challan_date")
	    private String challanDate;
	    
	    @JsonProperty("challan_status")
	    private String challanStatus;
	    
	    @JsonProperty("challan_amount")
	    private String challanAmount;
	    
	    @JsonProperty("rc_state_code")
	    private String rcStateCode;
	    
	    @JsonProperty("rto_office_name")
	    private String rtoOfficeName;
	    
	    @JsonProperty("challan_receipt_url")
	    private String challanReceiptUrl;
	    
	    @JsonProperty("challan_payment_source")
	    private String challanPaymentSource;
	    
	    @JsonProperty("challan_payment_date")
	    private String challanPaymentDate;
	    
	    @JsonProperty("offences")
	    private List<Offence> offences;
	    
	 
	    private LocalDateTime deliveryDate;
	    private String tenure;
	    private LocalDateTime contractTerminationDate;
	    
	    
		
		
		public LocalDateTime getDeliveryDate() {
			return deliveryDate;
		}
		public void setDeliveryDate(LocalDateTime deliveryDate) {
			this.deliveryDate = deliveryDate;
		}
		public String getTenure() {
			return tenure;
		}
		public void setTenure(String tenure) {
			this.tenure = tenure;
		}
		public LocalDateTime getContractTerminationDate() {
			return contractTerminationDate;
		}
		public void setContractTerminationDate(LocalDateTime contractTerminationDate) {
			this.contractTerminationDate = contractTerminationDate;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getAccusedName() {
			return accusedName;
		}
		public void setAccusedName(String accusedName) {
			this.accusedName = accusedName;
		}
		public String getAccusedFatherName() {
			return accusedFatherName;
		}
		public void setAccusedFatherName(String accusedFatherName) {
			this.accusedFatherName = accusedFatherName;
		}
		public String getRcRegistrationNumber() {
			return rcRegistrationNumber;
		}
		public void setRcRegistrationNumber(String rcRegistrationNumber) {
			this.rcRegistrationNumber = rcRegistrationNumber;
		}
		public String getChallanNumber() {
			return challanNumber;
		}
		public void setChallanNumber(String challanNumber) {
			this.challanNumber = challanNumber;
		}
		public int getChallanId() {
			return challanId;
		}
		public void setChallanId(int challanId) {
			this.challanId = challanId;
		}
		public String getChallanDate() {
			return challanDate;
		}
		public String getRtoOfficeName() {
			return rtoOfficeName;
		}
		public void setRtoOfficeName(String rtoOfficeName) {
			this.rtoOfficeName = rtoOfficeName;
		}
		public void setChallanDate(String challanDate) {
			this.challanDate = challanDate;
		}
		public String getChallanStatus() {
			return challanStatus;
		}
		public void setChallanStatus(String challanStatus) {
			this.challanStatus = challanStatus;
		}
		public String getChallanAmount() {
			return challanAmount;
		}
		public void setChallanAmount(String challanAmount) {
			this.challanAmount = challanAmount;
		}
		public String getRcStateCode() {
			return rcStateCode;
		}
		public void setRcStateCode(String rcStateCode) {
			this.rcStateCode = rcStateCode;
		}
		public String getChallanReceiptUrl() {
			return challanReceiptUrl;
		}
		public void setChallanReceiptUrl(String challanReceiptUrl) {
			this.challanReceiptUrl = challanReceiptUrl;
		}
		public String getChallanPaymentSource() {
			return challanPaymentSource;
		}
		public void setChallanPaymentSource(String challanPaymentSource) {
			this.challanPaymentSource = challanPaymentSource;
		}
		public String getChallanPaymentDate() {
			return challanPaymentDate;
		}
		public void setChallanPaymentDate(String challanPaymentDate) {
			this.challanPaymentDate = challanPaymentDate;
		}
		public List<Offence> getOffences() {
			return offences;
		}
		public void setOffences(List<Offence> offences) {
			this.offences = offences;
		}
		@Override
		public String toString() {
			return "Result [userName=" + userName + ", accusedName=" + accusedName + ", accusedFatherName="
					+ accusedFatherName + ", rcRegistrationNumber=" + rcRegistrationNumber + ", challanNumber="
					+ challanNumber + ", challanId=" + challanId + ", challanDate=" + challanDate + ", challanStatus="
					+ challanStatus + ", challanAmount=" + challanAmount + ", rcStateCode=" + rcStateCode
					+ ", rtoOfficeName=" + rtoOfficeName + ", challanReceiptUrl=" + challanReceiptUrl
					+ ", challanPaymentSource=" + challanPaymentSource + ", challanPaymentDate=" + challanPaymentDate
					+ ", offences=" + offences + ", deliveryDate=" + deliveryDate + ", tenure=" + tenure
					+ ", contractTerminationDate=" + contractTerminationDate + "]";
		}
		
		
  
    
	    
}
