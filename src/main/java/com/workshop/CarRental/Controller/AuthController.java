package com.workshop.CarRental.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workshop.CarRental.DTO.CarRentalLoginRequest;
import com.workshop.CarRental.DTO.CarRentalLoginResponse;
import com.workshop.CarRental.DTO.DriverLoginRequest;
import com.workshop.CarRental.DTO.DriverLoginResponse;
import com.workshop.CarRental.Entity.CarRentalUser;
import com.workshop.CarRental.Service.AuthService;
import com.workshop.CarRental.Service.CarRentalBookingService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CarRentalBookingService carRentalBookingService;

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public CarRentalUser createCarRentalUser(@RequestBody CarRentalUser carRentalUser){
        return this.authService.registerUser(carRentalUser);

    }

     @PostMapping("/userlogin")
    public ResponseEntity<CarRentalLoginResponse> loginUser(
             @RequestBody CarRentalLoginRequest loginRequest) {
        
        CarRentalLoginResponse response = authService.authenticateUser(loginRequest);
        
        if (response.getUsername() != null) {
            return ResponseEntity.ok()
                    .header("X-Auth-Status", "success")
                    .body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("X-Auth-Status", "failed")
                    .body(response);
        }
    }


    @PostMapping("/driverlogin")
    public ResponseEntity<?> loginDriver(
             @RequestBody DriverLoginRequest loginRequest) {
        
                DriverLoginResponse response = authService.authenticateDriver(loginRequest);
        
        if (response.getDriverName() != null) {
            return ResponseEntity.ok()
                    .header("X-Auth-Status", "success")
                    .body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("X-Auth-Status", "failed")
                    .body(response);
        }
    }
    
}
