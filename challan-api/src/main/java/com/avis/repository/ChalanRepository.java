package com.avis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.avis.entity.Challan;

@Repository
public interface ChalanRepository extends JpaRepository<Challan, Long>{

	Challan findByRcRegistrationNumberAndChallanNumber(String rcRegistrationNumber, String challanNumber);

	List<Challan> findByVehicleNumberIn(List<String> vehicleNumbers);

		
}

