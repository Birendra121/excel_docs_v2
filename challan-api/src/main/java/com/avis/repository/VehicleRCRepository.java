package com.avis.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.avis.entity.VehicleRC;

@Repository
public interface VehicleRCRepository extends JpaRepository<VehicleRC, Long>{

	Optional<VehicleRC> findByVehicleNumber(String vehicleNumber);
	Page<VehicleRC> findAll(Pageable pageable);
	
}
 