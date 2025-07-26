package com.workshop.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.workshop.Entity.CabAdmin;
import com.workshop.Entity.DriveAdmin;
import com.workshop.Entity.OutSourceCarCab;
import com.workshop.Service.DriverAdminService;

@RestController
@RequestMapping("/driverAdmin")
public class DriverAdminController {

	
	@Autowired
	private DriverAdminService driverAdminService;
	
	 @PostMapping("/save")
	    public ResponseEntity<DriveAdmin> saveDriveAdmin(
	            @RequestParam(value="DriverName", required = false) String DriverName,
	            @RequestParam(value="contactNo", required = false) String contactNo,
	            @RequestParam(value="AltMobNum", required = false) String AltMobNum,
	            @RequestParam(value="Adress", required = false) String Adress,
	            @RequestParam(value="emailId", required = false) String emailId,
	            @RequestParam(value="status", required = false) String status,
	            @RequestParam(value="otherDetails", required = false) String otherDetails,
	            @RequestParam(value="aadhaNo", required = false) String aadhaNo,
	            @RequestParam(value="drLicenseNo", required = false) String drLicenseNo,
	            @RequestParam(value="pvcNo2", required = false) String pvcNo2,

	            


	            
	            @RequestParam(value = "DriverImgSelfie", required = false) MultipartFile DriverImgSelfie,
	            @RequestParam(value="Aadhar", required = false) MultipartFile Aadhar,
	            @RequestParam(value="DrLicenceNum", required = false) MultipartFile DrLicenceNum,
	            @RequestParam(value="PvcNo", required = false) MultipartFile PvcNo
	            
	            
	            
	            ) {

	        DriveAdmin driverAdmin = new DriveAdmin();
	        driverAdmin.setDriverName(DriverName);
	        driverAdmin.setcontactNo(contactNo);
	        driverAdmin.setAltMobNum(AltMobNum);
	        driverAdmin.setAdress(Adress);
	        driverAdmin.setEmailId(emailId);
	        driverAdmin.setOtherDetails(otherDetails);
			driverAdmin.setAadhaNo(aadhaNo);
			driverAdmin.setDrLicenseNo(drLicenseNo);
			driverAdmin.setPvcNo2(pvcNo2);
	        driverAdmin.setStatus(status);

	        try {
	            // Prepare an array of images to pass to the service
	            MultipartFile[] images = new MultipartFile[] {
	            		DriverImgSelfie, Aadhar, DrLicenceNum, 
	            		PvcNo
	            };

	            DriveAdmin savedDriverAdmin = driverAdminService.saveDriverAdmin(driverAdmin, DriverImgSelfie, Aadhar, DrLicenceNum, PvcNo);
	            return ResponseEntity.ok(savedDriverAdmin);
	        } catch (IOException e) {
	            return ResponseEntity.status(500).body(null);
	        }
	    }

	    // Get all vehicles
	    @GetMapping("/all")
	    public ResponseEntity<List<DriveAdmin>> getAllDriverAdmin() {
	        List<DriveAdmin> driverAdmin = driverAdminService.getDriverAdmin();
	        return ResponseEntity.ok(driverAdmin);
	    }

	    // Get vehicle by ID
	    @GetMapping("/{id}")
	    public ResponseEntity<DriveAdmin> getVehicleById(@PathVariable int id) {
	        return driverAdminService.getDriverAdminById(id)
	                .map(ResponseEntity::ok)
	                .orElse(ResponseEntity.notFound().build());
	    }

//	    // Delete vehicle by ID
//	    @DeleteMapping("/{id}")
//	    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
//	        vehicleService.deleteVehicle(id);
//	        return ResponseEntity.noContent().build();
//	    }
@PutMapping("/{id}/status")
	public ResponseEntity<DriveAdmin> changeStatus( @PathVariable int id, @RequestBody Map<String, String> requestBody) {

	   
	    String status = requestBody.get("status");

	    try {
	    	DriveAdmin updatedOrder = driverAdminService.udpateStatus(id, status);
	        return ResponseEntity.ok(updatedOrder);
	    } catch (NoSuchElementException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}



	@GetMapping("/driver/{status}")
    public ResponseEntity<?> getCabsByStatus(@PathVariable String status) {
        List<DriveAdmin> cabs = driverAdminService.getCabByStatus(status);
        if (cabs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No cabs found with status: " + status);
        }
        return ResponseEntity.ok(cabs); // Return the list of cabs with HTTP 200 OK
    }



}
