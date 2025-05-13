package com.workshop.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.workshop.Entity.Vendor;
import com.workshop.Entity.VendorCabs;
import com.workshop.Entity.VendorDrivers;
import com.workshop.Repo.VendorRepository;
import com.workshop.Service.CloudinaryService;
import com.workshop.Service.EmailService;
import com.workshop.Service.VendorDriverService;


@RestController
public class VendorDriverController {

	@Autowired
	private VendorDriverService vendorDriverService;
	
	@Autowired
	private VendorRepository vendorRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CloudinaryService cloudinaryService;

	
private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/vendorDriver/";
    
    private void ensureUploadDirExists() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists() && uploadDir.mkdirs()) {
            System.out.println("✅ Upload directory created: " + UPLOAD_DIR);
        }
    }

    // ✅ Utility: Save File
    private String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null; // ✅ Return null if no file is uploaded
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ File saved: " + filePath);
        } catch (IOException e) {
            System.err.println("❌ File saving failed: " + e.getMessage());
            throw e;
        }
        return fileName;
    }
    
    @PostMapping("/addVendorDriver/{id}")
    public ResponseEntity<?> addVendor(
        @RequestParam("driverName") String driverName,
        @RequestParam("contactNo") String contactNo,
        @RequestParam("altContactNo") String altContactNo,
        @RequestParam("address") String address,
        @RequestParam("dLNo") String dLNo,
        @RequestParam("pvcNo") String pvcNo,
        @RequestParam("emailId") String emailId,
        @RequestParam("driverOtherDetails") String driverOtherDetails,


       
        @RequestPart(value = "driverImage", required = false) MultipartFile driverImage,
        @RequestPart(value = "driverSelfie", required = false) MultipartFile driverSelfie,
        @RequestPart(value = "dLnoImage", required = false) MultipartFile dLnoImage,
        @RequestPart(value = "pvcImage", required = false) MultipartFile pvcImage,
        @RequestPart(value = "driverDoc1Image", required = false) MultipartFile driverDoc1Image,
        @RequestPart(value = "driverDoc2Image", required = false) MultipartFile driverDoc2Image,
        @RequestPart(value = "driverDoc3Image", required = false) MultipartFile driverDoc3Image,
        
        @PathVariable Long id

       


    ) {
        try {
            // ✅ Ensure Upload Directory Exists
            // ensureUploadDirExists();

            String driverImageUrl = cloudinaryService.upload(driverImage);
            String driverSelfieUrl = cloudinaryService.upload(driverSelfie);
            String dLnoImageUrl = cloudinaryService.upload(dLnoImage);
            String pvcImageUrl = cloudinaryService.upload(pvcImage);
            String driverDoc1ImageUrl = cloudinaryService.upload(driverDoc1Image);
            String driverDoc2ImageUrl = cloudinaryService.upload(driverDoc2Image);
            String driverDoc3ImageUrl = cloudinaryService.upload(driverDoc3Image);

            
            Vendor vendor = vendorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Vendor not found with id " + id));
            

            

            // ✅ Create Vendor Object
          VendorDrivers v = new VendorDrivers();
          v.setDriverName(driverName);
          v.setContactNo(contactNo);
          v.setAltContactNo(altContactNo);
          v.setAddress(address);
          v.setDriverImage(driverImageUrl);
          v.setDriverSelfie(driverSelfieUrl);
          v.setdLNo(dLNo);
          v.setPvcNo(pvcNo);
          v.setdLnoImage(dLnoImageUrl);
          v.setEmailId(emailId);
          v.setPvcImage(pvcImageUrl);
          v.setDriverDoc1Image(driverDoc1ImageUrl);
          v.setDriverDoc2Image(driverDoc2ImageUrl);
          v.setDriverDoc3Image(driverDoc3ImageUrl);
          v.setVendor(vendor);
        //   String password = Math.random(Math.floor(100));
        //    v.setPassword("vendorDriver@123");

          String subject = "Driver Registration Successfully" + v.getDriverName();
          String message = "<!DOCTYPE html>"
          + "<html lang='en'>"
          + "<head>"
          + "<meta charset='UTF-8'>"
          + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
          + "<title>Welcome WTL Tourism Pvt Ltd</title>"
          + "</head>"
          + "<body style='font-family: Arial, sans-serif; background-color: #f7f7f7; margin: 0; padding: 0;'>"
          + "<div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); overflow: hidden;'>"
          + "<div style='background-color: #007BFF; color: #ffffff; padding: 20px; text-align: center;'>"
          + "<h1 style='margin: 0; font-size: 24px; font-weight: bold;'>Booking Confirmation</h1>"
          + "</div>"
          + "<div style='padding: 20px;'>"
          + "<h3 style='color: #007BFF; font-size: 20px; margin-bottom: 20px;'>Hello " + v.getDriverName() + ",</h3>"
          + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-bottom: 20px;'>Your registration has been confirmed. Below are the details of your personal information, Please kindly keep on your mind do not share this crediential to anyone:</p>"
          + "<div style='margin-top: 20px;'>"
          + "<ul style='list-style-type: none; padding: 0;'>"
          + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Contact No :</strong> " + v.getContactNo() + "</li>"
          + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Alternate Contact No :</strong> " + v.getAltContactNo() + "</li>"
          + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Address :</strong> " + v.getAddress() + "</li>"
          + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver License No :</strong> " + v.getdLNo() + "</li>"
          + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>PVC No :</strong> " + v.getPvcNo() + "</li>"
          + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Email Id:</strong> " + v.getEmailId() + "</li>"
          + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Your Password For Driver App:</strong>" + "vendorDriver@123" + "</li>"
        //   + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Cab Name:</strong> " + updatedBooking.getVendorCab().getCarName() + "</li>"
        //   + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Vehicle No:</strong> " + updatedBooking.getVendorCab().getVehicleNo() + "</li>"
        //   + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver Name:</strong> " + updatedBooking.getVendorDriver().getDriverName() + "</li>"
        //   + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver Contact:</strong> " + updatedBooking.getVendorDriver().getContactNo() + "</li>"
          + "</ul>"
          + "</div>"
          + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-top: 20px;'>If you have any query related to your registration, Please kindly contact to our WTL Tourism Pvt. Ltd</p>"
          + "</div>"
          + "<div style='text-align: center; padding: 20px; background-color: #f1f1f1; color: #777777; font-size: 14px;'>"
          + "<p style='margin: 0;'>If you have any questions, feel free to contact us at <a href='wtltourism@gmail.com' style='color: #007BFF; text-decoration: none;'>support@example.com</a>.</p>"
          + "<img src='https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExcjc1OGk0ZGVqNHFseDRrM3FvOW0xYnVyenJkcmQ2OXNsODE0djUzZyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/3oKIPhUfA1h2U2Koko/giphy.gif' alt='Namaskar' style='width: 100px; height: auto; margin-top: 10px;'>"
          + "</div>"
          + "</div>"
          + "</body>"
          + "</html>";
          emailService.sendEmail(message, subject, v.getEmailId());

          
           

            return ResponseEntity.ok(this.vendorDriverService.addVendorsDriver(v));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error while saving vendor: " + e.getMessage());
        }
    }
    
 // ✅ Utility: Get File Path
    private String getFilePath(String fileName) {
        return fileName != null ? "/uploads/" + fileName : null;
    }
    
   @GetMapping("/vendorDriver/{id}")
   public Optional<VendorDrivers> getVendorDriverById(@PathVariable int id) {
	   return this.vendorDriverService.getVendorDriversById(id);
   }
   
   @GetMapping("/getAllVendorDriver")
   public List<VendorDrivers> getAllVendorDriver(){
	   return this.vendorDriverService.getAllDrivers();
   }
   
   @GetMapping("/{vendorId}/drivers")
   public ResponseEntity<List<VendorDrivers>> getAllVendorCabByVendorId(@PathVariable Long vendorId) {
       try {
           // Call service method to get all VendorCabs for the given vendorId
           List<VendorDrivers> vendorCabs = vendorDriverService.getOrder(vendorId);

           // Return the list of VendorCabs with a 200 OK response
           return ResponseEntity.ok(vendorCabs);
       } catch (RuntimeException e) {
           // Return a 404 Not Found response if vendor is not found
           return ResponseEntity.notFound().build();
       }
   }
}
