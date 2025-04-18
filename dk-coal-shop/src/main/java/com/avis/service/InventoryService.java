package com.avis.service;

import com.avis.repository.CoalTransactionRepository;
import com.avis.repository.PurchaseTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InventoryService {

    @Autowired
    private PurchaseTransactionRepository purchaseRepo;

    @Autowired
    private CoalTransactionRepository saleRepo;

    public BigDecimal getTotalPurchasedWeight() {
        return purchaseRepo.sumTotalWeight().orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalSoldWeight() {
        return saleRepo.sumTotalWeight().orElse(BigDecimal.ZERO);
    }

    public BigDecimal getCurrentStock() {
        return getTotalPurchasedWeight().subtract(getTotalSoldWeight());
    }
}
