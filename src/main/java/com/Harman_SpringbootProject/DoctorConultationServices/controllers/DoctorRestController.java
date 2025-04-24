package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import com.Harman_SpringbootProject.DoctorConultationServices.vmm.DBLoader;
import com.Harman_SpringbootProject.DoctorConultationServices.vmm.RDBMS_TO_JSON;
import com.mysql.cj.Session;
import jakarta.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class DoctorRestController {
    
    @PostMapping("/specialityList")
    public String specialityList() {
            String ans= new RDBMS_TO_JSON().generateJSON("select * from specialities");
            return ans;
    }
    
    @PostMapping("/addDoctor")
    public String addDoctor(@RequestParam String name, @RequestParam String contact, @RequestParam String email, @RequestParam String password, @RequestParam String speciality, @RequestParam String city, @RequestParam String longitude, @RequestParam String latitude, @RequestParam String startTime, @RequestParam String endTime, @RequestParam int slotAmount, @RequestParam String desc, @RequestParam String experience, @RequestParam String education, @RequestParam MultipartFile photo) {
        
        String projectPath= System.getProperty("user.dir");
        String internalPath= "/src/main/resources/static";
        String folderName= "/myUploads";
        String orgName= "/" + photo.getOriginalFilename();
        
        try {
            ResultSet rs2= DBLoader.executeQuery("select * from specialities where sid='"+speciality+"'");
            String sname = "";
            if(rs2.next())
            {
             sname= rs2.getString("sname");
            }
            
            try {
            ResultSet rs= DBLoader.executeQuery("select * from doctor where dname='"+ name +"'");
            if(rs.next()) {
                return "failed";
            }
            else {
                
                
                FileOutputStream fos= new FileOutputStream(projectPath + internalPath + folderName + orgName);
            
                byte[] b1= photo.getBytes();

                fos.write(b1);
                fos.close();
                
                rs.moveToInsertRow();
                rs.updateString("dname", name);
                rs.updateString("demail", email);
                rs.updateString("dpass", password);
                rs.updateString("dspeciality", speciality);
                rs.updateString("dcity", city);
                rs.updateString("dlatitude", latitude);
                rs.updateString("dlongitude", longitude);
                rs.updateString("dphoto", orgName);
                rs.updateString("dstart_time", startTime);
                rs.updateString("dend_time", endTime);
                rs.updateInt("dslot_amount", slotAmount);
                rs.updateString("dcontact", contact);
                rs.updateString("ddesc", desc);
                rs.updateString("dexperience", experience);
                rs.updateString("deducation", education);
                rs.updateString("dspecialityname", sname);
                rs.insertRow();
                return "success";   
            }
        }
            catch(Exception ex) {
                return ex.toString();
            }
            }
        catch(Exception ex) {
            return ex.toString();
        }
                
        
    }
    
    @PostMapping("/dlogin")
    public String alogin(@RequestParam String email, @RequestParam String password,HttpSession session) {
        try {
            ResultSet rs= DBLoader.executeQuery("select * from doctor where demail='"+ email +"' and dpass='"+ password +"'");
            if(rs.next()) {
                int id= rs.getInt("did");
                session.setAttribute("did", id);
                
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
    
    @PostMapping("/addPhoto")
    public String addPhoto(@RequestParam MultipartFile photo, @RequestParam String photoDesc, HttpSession session) {
        String projectPath= System.getProperty("user.dir");
        String internalPath= "/src/main/resources/static";
        String folderName= "/myUploads";
        String orgName= "/" + photo.getOriginalFilename();
                
        try {
            ResultSet rs= DBLoader.executeQuery("select * from photos");
            if(rs.next()) {
                FileOutputStream fos= new FileOutputStream(projectPath + internalPath + folderName + orgName);
            
                byte[] b1= photo.getBytes();

                fos.write(b1);
                fos.close();
                Integer id= (Integer) session.getAttribute("did");
                
                rs.moveToInsertRow();
                rs.updateString("pdesc", photoDesc);
                rs.updateString("photo", orgName);
                rs.updateInt("did", id);
                rs.insertRow();
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
    
    @GetMapping("/showPhotos")
    public String showPhotos(HttpSession session) {
            Integer id= (Integer) session.getAttribute("did");
            String ans= new RDBMS_TO_JSON().generateJSON("select * from photos where did= '"+ id +"'");
            return ans;
    }
    
    @PostMapping("/delete")
    public String delete(@RequestParam int id) {
        try {
            ResultSet rs= DBLoader.executeQuery("select * from photos where pid= '"+ id +"'");
            if(rs.next()) {
                rs.deleteRow();
                return "success";
            }
            else {
                return "Exception";
            }
        }
        catch(Exception ex) {
            return ex.toString();
        }
    }
}
