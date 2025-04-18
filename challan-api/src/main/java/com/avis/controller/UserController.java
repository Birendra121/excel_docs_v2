package com.avis.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.avis.entity.User;
import com.avis.repository.UserRepository;
import com.avis.security.UserDetailsServiceImpl;

@Controller
public class UserController {
	
	@Autowired
	private UserDetailsServiceImpl userService;
	
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/index")
    public String showIndexPage(Model model) {
        return "index"; // This returns the index.html template
    }
    
    @GetMapping("/login")
    public String login(Model model, String logout, String error) {
    	if(logout !=null) {
    		model.addAttribute("logoutMessage", "You have been logged out successfully.");
    	}
    	if(error !=null) {
    		model.addAttribute("error", "Your username and password are invalid.");
    	}
        return "login";
    }
    
    @GetMapping("/error")
    public String handleError() {
        return "error"; // Name of the error page template
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
    	model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user,
            Model model) {

        // Check if user already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "User already exists with this email");
            return "register";
        }

        // Encode the password and save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "redirect:/login";
    }
    
    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        // Return the view name for the password change form
        return "change-password";
    }
    
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal, Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "New passwords do not match.");
            return "change-password"; // The name of the Thymeleaf template
        }

        try {
        	
            userService.changePassword(principal.getName(), currentPassword, newPassword);
            //return "redirect:/home";
        	model.addAttribute("successMessage", "Password changed successfully !! & Please try to Login");
        		       	
            return "change-password";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "change-password";
        }
    }
}
