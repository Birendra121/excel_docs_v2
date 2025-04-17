package com.avis.controller;


import com.avis.entity.PurchaseTransaction;
import com.avis.service.PurchaseTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/purchase")
public class PurchaseTransactionController {

    @Autowired
    private PurchaseTransactionService service;

    // Show all purchases
    @GetMapping("/list")
    public String list(Model model) {
        List<PurchaseTransaction> purchases = service.getAll();
        model.addAttribute("purchases", purchases);
        return "purchase_list";
    }

    // Show add form
    @GetMapping("/add")
    public String showForm(Model model) {
        model.addAttribute("purchase", new PurchaseTransaction());
        return "add_purchase";
    }

    // Save the purchase
    @PostMapping("/save")
    public String save(@ModelAttribute("purchase") PurchaseTransaction purchase) {
        service.save(purchase);
        return "redirect:/purchase/list";
    }

    // Show invoice by ID
    @GetMapping("/invoice/{id}")
    public String printInvoice(@PathVariable("id") Long id, Model model) {
        PurchaseTransaction txn = service.getById(id);
        model.addAttribute("txn", txn);
        return "purchase_invoice";
    }
}
