package com.workshop.CarRental.Controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.workshop.CarRental.DTO.LocationMessage;
import com.workshop.CarRental.DTO.LocationUpdateMessage;
import com.workshop.CarRental.Entity.CarRentalUser;
import com.workshop.CarRental.Repository.CarRentalRepository;
import com.workshop.Entity.Booking;
import com.workshop.Entity.VendorDrivers;
import com.workshop.Repo.BookingRepo;
import com.workshop.Repo.VendorDriverRepo;

@Controller
public class LocationTrackingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final BookingRepo bookingRepository;
    private final VendorDriverRepo vendorDriverRepository;
    private final CarRentalRepository userRepository;

    @Autowired
    public LocationTrackingController(SimpMessagingTemplate messagingTemplate,
    BookingRepo bookingRepository,
                                    VendorDriverRepo vendorDriverRepository,
                                    CarRentalRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.bookingRepository = bookingRepository;
        this.vendorDriverRepository = vendorDriverRepository;
        this.userRepository = userRepository;
    }

    @MessageMapping("/update-location")
    public void updateLocation(LocationUpdateMessage message) {
        if ("DRIVER".equals(message.getRole())) {
            VendorDrivers driver = vendorDriverRepository.findById(message.getUserId())
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            driver.setDriverLatitude(message.getLatitude());
            driver.setDriverLongitude(message.getLongitude());
            vendorDriverRepository.save(driver);
        } else {
            CarRentalUser user = userRepository.findById(message.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setUserlatitude(message.getLatitude());
            user.setUserlongitude(message.getLongitude());
            userRepository.save(user);
        }

        // Notify the other party about the location update
        Booking booking = bookingRepository.findById(message.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String destination;
        if ("DRIVER".equals(message.getRole())) {
            destination = "/topic/user-location/" + booking.getCarRentalUser();
        } else {
            destination = "/topic/driver-location/" + booking.getVendorDriver();
        }

        messagingTemplate.convertAndSend(destination, new LocationMessage(
                message.getLatitude(),
                message.getLongitude(),
                message.getUserId(),
                message.getRole(),
                new Date()
        ));
    }
}