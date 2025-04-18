package com.avis.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avis.entity.CoalTransaction;
import com.avis.repository.CoalTransactionRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Service
public class CoalTransactionService {

	@Autowired
    private CoalTransactionRepository repository;

    public CoalTransaction save(CoalTransaction txn) {
        BigDecimal amount = txn.getWeight().multiply(txn.getRate());
        BigDecimal gstAmount = amount.multiply(txn.getGstPercent()).divide(new BigDecimal("100"));
        BigDecimal total = amount.add(gstAmount);

        txn.setAmount(amount);
        txn.setGstAmount(gstAmount);
        txn.setTotalAmount(total);
      //  txn.setPlaceOfSupply(null);
        
     // Convert total to words
        txn.setTotalAmountInWords(convertToWords(total.longValue()) + " Only");
        
        return repository.save(txn);
    }

    

    
    /*
    public List<CoalTransaction> getAll() {
        return repository.findAll();
    }
*/
    
    public CoalTransaction getById(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    // pagination method for index.html( to see sales transaction)
    public Page<CoalTransaction> getPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

 // Simple Indian-style amount-to-words converter
    public String convertToWords(long number) {
        final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        };
        final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        };

        if (number == 0) return "Zero";

        StringBuilder words = new StringBuilder();

        if ((number / 10000000) > 0) {
            words.append(convertToWords(number / 10000000)).append(" Crore ");
            number %= 10000000;
        }
        if ((number / 100000) > 0) {
            words.append(convertToWords(number / 100000)).append(" Lakh ");
            number %= 100000;
        }
        if ((number / 1000) > 0) {
            words.append(convertToWords(number / 1000)).append(" Thousand ");
            number %= 1000;
        }
        if ((number / 100) > 0) {
            words.append(convertToWords(number / 100)).append(" Hundred ");
            number %= 100;
        }
        if (number > 0) {
            if (words.length() > 0) words.append("and ");
            if (number < 20) words.append(units[(int) number]);
            else {
                words.append(tens[(int) number / 10]);
                if ((number % 10) > 0) {
                    words.append("-").append(units[(int) number % 10]);
                }
            }
        }

        return words.toString().trim();
    }
}
