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
import com.workshop.Repo.VendorRepository;
import com.workshop.Service.CloudinaryService;
import com.workshop.Service.VendorCabService;
import com.workshop.Service.VendorService;

@RestController
public class VendorCabController {

	
	@Autowired
	private VendorCabService vendorCabService;
	
	@Autowired
	private VendorRepository vendorRepository;
	
	@Autowired
	private VendorService vendorService;

    @Autowired
    private CloudinaryService cloudinaryService;
	
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/vendorCab/";
    
    // private void ensureUploadDirExists() {
    //     File uploadDir = new File(UPLOAD_DIR);
    //     if (!uploadDir.exists() && uploadDir.mkdirs()) {
    //         System.out.println("✅ Upload directory created: " + UPLOAD_DIR);
    //     }
    // }

    // // ✅ Utility: Save File
    // private String saveFile(MultipartFile file) throws IOException {
    //     if (file == null || file.isEmpty()) {
    //         return null; // ✅ Return null if no file is uploaded
    //     }

    //     String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
    //     Path filePath = Paths.get(UPLOAD_DIR, fileName);

    //     try (var inputStream = file.getInputStream()) {
    //         Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    //         System.out.println("✅ File saved: " + filePath);
    //     } catch (IOException e) {
    //         System.err.println("❌ File saving failed: " + e.getMessage());
    //         throw e;
    //     }
    //     return fileName;
    // }
    
    @PostMapping("/addVendorCab/{id}")
    public ResponseEntity<?> addVendor(
        @RequestParam("carName") String carName,
        @RequestParam("rCNo") String rCNo,
        @RequestParam("vehicleNo") String vehicleNo,
        @RequestParam("cabOtherDetails") String cabOtherDetails,
       
        @RequestPart(value = "rCImage", required = false) MultipartFile rCImage,
        @RequestPart(value = "vehicleNoImage", required = false) MultipartFile vehicleNoImage,
        @RequestPart(value = "insuranceImage", required = false) MultipartFile insuranceImage,
        @RequestPart(value = "permitImage", required = false) MultipartFile permitImage,
        @RequestPart(value = "authorizationImage", required = false) MultipartFile authorizationImage,
        @RequestPart(value = "cabNoPlateImage", required = false) MultipartFile cabNoPlateImage,
        @RequestPart(value = "cabImage", required = false) MultipartFile cabImage,
        @RequestPart(value = "cabFrontImage", required = false) MultipartFile cabFrontImage,
        @RequestPart(value = "cabBackImage", required = false) MultipartFile cabBackImage,
        @RequestPart(value = "cabSideImage", required = false) MultipartFile cabSideImage,
        
        @PathVariable Long id  // Vendor ID passed from the URL
    ) {
        try {
            // // ✅ Ensure Upload Directory Exists
            // ensureUploadDirExists();

            String rCImageName = (rCImage != null && !rCImage.isEmpty()) 
            ? cloudinaryService.upload(rCImage) : null;
        
        String vehicleNoImageName = (vehicleNoImage != null && !vehicleNoImage.isEmpty()) 
            ? cloudinaryService.upload(vehicleNoImage) : null;
        
        String insuranceImageName = (insuranceImage != null && !insuranceImage.isEmpty()) 
            ? cloudinaryService.upload(insuranceImage) : null;
        
        String permitImageName = (permitImage != null && !permitImage.isEmpty()) 
            ? cloudinaryService.upload(permitImage) : null;
        
        String authorizationImageName = (authorizationImage != null && !authorizationImage.isEmpty()) 
            ? cloudinaryService.upload(authorizationImage) : null;
        
        String cabNoPlateImageName = (cabNoPlateImage != null && !cabNoPlateImage.isEmpty()) 
            ? cloudinaryService.upload(cabNoPlateImage) : null;
        
        String cabImageName = (cabImage != null && !cabImage.isEmpty()) 
            ? cloudinaryService.upload(cabImage) : null;
        
        String cabFrontImageName = (cabFrontImage != null && !cabFrontImage.isEmpty()) 
            ? cloudinaryService.upload(cabFrontImage) : null;
        
        String cabBackImageName = (cabBackImage != null && !cabBackImage.isEmpty()) 
            ? cloudinaryService.upload(cabBackImage) : null;
        
        String cabSideImageName = (cabSideImage != null && !cabSideImage.isEmpty()) 
            ? cloudinaryService.upload(cabSideImage) : null;
        

            // ✅ Fetch Vendor by ID
            Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id " + id));

            // ✅ Create VendorCabs Object and Set Vendor
            VendorCabs v = new VendorCabs();
            v.setCarName(carName);
            v.setrCNo(rCNo);
            v.setrCImage(rCImageName);
            v.setVehicleNo(vehicleNo);
            v.setVehicleNoImage(vehicleNoImageName);
            v.setInsuranceImage(insuranceImageName);
            v.setPermitImage(permitImageName);
            v.setAuthorizationImage(authorizationImageName);
            v.setCabNoPlateImage(cabNoPlateImageName);
            v.setCabImage(cabImageName);
            v.setCabFrontImage(cabFrontImageName);
            v.setCabBackImage(cabBackImageName);
            v.setCabOtherDetails(cabOtherDetails);
            v.setCabSideImage(cabSideImageName);

            // Associate VendorCabs with Vendor
            v.setVendor(vendor);

            // ✅ Save VendorCabs and Return Response
            VendorCabs savedVendorCab = vendorCabService.saveVendorCab(v);
            
            return ResponseEntity.ok(savedVendorCab);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error while saving vendor: " + e.getMessage());
        }
    }

    
 // ✅ Utility: Get File Path
    private String getFilePath(String fileName) {
        return fileName != null ? "/uploads/" + fileName : null;
    }
    
    
    @GetMapping("/vendorsCab/{vendorCabId}")
    public Optional<VendorCabs> getVendorCabById(@PathVariable int vendorCabId) {
    	return this.vendorCabService.getVendorCabsById(vendorCabId);
    }
    
    @GetMapping("/{vendorId}/cabs")
    public ResponseEntity<List<VendorCabs>> getAllVendorCabByVendorId(@PathVariable Long vendorId) {
        try {
            // Call service method to get all VendorCabs for the given vendorId
            List<VendorCabs> vendorCabs = vendorCabService.getOrder(vendorId);

            // Return the list of VendorCabs with a 200 OK response
            return ResponseEntity.ok(vendorCabs);
        } catch (RuntimeException e) {
            // Return a 404 Not Found response if vendor is not found
            return ResponseEntity.notFound().build();
        }
    }

}
