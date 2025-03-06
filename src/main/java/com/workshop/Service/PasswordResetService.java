package com.workshop.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.workshop.Entity.User;
import com.workshop.Repo.UserRepo;

import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String otp = generateOTP();
        boolean emailSent = emailService.sendEmail(otp, "Password Reset OTP", email);

        if (emailSent) {
            System.out.println("OTP sent successfully!");
        } else {
            throw new RuntimeException("Failed to send OTP email");
        }
    }

    private String generateOTP() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    public boolean validateOTP(String email, String otp) {

        return true;
    }

    public void resetPassword(String email, String newPassword) {
        if (validateOTP(email, "OTP_FROM_USER")) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                String encodedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedPassword); // Save the hashed password
                userRepository.save(user);
                System.out.println("Password reset successfully.");
            } else {
                throw new RuntimeException("User not found");
            }
        } else {
            throw new RuntimeException("Invalid OTP");
        }
    }
}
