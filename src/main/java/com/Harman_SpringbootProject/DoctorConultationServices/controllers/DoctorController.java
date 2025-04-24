package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DoctorController {
    
    @GetMapping("/DoctorSignup")
    public String doctorSignup() {
        
        return "DoctorSignup";
    }
    
    @GetMapping("/DoctorLogin")
    public String doctorLogin() {
        
        return "DoctorLogin";
    }
    @GetMapping("/DoctorHome")
    public String doctorHome() {
        
        return "DoctorHome";
    }
    
    @GetMapping("/DoctorManagePhotos")
    public String doctorManagePhotos() {
        
        return "DoctorManagePhotos";
    }
    
    @GetMapping("/DoctorEditDetails")
    public String doctorEditDetails() {
        
        return "DoctorEditDetails";
    }
}
