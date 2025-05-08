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

    
    @MessageMapping("/driver-location")
    public void updateDriverLocation(@Payload LocationMessage locationMessage) {
        locationService.saveDriverLocation(
                locationMessage.getDriverId(),
                locationMessage.getLatitude(),
                locationMessage.getLongitude()
        );
        
        locationService.updateDriverLocation(locationMessage);
    }

    
    @MessageMapping("/user-location")
    public void updateUserLocation(@Payload LocationMessage locationMessage) {
        locationService.saveUserLocation(
                locationMessage.getUserId(),
                locationMessage.getLatitude(),
                locationMessage.getLongitude()
        );
        
        locationService.updateUserLocation(locationMessage);
    }

    
    @MessageMapping("/send-otp")
    public void sendOtp(@Payload TripStatusMessage message) {
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