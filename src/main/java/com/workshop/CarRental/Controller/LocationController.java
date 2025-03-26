package com.workshop.CarRental.Controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.workshop.CarRental.DTO.LocationUpdate;
import com.workshop.CarRental.DTO.Trip;
import com.workshop.CarRental.DTO.TripRequest;
import com.workshop.CarRental.DTO.TripResponse;
import com.workshop.CarRental.Repository.TripRepository;


@RestController
public class LocationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    private  TripRepository tripRepository;

    @MessageMapping("/location.update")
    public void updateLocation(@Payload LocationUpdate update) {
        update.setTimestamp(LocalDateTime.now());
        
        if (update.getTripId() != null && tripRepository.existsById(update.getTripId())) {
            String destination = "/topic/location/" + 
                ("DRIVER".equals(update.getType()) ? "user/" : "driver/") + 
                update.getTripId();
            
            messagingTemplate.convertAndSend(destination, update);
        } else if ("DRIVER".equals(update.getType())) {
            messagingTemplate.convertAndSend("/topic/location/drivers/available", update);
        }
    }

    @MessageMapping("/trip.request")
    public void requestTrip(@Payload TripRequest request) {
        String tripId = "TRIP-" + UUID.randomUUID();
        
        Trip trip = new Trip();
        trip.setTripId(tripId);
        trip.setUserId(request.getUserId());
        trip.setDriverId(request.getDriverId());
        trip.setStatus(Trip.TripStatus.REQUESTED);
        trip.setCreatedAt(LocalDateTime.now());
        
        tripRepository.save(trip);
        
        messagingTemplate.convertAndSend(
            "/topic/trip/request/" + request.getDriverId(),
            new TripResponse(tripId, request.getUserId(), request.getDriverId())
        );
    }
}