package com.workshop.CarRental.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.workshop.Service.EmailService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CarRentalBookingService carRentalBookingService;

    @Autowired
    private AuthService authService;

    @Autowired
    PasswordEncoder passwordEncoder;

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
    


      @Autowired
    private EmailService emailService;

    private Map<String, String> otpStorage = new HashMap<>();

    // Send OTP to email
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); 
        otpStorage.put(email, otp);

        String subject = "Password Reset OTP";
        String message = "Your OTP for password reset is: " + otp;

        emailService.sendEmail(email, subject, message);

        return ResponseEntity.ok("OTP sent successfully to " + email);
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }

    // Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("password");

        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            String encryptedPassword = passwordEncoder.encode(newPassword); // Encrypt password
            boolean isUpdated = carRentalBookingService.updatePassword(email, encryptedPassword);

            if (isUpdated) {
                otpStorage.remove(email); // Remove OTP after successful verification
                return ResponseEntity.ok("Password updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to update password. User not found.");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }


}
