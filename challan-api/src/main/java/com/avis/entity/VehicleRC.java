package com.avis.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicle_rc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore extra fields from API response
public class VehicleRC {

    @Id
    @Column(name = "external_id", unique = true, nullable = false)
    @JsonProperty("ID")
    private Long externalId;

    @Column(name = "vehicle_number", unique = true, nullable = false)
    @JsonProperty("LICENSE_NO")
    private String vehicleNumber;
    
    @Column(name = "owner_name")
    @JsonProperty("user_name")
    private String ownerName;

    @Column(name = "transfer_status")
    @JsonProperty("Last_RC_Check_Status")
    private String transferStatus;
    
    @JsonProperty("rc_blacklist_status")
    private String rc_blacklist_status;
    
 //   @Lob
 //   @Column(name = "last_rc_check_response", columnDefinition = "TEXT")
  //  @JsonProperty("Last_RC_Check_Response")
  //  @JsonIgnore
  //  private String lastRcCheckResponse;
    
   // @Column(name = "transfer_date")
   // @JsonProperty("Last_RC_Check_Time")
   // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  //  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SS")
  //  private LocalDateTime transferDate;

    @Column(name = "rc_chassis_number")
    @JsonProperty("rc_chassis_number")
    private String rcChassisNumber;

    @Column(name = "rc_engine_number")
    @JsonProperty("rc_engine_number")
    private String rcEngineNumber;

    @Column(name = "rc_expiry_date")
    @JsonProperty("rc_expiry_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy")
    private LocalDate rcExpiryDate;

    @Column(name = "vehicle_class")
    @JsonProperty("vehicle_class_description")
    private String vehicleClassDescription;

    @Column(name = "vehicle_color")
    @JsonProperty("vehicle_color")
    private String vehicleColor;

    @Column(name = "fuel_type")
    @JsonProperty("vehicle_fuel_description")
    private String vehicleFuelDescription;
    
    @Column(name = "financer")
    @JsonProperty("financer")
    private String financer;
    
    @Column(name = "insurance_company")
    @JsonProperty("insurance_company")
    private String insuranceCompany;

    @Column(name = "insurance_expiry")
    @JsonProperty("insurance_expiry_date")
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy")
    private LocalDate insuranceExpiryDate;

    @Column(name = "permit_expiry_date")
    @JsonProperty("rc_permit_expiry_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy")
    private LocalDate rcPermitExpiryDate;

    @Column(name = "fitness_expiry_date")
    @JsonProperty("rc_fit_upto")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy")
    private LocalDate rcFitUpto;

    @Column(name = "first_check")
    @JsonProperty("First_Check")
    private String firstCheck;
    
    @Column(name = "billable")
    @JsonProperty("Billable")
    private String billable;
    
    @Column(name = "vehicle_status")
    @JsonProperty("VEH_STS")
    private String vehicleStatus;
    
    @Column(name = "rc_pucc_expiry_date")
    @JsonProperty("rc_pucc_expiry_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy")
    private LocalDate rcPuccExpiryDate;
}
