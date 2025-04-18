package com.avis.service;

import com.avis.entity.PurchaseTransaction;
import com.avis.repository.PurchaseTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PurchaseTransactionService {

    @Autowired
    private PurchaseTransactionRepository repository;

    @Autowired
    private CoalTransactionService coalTransactionService;
    
    public PurchaseTransaction save(PurchaseTransaction txn) {
    	CoalTransactionService cts=new CoalTransactionService();
    	
        BigDecimal amount = txn.getWeight().multiply(txn.getRate());
        BigDecimal gstAmount = amount.multiply(txn.getGstPercent()).divide(BigDecimal.valueOf(100));
        BigDecimal total = amount.add(gstAmount);

        txn.setAmount(amount);
        txn.setGstAmount(gstAmount);
        txn.setTotalAmount(total);
        txn.setTotalAmountInWords(cts.convertToWords(total.longValue()) + " Only");

        return repository.save(txn);
    }

    public List<PurchaseTransaction> getAll() {
        return repository.findAll();
    }

    public PurchaseTransaction getById(Long id) {
        return repository.findById(id).orElse(null);
    }
/*
    private String convertToWords(long number) {
        // (Same utility method as in sale service, reused here)
        // ... conversion logic omitted for brevity
    	
    	
        return "Amount In Words"; // Replace with actual logic
    }
    */
}