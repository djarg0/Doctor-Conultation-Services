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
    
    @GetMapping("/UserHome")
    public String userHome() {
         return "UserHome";
    }
}
