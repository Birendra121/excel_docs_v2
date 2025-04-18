package com.avis.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avis.entity.CoalTransaction;

public interface CoalTransactionRepository extends JpaRepository<CoalTransaction, Long>{

	@Query("SELECT SUM(c.weight) FROM CoalTransaction c")
	Optional<BigDecimal> sumTotalWeight();

}
