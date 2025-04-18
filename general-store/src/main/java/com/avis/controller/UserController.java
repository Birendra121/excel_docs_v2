package com.avis.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.avis.entity.User;
import com.avis.service.ExcelService;

@Controller
public class UserController {

    private final ExcelService excelService;

    public UserController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "user-form";
    }

    @PostMapping("/submit")
    @ResponseBody
    public String submitForm(@ModelAttribute User user, Model model) {
        excelService.writeToExcel(user);
        model.addAttribute("message", "User details saved successfully!");
      //  return "user-form";
        return "User details saved successfully!";
    }
}

