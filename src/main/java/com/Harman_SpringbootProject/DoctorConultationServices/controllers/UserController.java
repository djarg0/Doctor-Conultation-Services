package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import jakarta.servlet.http.HttpSession;
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
    public String userShowSpeciality(HttpSession session) {
        Integer uid=(Integer) session.getAttribute("uid");
        if(uid == null)
        {
            return "redirect:/UserLogin";
        }
        else{
            return "UserShowSpeciality";
        }
        
    }
    
    @GetMapping("/UserShowDoctors")
    public String userShowDoctors(HttpSession session) {
        Integer uid=(Integer) session.getAttribute("uid");
        if(uid == null)
        {
            return "redirect:/UserLogin";
        }
        else{
            return "UserShowDoctors";
        }
    }
    
    @GetMapping("/UserDoctorDetails")
    public String userDoctorDetails(HttpSession session) {
        Integer uid=(Integer) session.getAttribute("uid");
        if(uid == null)
        {
            return "redirect:/UserLogin";
        }
        else{
            return "UserDoctorDetails";
        }
    }
    
    @GetMapping("/UserBookAppointment")
    public String userBookAppointment(HttpSession session) {
        Integer uid=(Integer) session.getAttribute("uid");
        if(uid == null)
        {
            return "redirect:/UserLogin";
        }
        else{
            return "UserBookAppointment";
        }
    }
    
    @GetMapping("/payment")
    public String payment(HttpSession session) {
        Integer uid=(Integer) session.getAttribute("uid");
        if(uid == null)
        {
            return "redirect:/UserLogin";
        }
        else{
            return "payment";
        }
    }
    
    @GetMapping("/UserManageAppointments")
    public String userManageAppointments(HttpSession session) {
        Integer uid=(Integer) session.getAttribute("uid");
        if(uid == null)
        {
            return "redirect:/UserLogin";
        }
        else{
            return "UserManageAppointments";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
        session.invalidate();
        return "redirect:/";
    }
}