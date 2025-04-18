package com.avis.controller;

import com.avis.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/inventory")
    public String showInventory(Model model) {
        BigDecimal totalPurchased = inventoryService.getTotalPurchasedWeight();
        BigDecimal totalSold = inventoryService.getTotalSoldWeight();
        BigDecimal stock = inventoryService.getCurrentStock();

        model.addAttribute("purchased", totalPurchased);
        model.addAttribute("sold", totalSold);
        model.addAttribute("stock", stock);

        return "inventory_summary";
    }
}