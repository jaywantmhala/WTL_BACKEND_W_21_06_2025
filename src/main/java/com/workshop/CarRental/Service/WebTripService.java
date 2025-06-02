package com.workshop.CarRental.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.workshop.CarRental.Entity.TripStatusMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class WebTripService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, String> bookingOtps = new HashMap<>();
    private final Map<String, String> finalOtps = new HashMap<>();
    
    @Autowired
    public WebTripService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    
    
    public void sendOtp(TripStatusMessage message) {
        // Generate a random 4-digit OTP    //  Generates and sends OTP to user for trip verification

        String otp = generateOtp();
        
        bookingOtps.put(message.getBookingId(), otp);
        
        TripStatusMessage userResponse = new TripStatusMessage();
        userResponse.setBookingId(message.getBookingId());
        userResponse.setAction("OTP_SENT");
        userResponse.setOtp(otp);
        userResponse.setType("OTP_SENT");
        
        messagingTemplate.convertAndSend(
            "/topic/booking/" + message.getBookingId() + "/user-notifications", 
            userResponse
        );
        
        TripStatusMessage driverResponse = new TripStatusMessage();
        driverResponse.setBookingId(message.getBookingId());
        driverResponse.setAction("OTP_SENT");
        driverResponse.setType("OTP_SENT");
        
        
        messagingTemplate.convertAndSend(
            "/topic/booking/" + message.getBookingId() + "/driver-notifications", 
            driverResponse
        );
    }
    
    /**
     * Handles final OTP generation or storage for trip end verification
     */
    public void sendFinalOtp(TripStatusMessage message) {
        if ("REQUEST_FINAL_OTP".equals(message.getAction())) {
            // This is a request from driver to generate final OTP for the user
            TripStatusMessage userResponse = new TripStatusMessage();
            userResponse.setBookingId(message.getBookingId());
            userResponse.setAction("REQUEST_FINAL_OTP");
            userResponse.setType("REQUEST_FINAL_OTP");
            
            // Send request to user to display final OTP
            messagingTemplate.convertAndSend(
                "/topic/booking/" + message.getBookingId() + "/user-notifications", 
                userResponse
            );


        } else if ("STORE_FINAL_OTP".equals(message.getAction())) {
            // This is the final OTP generated on user side, store it for verification
            finalOtps.put(message.getBookingId(), message.getOtp());
        }
    }
    
    /**
     * Verifies OTP entered by driver
     * 
     */
    public void verifyOtp(TripStatusMessage message) {
        String storedOtp = bookingOtps.get(message.getBookingId());
        boolean isValid = storedOtp != null && storedOtp.equals(message.getOtp());
        
        TripStatusMessage response = new TripStatusMessage();
        response.setBookingId(message.getBookingId());
        response.setAction(isValid ? "OTP_VERIFIED" : "OTP_INVALID");
        response.setType(isValid ? "OTP_VERIFIED" : "OTP_INVALID");
        
        // Notify both user and driver about verification result
        messagingTemplate.convertAndSend(
            "/topic/booking/" + message.getBookingId() + "/user-notifications", 
            response
        );
        
        messagingTemplate.convertAndSend(
            "/topic/booking/" + message.getBookingId() + "/driver-notifications", 
            response
        );
        
        // If OTP is verified, remove it from storage
        if (isValid) {
            bookingOtps.remove(message.getBookingId());
        }
    }
    
    /**
     * Records start odometer and begins the trip
     */
    public void startTrip(TripStatusMessage message) {
        // In a real implementation, save the odometer reading and destination to the database
        // Update the trip status in your system
        
        TripStatusMessage response = new TripStatusMessage();
        response.setBookingId(message.getBookingId());
        response.setAction("TRIP_STARTED");
        response.setType("TRIP_STARTED");
        response.setStartOdometer(message.getStartOdometer());
        response.setDestination(message.getDestination());
        response.setDestinationLatitude(message.getDestinationLatitude());
        response.setDestinationLongitude(message.getDestinationLongitude());
        
        // Notify both user and driver that trip has started
        messagingTemplate.convertAndSend(
            "/topic/booking/" + message.getBookingId() + "/user-notifications", 
            response
        );
        
        messagingTemplate.convertAndSend(
            "/topic/booking/" + message.getBookingId() + "/driver-notifications", 
            response
        );
    }
    
    
    public void endTrip(TripStatusMessage message) {
        String storedFinalOtp = finalOtps.get(message.getBookingId());
        boolean isValid = storedFinalOtp != null && storedFinalOtp.equals(message.getOtp());
        
        if (!isValid) {
            TripStatusMessage response = new TripStatusMessage();
            response.setBookingId(message.getBookingId());
            response.setAction("FINAL_OTP_INVALID");
            response.setType("FINAL_OTP_INVALID");
            
            messagingTemplate.convertAndSend(
                "/topic/booking/" + message.getBookingId() + "/driver-notifications", 
                response
            );
            return;
        }
        
        double startOdo = message.getStartOdometer() != null ? message.getStartOdometer() : 0;
        double endOdo = message.getEndOdometer() != null ? message.getEndOdometer() : 0;
        double distance = endOdo - startOdo;
        
        TripStatusMessage response = new TripStatusMessage();
        response.setBookingId(message.getBookingId());
        response.setAction("TRIP_ENDED");
        response.setType("TRIP_ENDED");
        response.setStartOdometer(startOdo);
        response.setEndOdometer(endOdo);
        
        messagingTemplate.convertAndSend(
            "/topic/booking/" + message.getBookingId() + "/user-notifications", 
            response
        );
        
        messagingTemplate.convertAndSend(
            "/topic/booking/" + message.getBookingId() + "/driver-notifications", 
            response
        );
        
        finalOtps.remove(message.getBookingId());
    }
    
    
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); 
        return String.valueOf(otp);
    }

} 