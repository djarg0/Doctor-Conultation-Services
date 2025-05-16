package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import jakarta.servlet.http.HttpSession;
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
    public String adminHome(HttpSession session) {
        String email= (String) session.getAttribute("email");
        if(email==null)         return "redirect:/AdminLogin";
        else                    return "AdminHome";
    }
    
    @GetMapping("/AdminManageCities")
    public String adminManageCities(HttpSession session) {
        String email= (String) session.getAttribute("email");
        if(email==null)         return "redirect:/AdminLogin";
        else                    return "AdminManageCities";
    }
    
    @GetMapping("/AdminManageSpecialities")
    public String adminManageSpecialites(HttpSession session) {
        String email= (String) session.getAttribute("email");
        if(email==null)         return "redirect:/AdminLogin";
        else                    return "AdminManageSpecialities";
    }
    
    @GetMapping("AdminManageDoctors")
    public String adminManageDoctors(HttpSession session) {
        String email= (String) session.getAttribute("email");
        if(email==null)         return "redirect:/AdminLogin";
        else                    return "AdminManageDoctors";
    }
}
