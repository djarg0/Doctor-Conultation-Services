package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import com.Harman_SpringbootProject.DoctorConultationServices.vmm.DBLoader;
import com.Harman_SpringbootProject.DoctorConultationServices.vmm.RDBMS_TO_JSON;
import jakarta.servlet.http.HttpSession;
import java.io.FileOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.sql.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AdminRestController {
    
    @PostMapping("/alogin")
    public String alogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        try {
            ResultSet rs= DBLoader.executeQuery("select * from admin where email='"+ email +"' and password='"+ password +"'");
            if(rs.next()) {
                session.setAttribute("email", email);
                return "success";
            }
            else {
                return "failure";   
            }
        }
        catch(Exception ex) {
            return ex.toString();
        }
    }
    
    @PostMapping("/acity")
    public String acity(@RequestParam String cityName, @RequestParam String cityDesc, @RequestParam MultipartFile cityPhoto) {
        
        String projectPath= System.getProperty("user.dir");
        String internalPath= "/src/main/resources/static";
        String folderName= "/myUploads";
        String orgName= "/" + cityPhoto.getOriginalFilename();
                
        try {
            ResultSet rs= DBLoader.executeQuery("select * from cities where cityname='"+ cityName +"'");
            if(rs.next()) {
                return "failed";
            }
            else {
                
                
                FileOutputStream fos= new FileOutputStream(projectPath + internalPath + folderName + orgName);
            
                byte[] b1= cityPhoto.getBytes();

                fos.write(b1);
                fos.close();
                
                rs.moveToInsertRow();
                rs.updateString("cityname", cityName);
                rs.updateString("citydesc", cityDesc);
                rs.updateString("cityphoto", orgName);
                rs.insertRow();
                return "success";   
            }
        }
        catch(Exception ex) {
            return "exception";
        }
    }
    
    @PostMapping("/showCity")
    public String showCity() {
            String ans= new RDBMS_TO_JSON().generateJSON("select * from cities");
            return ans;
    }
    @PostMapping("/deleteCity")
    public String deleteCity(@RequestParam String cityId) {
            try {
                ResultSet rs= DBLoader.executeQuery("select * from cities where cityid='" +cityId+ "'");
                if(rs.next()) {
                    rs.deleteRow();
                    
                    return "success";
                }
                else {
                    return "failed";
                }
            }
            catch(Exception ex) {
                return "exception";
            }
    }
    
    
    @PostMapping("/addSpeciality")
    public String addSpeciality(@RequestParam String sName, @RequestParam String sDesc, @RequestParam MultipartFile sPhoto) {
        
        String projectPath= System.getProperty("user.dir");
        String internalPath= "/src/main/resources/static";
        String folderName= "/myUploads";
        String orgName= "/" + sPhoto.getOriginalFilename();
                
        try {
            ResultSet rs= DBLoader.executeQuery("select * from specialities where sname='"+ sName +"'");
            if(rs.next()) {
                return "failed";
            }
            else {
                
                
                FileOutputStream fos= new FileOutputStream(projectPath + internalPath + folderName + orgName);
            
                byte[] b1= sPhoto.getBytes();

                fos.write(b1);
                fos.close();
                
                rs.moveToInsertRow();
                rs.updateString("sname", sName);
                rs.updateString("sdesc", sDesc);
                rs.updateString("sphoto", orgName);
                rs.insertRow();
                return "success";   
            }
        }
        catch(Exception ex) {
            return "exception";
        }
    }
    
    @PostMapping("/showSpeciality")
    public String showSpeciality() {
            String ans= new RDBMS_TO_JSON().generateJSON("select * from specialities");
            return ans;
    }
    @PostMapping("/deleteSpeciality")
    public String deleteSpeciality(@RequestParam String sId) {
            try {
                ResultSet rs= DBLoader.executeQuery("select * from specialities where sid='" +sId+ "'");
                if(rs.next()) {
                    rs.deleteRow();
                    
                    return "success";
                }
                else {
                    return "failed";
                }
            }
            catch(Exception ex) {
                return "exception";
            }
    }
    
    @PostMapping("/showDoctor")
    public String showDoctor() {
            String ans= new RDBMS_TO_JSON().generateJSON("select * from doctor");
            return ans;
    }
    @PostMapping("/accept")
    public String accept(@RequestParam int id) {
        try {
            ResultSet rs= DBLoader.executeQuery("select * from doctor where did= '"+ id +"'");
            if(rs.next()) {
                rs.updateString("dstatus", "accepted");
                rs.updateRow();
                return "success";
            }
            else {
                return "failed";
            }
        }
        catch(Exception ex) {
            return ex.toString();
        }
    }
    @PostMapping("/remove")
    public String remove(@RequestParam int id) {
        try {
            ResultSet rs= DBLoader.executeQuery("select * from doctor where did= '"+ id +"'");
            if(rs.next()) {
                rs.updateString("dstatus", "rejected");
                rs.updateRow();
                return "success";
            }
            else {
                return "failed";
            }
        }
        catch(Exception ex) {
            return ex.toString();
        }
    }
    
    @PostMapping("/adminChangePassword")
    public String adminChangePassword(@RequestParam String email, @RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String confirmPassword,HttpSession session) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from admin where email='" + email + "' and password='" + oldPassword + "'");
            if (rs.next()) {
                rs.moveToCurrentRow();
                rs.updateString("password", newPassword);
                rs.updateRow();
                session.removeAttribute("email");
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }
    
}
