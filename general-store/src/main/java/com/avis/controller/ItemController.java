/*
 * package com.avis.controller;
 * 
 * 
 * import org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.ModelAttribute; import
 * org.springframework.web.bind.annotation.PostMapping;
 * 
 * import com.avis.entity.Item; import com.avis.service.ExcelService;
 * 
 * @Controller public class ItemController {
 * 
 * private final ExcelService excelService;
 * 
 * public ItemController(ExcelService excelService) { this.excelService =
 * excelService; }
 * 
 * @GetMapping("/") public String showForm(Model model) {
 * model.addAttribute("item", new Item()); return "item-form"; }
 * 
 * @PostMapping("/submit") public String submitForm(@ModelAttribute Item item,
 * Model model) { excelService.writeToExcel(item); model.addAttribute("message",
 * "Item saved successfully!"); return "item-form"; } }
 * 
 */