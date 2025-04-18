package com.avis.repository;

import com.avis.entity.PurchaseTransaction;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, Long> {
	
	@Query("SELECT SUM(p.weight) FROM PurchaseTransaction p")
	Optional<BigDecimal> sumTotalWeight();

}
