package com.workshop.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.workshop.Service.PasswordResetService;

@RestController
@RequestMapping("/api/password-reset")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/request-reset")
    public String requestPasswordReset(@RequestParam("email") String email) {
        passwordResetService.sendPasswordResetEmail(email);
        return "Password reset OTP sent to your email.";
    }

    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("email") String email, @RequestParam("otp") String otp) {
        boolean isValid = passwordResetService.validateOTP(email, otp);
        if (isValid) {
            return "OTP verified successfully.";
        } else {
            return "Invalid OTP.";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email, @RequestParam("newPassword") String newPassword) {
        passwordResetService.resetPassword(email, newPassword);
        return "Password reset successfully.";
    }
}
