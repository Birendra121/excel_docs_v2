package com.avis.util;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avis.entity.Challan;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Offence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonProperty("offence_name")
	@Column(name = "offence_name", length = 500)
    private String offenceName;
    @JsonProperty("offence_fine")
    private String offenceFine;
    @JsonProperty("motor_vehicle_act")
    private String motorVehicleAct;
    
    @ManyToOne
    @JoinColumn(name = "challan_id")
    private Challan challan;
    
    
    
	public Challan getChallan() {
		return challan;
	}
	public void setChallan(Challan challan) {
		this.challan = challan;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOffenceName() {
		return offenceName;
	}
	public void setOffenceName(String offenceName) {
		this.offenceName = offenceName;
	}
	public String getOffenceFine() {
		return offenceFine;
	}
	public void setOffenceFine(String offenceFine) {
		this.offenceFine = offenceFine;
	}
	public String getMotorVehicleAct() {
		return motorVehicleAct;
	}
	public void setMotorVehicleAct(String motorVehicleAct) {
		this.motorVehicleAct = motorVehicleAct;
	}
	@Override
	public String toString() {
		return "Offence [id=" + id + ", offenceName=" + offenceName + ", offenceFine=" + offenceFine
				+ ", motorVehicleAct=" + motorVehicleAct + ", challan=" + challan + "]";
	}
    
    
}
