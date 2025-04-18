package com.avis.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.avis.entity.CoalTransaction;
import com.avis.repository.CoalTransactionRepository;
import com.avis.service.CoalTransactionService;

@Controller
public class CoalTransactionController {

	@Autowired
    private CoalTransactionService service;
	
	@Autowired
	private CoalTransactionRepository coalTransactionRepository;

	@GetMapping("/")
	public String home(Model model) {
	    return "dashboard";
	}
	
	@GetMapping("/about")
	public String about() {
		return "about";
	}
	
	/*
    @GetMapping("/")
    public String home(Model model) {
        List<CoalTransaction> transactions = service.getAll();
        model.addAttribute("transactions", transactions);
        return "index";
    }
*/
	/*
	@GetMapping("/sales")
	public String viewSales(Model model) {
	    List<CoalTransaction> transactions = service.getAll();
	    model.addAttribute("transactions", transactions);
	    return "index";
	}
*/
	@GetMapping("/sales")
	public String viewSales(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
		int pageSize = 5;
		Page<CoalTransaction> transactionsPage = service.getPaginated(page, pageSize);

	    model.addAttribute("transactions", transactionsPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", transactionsPage.getTotalPages());

	    return "index";
	}
	@GetMapping("/edit/{id}")
	public String editInvoice(@PathVariable(name = "id") Long id, Model model) {
	    CoalTransaction txn = service.getById(id);
	    model.addAttribute("transaction", txn);
	    return "add_transaction"; // reuse the same form
	}

	
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("transaction", new CoalTransaction());
        return "add_transaction";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute CoalTransaction txn) {
        service.save(txn);
        return "redirect:/sales";
    }

    @GetMapping("/invoice/{id}")
    public String invoice(@PathVariable("id") Long id, Model model) {
        CoalTransaction txn = service.getById(id);
        model.addAttribute("txn", txn);
        return "invoice";
    }
    
    public String update(Long id, CoalTransaction txn) {
        CoalTransaction existing = coalTransactionRepository.findById(id).orElse(null);
        if (existing != null) {
            txn.setId(id); // ensure correct ID is used
            return save(txn); // reuse logic
        }
        return null;
    }

}


