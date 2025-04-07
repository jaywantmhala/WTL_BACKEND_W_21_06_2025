package com.workshop.CarRental.Controller;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.workshop.CarRental.DTO.LocationMessage;
import com.workshop.CarRental.DTO.LocationUpdateMessage;
import com.workshop.CarRental.DTO.OtpRequestMessage;
import com.workshop.CarRental.DTO.OtpValidationMessage;
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
    private final Map<Integer, String> pickupOtpMap = new HashMap<>();
    private final Map<Integer, String> dropOffOtpMap = new HashMap<>();
    @Autowired
    public LocationTrackingController(SimpMessagingTemplate messagingTemplate,BookingRepo bookingRepository,VendorDriverRepo vendorDriverRepository,CarRentalRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.bookingRepository = bookingRepository;
        this.vendorDriverRepository = vendorDriverRepository;
        this.userRepository = userRepository;
    }
@MessageMapping("/update-location")
    public void updateLocation(LocationUpdateMessage message) {
       Booking booking = bookingRepository.findById(message.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
if ("DRIVER".equals(message.getRole())) {
            validateDriverBookingAssociation(booking, message.getUserId());
            updateDriverLocation(message);
        } else {
            validateUserBookingAssociation(booking, message.getUserId());
            updateUserLocation(message);
        }
notifyOtherParty(booking, message);
    }
    @MessageMapping("/request-pickup-otp")
    public void requestPickupOtp(@Payload OtpRequestMessage message) {
        int bookingId = message.getBookingId();
        int driverId = message.getDriverId();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        validateDriverBookingAssociation(booking, driverId);
        String otp = generateOTP();
        pickupOtpMap.put(bookingId, otp);
        int userId = booking.getCarRentalUser().getId();
        Map<String, Object> otpMessage = new HashMap<>();
        otpMessage.put("bookingId", bookingId);
        otpMessage.put("otp", otp);
        otpMessage.put("type", "PICKUP");
        otpMessage.put("timestamp", new Date());
        messagingTemplate.convertAndSend("/topic/otp/" + userId, otpMessage);
    }
    @MessageMapping("/validate-pickup-otp")
    public void validatePickupOtp(@Payload OtpValidationMessage message) {
        int bookingId = message.getBookingId();
        int driverId = message.getDriverId();
        String providedOtp = message.getOtp();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
         validateDriverBookingAssociation(booking, driverId);
        String storedOtp = pickupOtpMap.get(bookingId);
         boolean isValid = storedOtp != null && storedOtp.equals(providedOtp);
    int userId = booking.getCarRentalUser().getId();
        Map<String, Object> resultMessage = new HashMap<>();
        resultMessage.put("bookingId", bookingId);
        resultMessage.put("isValid", isValid);
        resultMessage.put("type", "PICKUP");
        resultMessage.put("timestamp", new Date());
        messagingTemplate.convertAndSend("/topic/otp-validation/driver/" + driverId, resultMessage);
        messagingTemplate.convertAndSend("/topic/otp-validation/user/" + userId, resultMessage);
        if (isValid) {
            pickupOtpMap.remove(bookingId);
            booking.setStatus(1);
            bookingRepository.save(booking);
        }
    }
     @MessageMapping("/request-dropoff-otp")
    public void requestDropoffOtp(@Payload OtpRequestMessage message) {
        int bookingId = message.getBookingId();
        int driverId = message.getDriverId();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        validateDriverBookingAssociation(booking, driverId);
        String otp = generateOTP();
        dropOffOtpMap.put(bookingId, otp);
        int userId = booking.getCarRentalUser().getId();
        Map<String, Object> otpMessage = new HashMap<>();
        otpMessage.put("bookingId", bookingId);
        otpMessage.put("otp", otp);
        otpMessage.put("type", "DROPOFF");
        otpMessage.put("timestamp", new Date());
        messagingTemplate.convertAndSend("/topic/otp/" + userId, otpMessage);
    }
    @MessageMapping("/validate-dropoff-otp")
    public void validateDropoffOtp(@Payload OtpValidationMessage message) {
        int bookingId = message.getBookingId();
        int driverId = message.getDriverId();
        String providedOtp = message.getOtp();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        validateDriverBookingAssociation(booking, driverId);
        String storedOtp = dropOffOtpMap.get(bookingId);
        boolean isValid = storedOtp != null && storedOtp.equals(providedOtp);
        int userId = booking.getCarRentalUser().getId();
         Map<String, Object> resultMessage = new HashMap<>();
        resultMessage.put("bookingId", bookingId);
        resultMessage.put("isValid", isValid);
        resultMessage.put("type", "DROPOFF");
        resultMessage.put("timestamp", new Date());
        messagingTemplate.convertAndSend("/topic/otp-validation/driver/" + driverId, resultMessage);
        messagingTemplate.convertAndSend("/topic/otp-validation/user/" + userId, resultMessage);
        if (isValid) {
            dropOffOtpMap.remove(bookingId);
            booking.setStatus(2);
        bookingRepository.save(booking);
        }
    }
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }
private void validateDriverBookingAssociation(Booking booking, int userId) {
        if (booking.getVendorDriver().getVendorDriverId() != userId) {
            throw new RuntimeException("Driver not associated with this booking");
        }
    }
private void validateUserBookingAssociation(Booking booking, int userId) {
        if (booking.getCarRentalUser().getId() != userId) {
            throw new RuntimeException("User not associated with this booking");
        }
    }
private void updateDriverLocation(LocationUpdateMessage message) {
        VendorDrivers driver = vendorDriverRepository.findById(message.getUserId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setDriverLatitude(message.getLatitude());
        driver.setDriverLongitude(message.getLongitude());
        vendorDriverRepository.save(driver);
    }
private void updateUserLocation(LocationUpdateMessage message) {
        CarRentalUser user = userRepository.findById(message.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserlatitude(message.getLatitude());
        user.setUserlongitude(message.getLongitude());
        userRepository.save(user);
    }
private void notifyOtherParty(Booking booking, LocationUpdateMessage message) {
        String destination;
        int recipientId;

        if ("DRIVER".equals(message.getRole())) {
            recipientId = booking.getCarRentalUser().getId();
            destination = "/topic/user-location/" + recipientId;
        } else {
            recipientId = booking.getVendorDriver().getVendorDriverId();
            destination = "/topic/driver-location/" + recipientId;
        }
messagingTemplate.convertAndSend(destination, createLocationMessage(message));
    }
private LocationMessage createLocationMessage(LocationUpdateMessage message) {
        return new LocationMessage(
                message.getLatitude(),
                message.getLongitude(),
                message.getUserId(),
                message.getRole(),
                new Date()
        );
    }
}