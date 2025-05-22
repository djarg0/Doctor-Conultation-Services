package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import com.Harman_SpringbootProject.DoctorConultationServices.vmm.DBLoader;
import com.Harman_SpringbootProject.DoctorConultationServices.vmm.RDBMS_TO_JSON;
import com.mysql.cj.Session;
import jakarta.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DoctorRestController {

    @Autowired
    public EmailSenderService email;
    @PostMapping("/specialityList")
    public String specialityList() {
        String ans = new RDBMS_TO_JSON().generateJSON("select * from specialities");
        return ans;
    }

    @PostMapping("/addDoctor")
    public String addDoctor(@RequestParam String name, @RequestParam String contact, @RequestParam String address, @RequestParam String email, @RequestParam String password, @RequestParam String speciality, @RequestParam String city, @RequestParam String longitude, @RequestParam String latitude, @RequestParam String startTime, @RequestParam String endTime, @RequestParam int slotAmount, @RequestParam String desc, @RequestParam String experience, @RequestParam String education, @RequestParam MultipartFile photo) {

        String projectPath = System.getProperty("user.dir");
        String internalPath = "/src/main/resources/static";
        String folderName = "/myUploads";
        String orgName = "/" + photo.getOriginalFilename();

        try {
            ResultSet rs2 = DBLoader.executeQuery("select * from specialities where sid='" + speciality + "'");
            String sname = "";
            if (rs2.next()) {
                sname = rs2.getString("sname");
            }

            try {
                ResultSet rs = DBLoader.executeQuery("select * from doctor where demail='" + email + "'");
                if (rs.next()) {
                    return "failed";
                } else {

                    FileOutputStream fos = new FileOutputStream(projectPath + internalPath + folderName + orgName);

                    byte[] b1 = photo.getBytes();

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
                    rs.updateString("daddress", address);
                    rs.updateString("dspecialityname", sname);
                    rs.insertRow();
                    return "success";
                }
            } catch (Exception ex) {
                return ex.toString();
            }
        } catch (Exception ex) {
            return ex.toString();
        }

    }

    @PostMapping("/dlogin")
    public String alogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from doctor where demail='" + email + "' and dpass='" + password + "'");
            if (rs.next()) {
                int id = rs.getInt("did");
                session.setAttribute("did", id);

                return "success";
            } else {
                return "failure";
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    @PostMapping("/addPhoto")
    public String addPhoto(@RequestParam MultipartFile photo, @RequestParam String photoDesc, HttpSession session) {
        String projectPath = System.getProperty("user.dir");
        String internalPath = "/src/main/resources/static";
        String folderName = "/myUploads";
        String orgName = "/" + photo.getOriginalFilename();

        try {
            ResultSet rs = DBLoader.executeQuery("select * from photos");
            if (rs.next()) {
                FileOutputStream fos = new FileOutputStream(projectPath + internalPath + folderName + orgName);

                byte[] b1 = photo.getBytes();

                fos.write(b1);
                fos.close();
                Integer id = (Integer) session.getAttribute("did");

                rs.moveToInsertRow();
                rs.updateString("pdesc", photoDesc);
                rs.updateString("photo", orgName);
                rs.updateInt("did", id);
                rs.insertRow();
                return "success";
            } else {
                return "failed";
            }
        } catch (Exception ex) {
            return "exception";
        }
    }

    @GetMapping("/showPhotos")
    public String showPhotos(HttpSession session) {
        Integer id = (Integer) session.getAttribute("did");
        String ans = new RDBMS_TO_JSON().generateJSON("select * from photos where did= '" + id + "'");
        return ans;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam int id) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from photos where pid= '" + id + "'");
            if (rs.next()) {
                rs.deleteRow();
                return "success";
            } else {
                return "Exception";
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    @GetMapping("/showDetails")
    public String showDetails(HttpSession session) {
        Integer id = (Integer) session.getAttribute("did");
        String ans = new RDBMS_TO_JSON().generateJSON("select * from doctor where did= '" + id + "'");
        return ans;
    }

    @PostMapping("/editDetails")
    public String editDetails(@RequestParam String name, @RequestParam String contact, @RequestParam String city, @RequestParam String longitude, @RequestParam String latitude, @RequestParam String startTime, @RequestParam String endTime, @RequestParam int slotAmount, @RequestParam String desc, @RequestParam String experience, @RequestParam String education, HttpSession session) {

        Integer id = (Integer) session.getAttribute("did");

        try {
            ResultSet rs = DBLoader.executeQuery("select * from doctor where did='" + id + "'");
            if (rs.next()) {

                rs.moveToCurrentRow();
                rs.updateString("dname", name);
                rs.updateString("dcity", city);
                rs.updateString("dlatitude", latitude);
                rs.updateString("dlongitude", longitude);
                rs.updateString("dstart_time", startTime);
                rs.updateString("dend_time", endTime);
                rs.updateInt("dslot_amount", slotAmount);
                rs.updateString("dcontact", contact);
                rs.updateString("ddesc", desc);
                rs.updateString("dexperience", experience);
                rs.updateString("deducation", education);
                rs.updateRow();
                return "success";
            } else {
                return "failed";
            }
        } catch (Exception ex) {
            return ex.toString();
        }

    }
    
    
    @PostMapping("/showBookings")
    public String showBookings2(HttpSession session) {
        
        Integer id = (Integer) session.getAttribute("did");
        try {
            ResultSet rs= DBLoader.executeQuery("SELECT  * FROM booking JOIN booking_details ON booking.booking_id = booking_details.booking_id JOIN user ON booking.uid = user.uid where booking.did="+id+";");

            String ans = "";

            StringBuilder json = new StringBuilder();
            json.append("[");

            int bid=-1;
            boolean first= true;
            boolean last=false;
            while (rs.next()) {
                if (!first || last) {
                    json.append(",");
                } else {
                    first = false;
                }
                int booking_id= rs.getInt("booking_id");
                String uname = rs.getString("uname");
                String ucontact= rs.getString("ucontact");
                String date = rs.getString("date");
                String total_price = rs.getString("total_price");
                String ugender = rs.getString("ugender");
                String status = rs.getString("status");
                String payment_type = rs.getString("payment_type");
                String uemail= rs.getString("uemail");

                if(bid!=booking_id) {
                    
                    bid=booking_id;

                    json.append("{");
                    json.append("\"uname\":\"").append(uname).append("\",");
                    json.append("\"ucontact\":\"").append(ucontact).append("\",");
                    json.append("\"date\":\"").append(date).append("\",");
                    json.append("\"total_price\":\"").append(total_price).append("\",");
                    json.append("\"ugender\":\"").append(ugender).append("\",");
                    json.append("\"status\":\"").append(status).append("\",");
                    json.append("\"booking_id\":").append(booking_id).append(",");
                    StringBuilder json2 = new StringBuilder();
                    try{
                        
                        json2.append("[");
                        ResultSet rs2= DBLoader.executeQuery("select * from booking_details where booking_id="+booking_id);
                        boolean first1= true;
                        while(rs2.next()) {
                            
                            if (!first1) {
                                json2.append(",");
                            } else {
                                first1 = false;
                            }
                            String slot= rs2.getString("start_slot")+"-"+rs2.getString("end_slot");
                            
                            json2.append("{");
                            json2.append("\"slot\":\"").append(slot);
                            json2.append("\"}");
                    
                            //if(rs2.next()) json2.append(", ");
                        }
                        json2.append("]");
                    }
                    
                    catch(Exception ex) {
                        return ex.toString();
                    }
                    String slots= json2.toString();
                    
                    json.append("\"slots\":").append(slots).append(",");
                    json.append("\"uemail\":\"").append(uemail).append("\",");
                    json.append("\"payment_type\":\"").append(payment_type);
                    json.append("\"}");
                    
                    //if(rs.next()) json.append(", ");
                }
                if(!rs.next()) last=true;
            }

            json.append("]");

            ans= json.toString();
            return ans;
        }
        catch(Exception ex) {
            return ex.toString();
        }
    }
    
    @GetMapping("/showSlots")
    public String showSlots(@RequestParam int bid) {
        String ans= new RDBMS_TO_JSON().generateJSON("select * from booking_details where booking_id="+bid);
        return ans;
    }
    
    @PostMapping("/acceptBooking")
    public String accept(@RequestParam int id) {
        try {
            ResultSet rs= DBLoader.executeQuery("select * from booking where booking_id= '"+ id +"'");
            if(rs.next()) {
                rs.updateString("status", "approved");
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
    
    @PostMapping("/doctorChangePassword")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String confirmPassword,HttpSession session) {
        Integer id= (Integer) session.getAttribute("did");
        try {
            ResultSet rs = DBLoader.executeQuery("select * from doctor where did=" + id + " and dpass='" + oldPassword + "'");
            if (rs.next()) {
                rs.moveToCurrentRow();
                rs.updateString("dpass", newPassword);
                rs.updateRow();
                session.removeAttribute("did");
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }
    
    @GetMapping("/dforgot")
    public String dforgot(@RequestParam String email, @RequestParam String otp) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from doctor where demail='" + email + "'");
            if (rs.next()) {
                String body = "Your otp for login page is =" + otp;
                String subject = "Login Authntication";
                this.email.sendSimpleEmail(email, body, subject);
                return "success";
            } else {
                return "fail";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    @GetMapping("/dotpverify")
    public String dotpverify(@RequestParam String email) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from doctor where demail='" + email + "'");
            if (rs.next()) {
                rs.moveToCurrentRow();
                String pass = rs.getString("dpass");
                String subject = "Your Account Password - JC Pawfect";
                String body = "Dear Doctor,\n\n"
                        + "As per your request, here is your account password:\n\n"
                        + "Password: " + pass + "\n\n"
                        + "Please do not share this password with anyone.\n"
                        + "We recommend changing your password after login for better security.\n\n"
                        + "Regards,\n"
                        + "Team Doc";
                this.email.sendSimpleEmail(email, body, subject);
                return "success";
            } else {
                return "fail";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }
    
    @GetMapping("/dsendemail")
    public String dSendemail(@RequestParam String body, @RequestParam String subject, @RequestParam String id)
    {
        String email="";
        try {
            ResultSet rs= DBLoader.executeQuery("select * from doctor where did="+id);
            if(rs.next()) {
                email= rs.getString("demail");
            }
        }
        catch(Exception ex) {
            return ex.toString();
        }
        this.email.sendSimpleEmail(email, body, subject);
        return "success";
    }
}
