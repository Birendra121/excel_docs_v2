package com.avis.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
@Entity
@Table(name = "coal_transaction")
public class CoalTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String customerName;
    private String vehicleNo;
    private BigDecimal weight;
    private BigDecimal rate;
    private BigDecimal gstPercent;
    private BigDecimal amount;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    
    private String customerAddress;
    private String customerGstNo;

    private String shipToAddress;
    private String shipToGstNo;

    private String hsnCode;
    private String poNumber;

    private String totalAmountInWords;
    
    private String placeOfSupply;
    
    // Getters and Setters
    // (Omitted for brevity)
}

