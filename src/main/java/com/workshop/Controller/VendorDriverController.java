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
import com.workshop.Service.VendorDriverService;


@RestController
public class VendorDriverController {

	@Autowired
	private VendorDriverService vendorDriverService;
	
	@Autowired
	private VendorRepository vendorRepository;
	
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
            ensureUploadDirExists();

            // ✅ Save Files
            String driverImageName = saveFile(driverImage);
            String driverSelfieName = saveFile(driverSelfie);
            String dLnoImageName = saveFile(dLnoImage);
            String pvcImageName = saveFile(pvcImage);
            String driverDoc1ImageName = saveFile(driverDoc1Image);
            String driverDoc2ImageName = saveFile(driverDoc2Image);
            String driverDoc3ImageName = saveFile(driverDoc3Image);
            
            Vendor vendor = vendorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Vendor not found with id " + id));
            

            

            // ✅ Create Vendor Object
          VendorDrivers v = new VendorDrivers();
          v.setDriverName(driverName);
          v.setContactNo(contactNo);
          v.setAltContactNo(altContactNo);
          v.setAddress(address);
          v.setDriverImage(driverImageName);
          v.setDriverSelfie(driverSelfieName);
          v.setdLNo(dLNo);
          v.setPvcNo(pvcNo);
          v.setdLnoImage(dLnoImageName);
          v.setEmailId(emailId);
          v.setPvcImage(pvcImageName);
          v.setDriverDoc1Image(driverDoc1ImageName);
          v.setDriverDoc2Image(driverDoc2ImageName);
          v.setDriverDoc3Image(driverDoc3ImageName);
          v.setVendor(vendor);
          
           

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
