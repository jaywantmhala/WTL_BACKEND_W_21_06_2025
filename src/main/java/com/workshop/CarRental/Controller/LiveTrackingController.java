package com.workshop.CarRental.Controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import com.workshop.CarRental.Entity.LocationMessage;
import com.workshop.CarRental.Entity.TripStatusMessage;
import com.workshop.CarRental.Service.LocationService;
import com.workshop.CarRental.Service.WebTripService;

@RestController
public class LiveTrackingController {

    private final LocationService locationService;
    private final WebTripService tripService;

    @Autowired
    public LiveTrackingController(LocationService locationService, WebTripService tripService) {
        this.locationService = locationService;
        this.tripService = tripService;
    }

    /**
     * Handles driver location updates
     */
    @MessageMapping("/driver-location")
    public void updateDriverLocation(@Payload LocationMessage locationMessage) {
        // Save driver location to database
        locationService.saveDriverLocation(
                locationMessage.getDriverId(),
                locationMessage.getLatitude(),
                locationMessage.getLongitude()
        );
        
        // Broadcast to relevant user
        locationService.updateDriverLocation(locationMessage);
    }

    /**
     * Handles user location updates
     */
    @MessageMapping("/user-location")
    public void updateUserLocation(@Payload LocationMessage locationMessage) {
        // Save user location to database
        locationService.saveUserLocation(
                locationMessage.getUserId(),
                locationMessage.getLatitude(),
                locationMessage.getLongitude()
        );
        
        // Broadcast to relevant driver
        locationService.updateUserLocation(locationMessage);
    }

    /**
     * Handles OTP sending request
     * - Initial OTP verification
     * - Final OTP verification at trip end
     */
    @MessageMapping("/send-otp")
    public void sendOtp(@Payload TripStatusMessage message) {
        // Check the action to determine if it's initial or final OTP
        if ("REQUEST_FINAL_OTP".equals(message.getAction()) || "STORE_FINAL_OTP".equals(message.getAction())) {
            tripService.sendFinalOtp(message);
        } else {
            tripService.sendOtp(message);
        }
    }

    /**
     * Handles OTP verification from driver
     */
    @MessageMapping("/verify-otp")
    public void verifyOtp(@Payload TripStatusMessage message) {
        tripService.verifyOtp(message);
    }

    /**
     * Handles trip start (recording odometer and destination)
     */
    @MessageMapping("/start-trip")
    public void startTrip(@Payload TripStatusMessage message) {
        tripService.startTrip(message);
    }

    /**
     * Handles trip end (recording odometer and final OTP verification)
     */
    @MessageMapping("/end-trip")
    public void endTrip(@Payload TripStatusMessage message) {
        tripService.endTrip(message);
    }
} 