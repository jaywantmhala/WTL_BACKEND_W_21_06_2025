package com.workshop.CarRental.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.workshop.CarRental.DTO.CarRentalLoginRequest;
import com.workshop.CarRental.DTO.CarRentalLoginResponse;
import com.workshop.CarRental.DTO.DriverLoginRequest;
import com.workshop.CarRental.DTO.DriverLoginResponse;

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
            
            return new CarRentalLoginResponse("Login Successful", user.getUsername(), user.getRole(), user.getId());
            
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


}
