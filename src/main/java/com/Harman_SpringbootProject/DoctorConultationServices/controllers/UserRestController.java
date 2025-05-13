package com.Harman_SpringbootProject.DoctorConultationServices.controllers;

import com.Harman_SpringbootProject.DoctorConultationServices.vmm.DBLoader;
import com.Harman_SpringbootProject.DoctorConultationServices.vmm.RDBMS_TO_JSON;
import jakarta.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.util.StringTokenizer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
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
        String ans= new RDBMS_TO_JSON().generateJSON("SELECT DISTINCT s.*, d.dcity, d.dstatus FROM specialities s JOIN doctor d ON s.sname = d.dspecialityname WHERE d.dcity ='"+city+"'");
        return ans;
    }
    
    @PostMapping("getDoctors")
    public String getDoctors(@RequestParam String city, @RequestParam int sid) {
        String ans= new RDBMS_TO_JSON().generateJSON("SELECT DISTINCT s.*, d.did, d.dstatus, d.dslot_amount, d.dphoto, d.dname, d.dspecialityname FROM specialities s JOIN doctor d ON s.sname = d.dspecialityname WHERE d.dcity ='"+city+"' AND s.sid = '"+sid+"'");
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

        System.out.println(date);
        System.out.println(did);
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
            System.out.println(ans.toString());
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

    
}
