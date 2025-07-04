package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import com.Harman_SpringbootProject.DoctorConultationServices.vmm.DBLoader;
import com.Harman_SpringbootProject.DoctorConultationServices.vmm.RDBMS_TO_JSON;
import jakarta.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserRestController {
    @Autowired
    public EmailSenderService email;

    
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
        String ans= new RDBMS_TO_JSON().generateJSON("SELECT DISTINCT s.*, d.dcity, d.dstatus FROM specialities s JOIN doctor d ON s.sname = d.dspecialityname WHERE d.dcity ='"+city+"'");
        return ans;
    }
    
    
    @PostMapping("getDoctors")
    public String getDoctors(@RequestParam String city, @RequestParam int sid) {
        String ans= new RDBMS_TO_JSON().generateJSON("SELECT DISTINCT s.*, d.did, d.dstatus, d.dslot_amount, d.dphoto, d.dname, d.dspecialityname FROM specialities s JOIN doctor d ON s.sname = d.dspecialityname WHERE d.dcity ='"+city+"' AND s.sid = '"+sid+"'");
        return ans;
    }
    @PostMapping("getDoctors1")
    public String getDoctors1() {
        String ans= new RDBMS_TO_JSON().generateJSON("SELECT DISTINCT s.*, d.did, d.dstatus, d.dslot_amount, d.dphoto, d.dname, d.dspecialityname FROM specialities s JOIN doctor d ON s.sname = d.dspecialityname");
        return ans;
    }
    
    @PostMapping("doctorDetails")
    public String doctorDetails(@RequestParam String did) {
        String ans= new RDBMS_TO_JSON().generateJSON("select * from doctor where did= '"+did+"'");
        return ans;
    }
    
    @PostMapping("showPhotos")
    public String showPhotos(@RequestParam String did) {
        String ans= new RDBMS_TO_JSON().generateJSON("select * from photos where did= '"+did+"'");
        return ans;
    }
    
    @GetMapping("/view_slots")
    String view_slots(@RequestParam String did, @RequestParam String date) {

        
        try {
            ResultSet rs = DBLoader.executeQuery("select * from doctor where did='" + did + "'");

            String start;
            String end;
            String slot;
            if (rs.next()) {
                start = rs.getString("dstart_time");
                end = rs.getString("dend_time");
                slot = rs.getString("dslot_amount");

            } else {
                String err = "failed";
                return err;
            }
            int Start = Integer.parseInt(start);
            int End = Integer.parseInt(end);
            int Slot = Integer.parseInt(slot);
            JSONObject ans = new JSONObject();

            //Define JSONArray
            JSONArray arr = new JSONArray();
            for (int i = Start; i <= End; i++) {
                JSONObject row = new JSONObject();
                row.put("start_slot", Start);
                row.put("end_slot", ++Start);
                row.put("slot_amount", slot);

                ResultSet rs2 = DBLoader.executeQuery("select * from booking_details where start_slot ='" + i + "' and booking_id in (select booking_id from booking where date=\'" + date + "\' and did =\'" + did + "\' ) ");
                if (rs2.next()) {
                    row.put("status", "Booked");
                } else {
                    row.put("status", "Available");
                }

                arr.add(row);
            }
            ans.put("ans", arr);
            return (ans.toJSONString());

        } catch (Exception e) {
            return e.toString();
        }

    }
    
    
    
    @GetMapping("/pay")
    public String payment(@RequestParam String date,
            @RequestParam String did,
            @RequestParam String amount,
            @RequestParam String slots,
            HttpSession session,
            @RequestParam String type,
            @RequestParam String status) {
        String ans = "";
        Integer uid = (Integer) session.getAttribute("uid");

        try {
            // 1. Insert into booking table
            ResultSet rs = DBLoader.executeQuery("SELECT * FROM booking");
            rs.moveToInsertRow();
            rs.updateString("date", date);
            rs.updateInt("did", Integer.parseInt(did));
            rs.updateInt("uid", uid);
            rs.updateString("total_price", amount);
            rs.updateString("payment_type", type);
            rs.updateString("status", status);
            rs.insertRow();

            // 2. Get inserted booking_id
            int booking_id = 0;
            ResultSet rs2 = DBLoader.executeQuery("SELECT MAX(booking_id) AS maxid FROM booking");
            if (rs2.next()) {
                booking_id = rs2.getInt("maxid");
            }

            // 3. Insert slots into booking_detail table
            StringTokenizer st = new StringTokenizer(slots, ",");
            while (st.hasMoreTokens()) {
                int start_slot = Integer.parseInt(st.nextToken());
                int end_slot = start_slot + 1;

                ResultSet rs3 = DBLoader.executeQuery("SELECT * FROM booking_details");
                rs3.moveToInsertRow();
                rs3.updateInt("booking_id", booking_id); // fk to booking_id
                rs3.updateString("start_slot", String.valueOf(start_slot));
                rs3.updateString("end_slot", String.valueOf(end_slot));
                rs3.insertRow();
            }

            ans = "success";
            return ans;
        } catch (Exception ex) {
            return ex.toString();
        }
    }
    
    @GetMapping("/showUserBookings")
    public String showUserBookings(HttpSession session) {
        Integer id = (Integer) session.getAttribute("uid");
        String ans= new RDBMS_TO_JSON().generateJSON("SELECT  booking.*, doctor.dname, doctor.dspecialityname, doctor.dcontact,user.uid, user.uname FROM booking JOIN booking_details ON booking.booking_id = booking_details.booking_id JOIN doctor ON booking.did = doctor.did JOIN user ON booking.uid = user.uid where booking.uid="+id+";");
       return ans;
    }
    
    @PostMapping("/changePassword")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String confirmPassword,HttpSession session) {
        Integer id= (Integer) session.getAttribute("uid");
        try {
            ResultSet rs = DBLoader.executeQuery("select * from user where uid=" + id + " and upass='" + oldPassword + "'");
            if (rs.next()) {
                rs.moveToCurrentRow();
                rs.updateString("upass", newPassword);
                rs.updateRow();
                session.removeAttribute("uid");
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }
    
    @GetMapping("/userAddReview")
    public String userAddReview(@RequestParam int did, @RequestParam int rating, @RequestParam String comment, HttpSession session) {
        Integer uid = (Integer) session.getAttribute("uid");
        System.out.println(uid);
//        System.out.println(rating);
        String ans = "";
        String email="";
        try {
            ResultSet rs2= DBLoader.executeQuery("select * from user where uid="+uid);
            if(rs2.next())  email= rs2.getString("uemail");
        }
        catch(Exception ex) {
        }
        try {
            ResultSet rs = DBLoader.executeQuery("Select * from review");

            rs.moveToInsertRow();
            rs.updateInt("did", did);
            rs.updateInt("uid", uid);
            rs.updateString("comment", comment);
            rs.updateInt("rating", rating);
            rs.updateString("uemail", email);
            rs.insertRow();
            ans = "success";

        } catch (Exception e) {
            ans = e.toString();
        }

        return ans;
    }


    @GetMapping("/userShowAverageRatings")
    public String userShowAverageRatings(@RequestParam int did) {

        // Assuming RDBMS_TO_JSON is available as a service or component
        String ans = new RDBMS_TO_JSON().generateJSON("select avg(rating) as r1 from review where did='" + did + "' ");
        System.out.println(ans);
        return ans;

    }

    @GetMapping("/userShowRatings")
    public String userShowRatings(@RequestParam int did) {

        // Assuming RDBMS_TO_JSON is available as a service or component
        String ans = new RDBMS_TO_JSON().generateJSON("select * from review where did=" + did + " ");
        System.out.println(ans);
        return ans;

    }
    
    @GetMapping("/usendemail")
    public String sendemail(@RequestParam String body, @RequestParam String subject, @RequestParam String email)
    {
        //Integer id= (Integer) session.getAttribute("uid");
//        try {
//            ResultSet rs= DBLoader.executeQuery("select * from user where uid="+id);
//            if(rs.next()) {
//                email= rs.getString("uemail");
//            }
//        }
//        catch(Exception ex) {
//            return ex.toString();
//        }
        this.email.sendSimpleEmail(email, body, subject);
        return "success";
    }
    
    @GetMapping("sessionsendemail")
    public String userSendemail(@RequestParam String body, @RequestParam String subject, @RequestParam String sesId, HttpSession session)
    {
        String email="";
        if(sesId.equals("email")) {
            email= (String) session.getAttribute("email");
            System.out.println(email);
        }
        else if(sesId.equals( "did")) {
            Integer id= (Integer) session.getAttribute("did");
            try {
                ResultSet rs= DBLoader.executeQuery("select * from doctor where did="+id);
                if(rs.next()) {
                    email= rs.getString("demail");
                }
            }
            catch(Exception ex) {
                return ex.toString();
            }
        }
        else if(sesId.equals("uid")) {
            Integer id= (Integer) session.getAttribute("uid");
            try {
                ResultSet rs= DBLoader.executeQuery("select * from user where uid="+id);
                if(rs.next()) {
                    email= rs.getString("uemail");
                }
            }
            catch(Exception ex) {
                return ex.toString();
            }
        }
        this.email.sendSimpleEmail(email, body, subject);
        return "success";
    }
    
    @GetMapping("/forgot")
    public String forgot(@RequestParam String email, @RequestParam String otp) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from user where uemail='" + email + "'");
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

    @GetMapping("/otpverify")
    public String otpverify(@RequestParam String email) {
        try {
            ResultSet rs = DBLoader.executeQuery("select * from user where uemail='" + email + "'");
            if (rs.next()) {
                rs.moveToCurrentRow();
                String pass = rs.getString("upass");
                String subject = "Your Account Password - JC Pawfect";
                String body = "Dear User,\n\n"
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
}
