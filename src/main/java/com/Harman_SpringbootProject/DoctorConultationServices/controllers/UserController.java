package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    
    @GetMapping("/UserSignup")
    public String userSignup() {
        
        return "UserSignup";
    }
    
    @GetMapping("/UserLogin")
    public String userLogin() {
         return "UserLogin";
    }
    
    @GetMapping("/")
    public String index() {
         return "index";
    }
    
    @GetMapping("/UserShowSpeciality")
    public String userShowSpeciality() {
        return "UserShowSpeciality";
    }
    
    @GetMapping("/UserShowDoctors")
    public String userShowDoctors() {
        return "UserShowDoctors";
    }
    
    @GetMapping("/UserDoctorDetails")
    public String userDoctorDetails() {
        return "UserDoctorDetails";
    }
    
    @GetMapping("UserBookAppointment")
    public String userBookAppointment() {
        return "UserBookAppointment";
    }
}
