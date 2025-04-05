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
import com.workshop.Entity.VendorDrivers;
import com.workshop.Repo.VendorDriverRepo;

@Service
public class AuthService {
    

    @Autowired
    private CarRentalRepository carRentalRepository;

    @Autowired
    private VendorDriverRepo vendorDriverRepo;


    @Autowired
    private PasswordEncoder passwordEncoder;


    public CarRentalLoginResponse authenticateUser(CarRentalLoginRequest loginRequest) {
        try {
            CarRentalUser user = ((Optional<CarRentalUser>) carRentalRepository.findByPhone(loginRequest.getMobile()))
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return new CarRentalLoginResponse("Invalid Password", null,null, 0);
            }
            
            return new CarRentalLoginResponse("Login Successful", user.getUserName(), user.getRole(), user.getId());
            
        } catch (Exception e) {
            return new CarRentalLoginResponse("Login Failed",null,null, 0);
        }


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
        // Check CarRentalUser first
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

        // Check VendorDrivers if user not found
        VendorDrivers driver = vendorDriverRepo.findByContactNo(mobile);
        if (driver != null) {
            if (password.equals(driver.getPassword())) { // Note: Should implement hashing
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

        // If neither found
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
