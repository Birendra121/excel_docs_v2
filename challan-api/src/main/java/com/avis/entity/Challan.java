package com.avis.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avis.util.Offence;

@Entity
@Table(name = "challan_details")
public class Challan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String vehicleNumber;
	private String userName;
	private String accusedName;
	private String rcRegistrationNumber;
	private String challanNumber;
	private String challanDate;
	private String challanStatus;
	private String challanAmount;
	private String rcStateCode;
	private String rtoOfficeName;

	private String challanPaymentSource;
	private String challanPaymentDate;
	private String status;

	@Column(name = "delivery_date")
	private LocalDateTime deliveryDate;

	@Column(name = "tenure")
	private String tenure;

	@Column(name = "contract_termination_date")
	private LocalDateTime contractTerminationDate;

	@Column(name = "request_timestamp")
	private OffsetDateTime requestTimestamp;

	@Column(name = "response_timestamp")
	private OffsetDateTime responseTimestamp;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "challan_id")
	private List<Offence> offences;

	// Getters and setters, constructors, and other methods

	public Long getId() {
		return id;
	}

	public OffsetDateTime getRequestTimestamp() {
		return requestTimestamp;
	}

	public void setRequestTimestamp(OffsetDateTime requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}

	public OffsetDateTime getResponseTimestamp() {
		return responseTimestamp;
	}

	public void setResponseTimestamp(OffsetDateTime responseTimestamp) {
		this.responseTimestamp = responseTimestamp;
	}

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

	public void setId(Long id) {
		this.id = id;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
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

	public String getChallanDate() {
		return challanDate;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Offence> getOffences() {
		return offences;
	}

	public void setOffences(List<Offence> offences) {
		this.offences = offences;
	}

	@Override
	public String toString() {
		return "Challan [id=" + id + ", vehicleNumber=" + vehicleNumber + ", userName=" + userName + ", accusedName="
				+ accusedName + ", rcRegistrationNumber=" + rcRegistrationNumber + ", challanNumber=" + challanNumber
				+ ", challanDate=" + challanDate + ", challanStatus=" + challanStatus + ", challanAmount="
				+ challanAmount + ", rcStateCode=" + rcStateCode + ", rtoOfficeName=" + rtoOfficeName
				+ ", challanPaymentSource=" + challanPaymentSource + ", challanPaymentDate=" + challanPaymentDate
				+ ", status=" + status + ", deliveryDate=" + deliveryDate + ", tenure=" + tenure
				+ ", contractTerminationDate=" + contractTerminationDate + ", requestTimestamp=" + requestTimestamp
				+ ", responseTimestamp=" + responseTimestamp + ", offences=" + offences + "]";
	}

	public String getRtoOfficeName() {
		return rtoOfficeName;
	}

	public void setRtoOfficeName(String rtoOfficeName) {
		this.rtoOfficeName = rtoOfficeName;
	}

}
