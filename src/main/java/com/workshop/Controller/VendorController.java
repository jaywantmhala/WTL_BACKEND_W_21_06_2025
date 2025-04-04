package com.workshop.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.workshop.DTO.VendorLoginRequest;
import com.workshop.DTO.VendorLoginResponse;
import com.workshop.Entity.Booking;
import com.workshop.Entity.Vendor;
import com.workshop.Service.EmailService;
import com.workshop.Service.VendorService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/vendors")
// @CrossOrigin(origins = "http://localhost:3000") // ✅ Allow frontend access
public class VendorController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

    @Autowired
    private VendorService service;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;

    // ✅ Add Vendor with File Upload
    @PostMapping("/add")
    public ResponseEntity<?> addVendor(
            @RequestParam("vendorCompanyName") String vendorCompanyName,
            @RequestParam("contactNo") String contactNo,
            @RequestParam("alternateMobileNo") String alternateMobileNo,
            @RequestParam("city") String city,
            @RequestParam("vendorEmail") String vendorEmail,
            @RequestParam("bankName") String bankName,
            @RequestParam("bankAccountNo") String bankAccountNo,
            @RequestParam("ifscCode") String ifscCode,
            @RequestParam("aadharNo") String aadharNo,
            @RequestParam("panNo") String panNo,
            @RequestParam("udyogAadharNo") String udyogAadharNo,
            @RequestPart(value = "govtApprovalCertificate", required = false) MultipartFile govtApprovalCertificate,
            @RequestPart(value = "vendorDocs", required = false) MultipartFile vendorDocs,
            @RequestPart(value = "vendorImage", required = false) MultipartFile vendorImage,
            @RequestPart(value = "aadharPhoto", required = false) MultipartFile aadharPhoto,
            @RequestPart(value = "panPhoto", required = false) MultipartFile panPhoto,
            @RequestParam(value = "vendorOtherDetails", required = false) String vendorOtherDetails) {
        try {
            // ✅ Ensure Upload Directory Exists
            ensureUploadDirExists();

            // ✅ Save Files
            String govtApprovalCertName = saveFile(govtApprovalCertificate);
            String vendorDocsName = saveFile(vendorDocs);
            String vendorImageName = saveFile(vendorImage);
            String aadharPhotoName = saveFile(aadharPhoto);
            String panPhotoName = saveFile(panPhoto);

            // ✅ Create Vendor Object
            Vendor vendor = new Vendor();
            vendor.setVendorCompanyName(vendorCompanyName);
            vendor.setContactNo(contactNo);
            vendor.setAlternateMobileNo(alternateMobileNo);
            vendor.setCity(city);
            vendor.setVendorEmail(vendorEmail);
            vendor.setBankName(bankName);
            vendor.setBankAccountNo(bankAccountNo);
            vendor.setIfscCode(ifscCode);
            vendor.setAadharNo(aadharNo);
            vendor.setPanNo(panNo);
            vendor.setUdyogAadharNo(udyogAadharNo);
            vendor.setGovtApprovalCertificate(govtApprovalCertName);
            vendor.setVendorDocs(vendorDocsName);
            vendor.setVendorImage(vendorImageName);
            vendor.setAadharPhoto(aadharPhotoName);
            vendor.setPanPhoto(panPhotoName);
            vendor.setVendorOtherDetails(vendorOtherDetails);

            return ResponseEntity.ok(service.saveVendor(vendor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving vendor: " + e.getMessage());
        }
    }

    // ✅ Get All Vendors
    @GetMapping("/allVendors")
    public ResponseEntity<List<Vendor>> getAllVendors() {
        return ResponseEntity.ok(service.getAllVendors());
    }

    // ✅ Get Vendor by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable Long id) {
        Vendor vendor = service.getVendorById(id);
        if (vendor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Vendor not found"));
        }
        return ResponseEntity.ok(generateVendorResponse(vendor));
    }

    // ✅ Delete Vendor
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVendor(@PathVariable Long id) {
        String message = service.deleteVendor(id);
        if (message.equals("Vendor deleted successfully")) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
    }

    // ✅ Get Vendor by Email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getVendorByEmail(@PathVariable String email) {
    Vendor vendor = service.getVendorByEmail(email);
    if (vendor == null) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",
    "Vendor not found"));
    }
    return ResponseEntity.ok(generateVendorResponse(vendor));
    }

    // ✅ Utility: Ensure Upload Directory Exists
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

    // ✅ Utility: Format Vendor Response
    private Map<String, Object> generateVendorResponse(Vendor vendor) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", vendor.getId());
        response.put("vendorCompanyName", vendor.getVendorCompanyName());
        response.put("contactNo", vendor.getContactNo());
        response.put("alternateMobileNo", vendor.getAlternateMobileNo());
        response.put("city", vendor.getCity());
        response.put("vendorEmail", vendor.getVendorEmail());
        response.put("bankName", vendor.getBankName());
        response.put("bankAccountNo", vendor.getBankAccountNo());
        response.put("ifscCode", vendor.getIfscCode());
        response.put("aadharNo", vendor.getAadharNo());
        response.put("panNo", vendor.getPanNo());
        response.put("udyogAadharNo", vendor.getUdyogAadharNo());
        response.put("govtApprovalCertificate", getFilePath(vendor.getGovtApprovalCertificate()));
        response.put("vendorDocs", getFilePath(vendor.getVendorDocs()));
        response.put("vendorImage", getFilePath(vendor.getVendorImage()));
        response.put("aadharPhoto", getFilePath(vendor.getAadharPhoto()));
        response.put("panPhoto", getFilePath(vendor.getPanPhoto()));
        response.put("vendorOtherDetails", vendor.getVendorOtherDetails());

        return response;
    }

    // ✅ Utility: Get File Path
    private String getFilePath(String fileName) {
        return fileName != null ? "/uploads/" + fileName : null;
    }

    @PostMapping("/vendorLogin")
    public ResponseEntity<?> vendorLogin(@RequestBody VendorLoginRequest loginRequest) {
        try {
            // Call vendorService to validate and retrieve vendor login response
            VendorLoginResponse vendorLoginResponse = service.vendorLogin(loginRequest.getEmail(),
                    loginRequest.getPassword());
            return ResponseEntity.ok(vendorLoginResponse); // Return the response as JSON
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Invalid credentials or other error.");
        }
    }

    // @GetMapping("/{vendorId}/vendorByBookings")
    // public ResponseEntity<List<Booking>> getBookingsByVendor(@PathVariable Long
    // vendorId) {
    // List<Booking> bookings = service.getBookingByVendor(vendorId);
    // if (bookings.isEmpty()) {
    // return ResponseEntity.noContent().build(); // Returns 204 if no bookings
    // found
    // }
    // return ResponseEntity.ok(bookings); // Returns 200 with the list of bookings
    // }


    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vendor> update(@RequestBody Vendor vendor, @PathVariable Long id) {
        Vendor updatedVendor = this.service.updatePassword(vendor, id);
        return ResponseEntity.ok(updatedVendor);
    }




    @PostMapping("/send-manual/{email}")
    public ResponseEntity<?> sendManualEmailWithPath(@PathVariable("email") String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
        }

        try {
            // MimeMessage mimeMessage = mailSender.createMimeMessage();
            // MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // helper.setFrom("jaywantmhala928@gmail.com"); // Use your sender email
            // helper.setTo(email);
            // helper.setSubject("Vendor Registration - Please Fill the Details");

            // HTML formatted email content
            String subject = "Vendor Registration Form - " + email;

            String emailContent = "<html><body>" +
                    "<p>Hi,</p>" +
                    "<p>Please click on the following link to fill in the vendor registration details:</p>" +
                    "<p><a href='http://192.168.231.233:3001/vendor-registration'  " +
                    "style='color: #007bff; text-decoration: none; font-weight: bold;'>Complete Vendor Registration</a></p>"
                    +
                    "<p>Thank you.</p>" +
                    "<p>Best Regards,<br>WTL Tourism Pvt. Ltd.</p>" +
                    "</body></html>";

                    boolean emailSent = emailService.sendEmail(emailContent, subject, email);

                    if (emailSent) {
                        System.out.println("Booking confirmation email sent successfully.");
                    } else {
                        System.out.println("Failed to send booking confirmation email.");
                    }

            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + ex.getMessage());
        }

}

}
