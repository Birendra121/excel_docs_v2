package com.avis.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.avis.entity.VehicleRC;
import com.avis.service.VehicleRCService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VehicleRCController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRCService.class);
    private final VehicleRCService vehicleRCService;

    @GetMapping("/rcindex")
    public String showRCIndex(Model model) {
      //  model.addAttribute("rcDetailsList", List.of());
        model.addAttribute("rcDetailsList", Collections.emptyList());

        return "rcindex";
    }

    @PostMapping("/uploadExcel")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        try {
            List<String> failedUploads = vehicleRCService.uploadVehicleNumbers(file);

            model.addAttribute(failedUploads.isEmpty() ? "successMessage" : "warningMessage",
                    failedUploads.isEmpty() ? "File processed successfully." : "Some records failed to process.");
            model.addAttribute("failedUploads", failedUploads);

        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error processing file: " + e.getMessage());
        }

        return "rcindex";
    }
    
    //--re-check-   for single vehicle
    @GetMapping("/recheck")
    public String recheckVehicle(@RequestParam String vehicleNumber, RedirectAttributes redirectAttributes) {
        boolean success = vehicleRCService.recheckTransferRC(vehicleNumber);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Recheck successful for " + vehicleNumber);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Recheck failed for " + vehicleNumber);
        }
        return "redirect:/rcindex";
    }
    
//---- handler method to re-check by uploaded file
    
    @PostMapping("/recheckUpload")
    public String recheckVehicleBulk(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            List<String> failedUploads = vehicleRCService.uploadVehicleNumbersForReCheck(file);
            if (failedUploads.isEmpty()) {
                redirectAttributes.addFlashAttribute("successMessage", "Recheck successful for all vehicles.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Recheck failed for: " + String.join(", ", failedUploads));
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing the file.");
        }
        return "redirect:/rcindex";
    }

    
    //--end re-check

    @GetMapping("/getAll")
    public String getAllTransferRCDetails(Model model) {
        List<VehicleRC> vehicleRCList = vehicleRCService.getAllTransferRCDetails();
        model.addAttribute("rcDetailsList", vehicleRCList);
        model.addAttribute("warningMessage", vehicleRCList.isEmpty() ? "No Transfer RC details found." : "");
        return "rcindex";
    }
   // handler method to download all data from database
    @GetMapping("/exportExcel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=TransferRC_Details.xlsx");
        
        vehicleRCService.exportToExcel(response);
		
    }
   
   //-------method to download processed data
    @GetMapping("/downloadProcessedList")
    public void downloadProcessedList(HttpServletResponse response) throws IOException {
        vehicleRCService.exportProcessedListToExcel(response);
    }


}
