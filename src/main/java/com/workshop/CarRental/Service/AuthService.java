package com.workshop.CarRental.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.workshop.CarRental.DTO.CarRentalLoginRequest;
import com.workshop.CarRental.DTO.CarRentalLoginResponse;
import com.workshop.CarRental.DTO.DriverLoginRequest;
import com.workshop.CarRental.DTO.DriverLoginResponse;
import com.workshop.CarRental.DTO.UnifiedLoginRequest;
import com.workshop.CarRental.DTO.UnifiedLoginResponse;
import com.workshop.CarRental.Entity.CarRentalUser;
import com.workshop.CarRental.Repository.CarRentalRepository;
import com.workshop.DTO.LoginRequest;
import com.workshop.Entity.DriveAdmin;
import com.workshop.Entity.User;
import com.workshop.Entity.VendorDrivers;
import com.workshop.Repo.DriverAdminRepo;
import com.workshop.Repo.VendorDriverRepo;

@Service
public class AuthService {
    

    @Autowired
    private CarRentalRepository carRentalRepository;

    @Autowired
    private VendorDriverRepo vendorDriverRepo;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DriverAdminRepo driverAdminRepo;


    // public CarRentalLoginResponse authenticateUser(CarRentalLoginRequest loginRequest) {
    //     try {
    //         Optional<CarRentalUser> optionalUser = carRentalRepository.findByPhone(loginRequest.getMobile());

    //         if (optionalUser.isEmpty()) {
    //             return new CarRentalLoginResponse("User not found", null, null, 0);
    //         }

    //         CarRentalUser user = optionalUser.get();

    //         if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
    //             return new CarRentalLoginResponse("Invalid Password", null, null, 0);
    //         }

    //         return new CarRentalLoginResponse("Login Successful", user.getUserName(), user.getRole(), user.getId());

    //     } catch (Exception e) {
    //         return new CarRentalLoginResponse("Login Failed", null, null, 0);
    //     }
    // }

    public CarRentalUser login(String mobile, String password) {
		CarRentalUser user = carRentalRepository.findByPhone(mobile).get();
		if (user != null && passwordEncoder.matches(password, user.getPassword())) {
			return user;
		}
		return null;
	}


    public DriverLoginResponse authenticateDriver(DriverLoginRequest loginRequest) {
        try {
            // Find driver by contact number
            VendorDrivers driver = vendorDriverRepo.findByContactNo(loginRequest.getContactNo());
            
            // Check if driver exists
            if (driver == null) {
                return new DriverLoginResponse(false, "Driver not found with this contact number");
            }
            
            // Verify password (plain text comparison - INSECURE)
            if (!loginRequest.getPassword().equals(driver.getPassword())) {
                return new DriverLoginResponse(false, "Invalid password");
            }
            
            // Successful login
            return new DriverLoginResponse(true, "Login successful", 
                                        driver.getVendorDriverId(), 
                                        driver.getDriverName(),
                                        driver.getRole());
            
        } catch (Exception e) {
            return new DriverLoginResponse(false, "Login failed: " + e.getMessage());
        }
    }

public UnifiedLoginResponse combineLogin(UnifiedLoginRequest loginRequest) {
    String mobile = loginRequest.getMobile();
    String password = loginRequest.getPassword();
    UnifiedLoginResponse response = new UnifiedLoginResponse();
        
    try {
        Optional<CarRentalUser> userOpt = carRentalRepository.findByPhone(mobile);
        if (userOpt.isPresent()) {
            CarRentalUser user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                response.setStatus("success");
                response.setRole(user.getRole());
                response.setUserId(user.getId());
                response.setUsername(user.getUserName());
                return response;
            }
            response.setStatus("invalid_password");
            response.setMessage("Wrong password for user account");
            return response;
        }
            
        VendorDrivers driver = vendorDriverRepo.findByContactNo(mobile);
        if (driver != null) {
            String licenseNo = driver.getdLNo();
            String last6 = driver.getdLNo().substring(driver.getdLNo().length() - 6);
                
            if (password.equals(last6)) {
                response.setStatus("success");
                response.setRole(driver.getRole());
                response.setUserId(driver.getVendorDriverId());
                response.setUsername(driver.getDriverName());
                return response;
            }
                
            response.setStatus("invalid_password");
            response.setMessage("Wrong password for driver account");
            return response;
        }
        
        DriveAdmin driverAdmin = driverAdminRepo.findByContactNo(mobile);
        if (driverAdmin != null) {
            
            String licenseNo = driverAdmin.getDrLicenseNo(); 
            String last6 = licenseNo.substring(licenseNo.length() - 6);
                
            if (password.equals(last6)) {
                response.setStatus("success");
                response.setRole(driverAdmin.getRole());
                response.setUserId(driverAdmin.getId()); 
                response.setUsername(driverAdmin.getDriverName()); 
                return response;
            }
                
            response.setStatus("invalid_password");
            response.setMessage("Wrong password for admin account");
            return response;
        }
            
        response.setStatus("not_found");
        response.setMessage("No account found with this mobile number");
        return response;
            
    } catch (Exception e) {
        response.setStatus("error");
        response.setMessage("Login failed: " + e.getMessage());
        return response;
    }
}
    
    

    


  


public CarRentalUser registerUser(CarRentalUser carRentalUser) {
    carRentalRepository.findByPhone(carRentalUser.getPhone())
        .ifPresent(user -> {
            throw new RuntimeException("Phone number already exists");
        });
    
    String encodedPassword = passwordEncoder.encode(carRentalUser.getPassword());
    carRentalUser.setPassword(encodedPassword);
    
    if (carRentalUser.getRole() == null) {
        carRentalUser.setRole("USER"); 
    }
    
    return carRentalRepository.save(carRentalUser);
}


public boolean checkUserExistsByMobile(String phone) {
    return carRentalRepository.existsByPhone(phone); // replace with your actual repo logic
}

public boolean checkDriverExistsByContact(String contactNo) {
    return vendorDriverRepo.existsByContactNo(contactNo); // replace with your actual repo logic
}





}
