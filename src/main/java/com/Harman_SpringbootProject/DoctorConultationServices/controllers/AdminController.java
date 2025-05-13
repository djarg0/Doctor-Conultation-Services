package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/AdminLogin")
    public String adminLogin() {
        return "AdminLogin";
    }
    
    @GetMapping("/AdminHome")
    public String adminHome() {
        return "AdminHome";
    }
    
    @GetMapping("/AdminManageCities")
    public String adminManageCities() {
        return "AdminManageCities";
    }
    
    @GetMapping("/AdminManageSpecialities")
    public String adminManageSpecialites() {
        return "AdminManageSpecialities";
    }
    
    @GetMapping("AdminManageDoctors")
    public String adminManageDoctors() {
        return "AdminManageDoctors";
    }
}
