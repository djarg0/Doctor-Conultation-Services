package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import com.Harman_SpringbootProject.DoctorConultationServices.vmm.DBLoader;
import com.Harman_SpringbootProject.DoctorConultationServices.vmm.RDBMS_TO_JSON;
import jakarta.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserRestController {
    
    
    @PostMapping("/addUser")
    public String addUser(@RequestParam String name, @RequestParam String contact, @RequestParam String email,
            @RequestParam String password,@RequestParam String address, @RequestParam String gender,
            @RequestParam String dob, @RequestParam String bloodGroup, @RequestParam MultipartFile photo) {

        String projectPath = System.getProperty("user.dir");
        String internalPath = "/src/main/resources/static";
        String folderName = "/myUploads";
        String orgName = "/" + photo.getOriginalFilename();

            try {
                ResultSet rs = DBLoader.executeQuery("select * from user where uemail='" + email + "'");
                if (rs.next()) {
                    return "failed";
                } else {

                    FileOutputStream fos = new FileOutputStream(projectPath + internalPath + folderName + orgName);

                    byte[] b1 = photo.getBytes();

                    fos.write(b1);
                    fos.close();

                    rs.moveToInsertRow();
                    rs.updateString("uname", name);
                    rs.updateString("uemail", email);
                    rs.updateString("upass", password);
                    rs.updateString("ucontact", contact);
                    rs.updateString("uaddress", address);
                    rs.updateString("ugender", gender);
                    rs.updateString("udob", dob);
                    rs.updateString("ubloodgroup", bloodGroup);
                    rs.updateString("uphoto", orgName);
                    rs.insertRow();
                    return "success";
                }
            } catch (Exception ex) {
                return ex.toString();
            }

    }
    
    @PostMapping("/ulogin")
    public String ulogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from user where uemail='" + email + "' and upass='" + password + "'");
            if (rs.next()) {
                int id = rs.getInt("uid");
                session.setAttribute("uid", id);

                return "success";
            } else {
                return "failure";
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }
    
    
    @PostMapping("getSpeciality")
    public String getSpeciality(@RequestParam String city) {
        String ans= new RDBMS_TO_JSON().generateJSON("SELECT DISTINCT * FROM specialities s JOIN doctor d ON s.sname = d.dspecialityname WHERE d.dcity ='"+city+"'");
        return ans;
    }
    
    @PostMapping("getDoctors")
    public String getDoctors(@RequestParam String city, @RequestParam int sid) {
        String ans= new RDBMS_TO_JSON().generateJSON("SELECT DISTINCT * FROM specialities s JOIN doctor d ON s.sname = d.dspecialityname WHERE d.dcity ='"+city+"' AND s.sid = '"+sid+"'");
        return ans;
    }
}
