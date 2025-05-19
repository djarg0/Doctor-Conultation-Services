package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import jakarta.servlet.http.HttpSession;
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
    public String doctorHome(HttpSession session) {        
        Integer did= (Integer) session.getAttribute("did");
        if(did==null)       return "redirect:/DoctorLogin";
        else                return "DoctorHome";
    }
    
    @GetMapping("/DoctorManagePhotos")
    public String doctorManagePhotos(HttpSession session) {        
        Integer did= (Integer) session.getAttribute("did");
        if(did==null)       return "redirect:/DoctorLogin";
        else                return "DoctorManagePhotos";
    }
    
    @GetMapping("/DoctorEditDetails")
    public String doctorEditDetails(HttpSession session) {        
        Integer did= (Integer) session.getAttribute("did");
        if(did==null)       return "redirect:/DoctorLogin";
        else                return "DoctorEditDetails";
    }
    
    @GetMapping("/DoctorManageAppointments")
    public String doctorManageAppointments(HttpSession session) {        
        Integer did= (Integer) session.getAttribute("did");
        if(did==null)       return "redirect:/DoctorLogin";
        else                return "DoctorManageAppointments";
    }
    
    @GetMapping("DoctorChangePassword")
    public String DoctorChangePassword(HttpSession session) {
        Integer did= (Integer) session.getAttribute("did");
        if(did==null)       return "redirect:/DoctorLogin";
        else                return "DoctorChangePassword";
    }
}
