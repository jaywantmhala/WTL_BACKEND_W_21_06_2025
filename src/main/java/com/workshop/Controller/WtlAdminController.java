
package com.workshop.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.workshop.DTO.BookingDTO;
import com.workshop.DTO.CancellationRequest;
import com.workshop.DTO.CancellationResult;
import com.workshop.DTO.CityDTO;
import com.workshop.DTO.LoginRequest;
import com.workshop.DTO.PriceUpdateRequest;
import com.workshop.DTO.StateDTO;
import com.workshop.Entity.Booking;
import com.workshop.Entity.CabInfo;
import com.workshop.Entity.Cities;
import com.workshop.Entity.Penalty;
import com.workshop.Entity.Popup;
import com.workshop.Entity.States;
import com.workshop.Entity.Tripprice;
import com.workshop.Entity.User;
import com.workshop.Entity.onewayTrip;
import com.workshop.Entity.roundTrip;
import com.workshop.Repo.BookingRepo;
import com.workshop.Repo.StateRepository;
import com.workshop.Repo.Trip;
import com.workshop.Service.BookingService;
import com.workshop.Service.CabInfoService;
import com.workshop.Service.CitiesService;
import com.workshop.Service.EmailService;
import com.workshop.Service.PopupService;
import com.workshop.Service.SmsService;
import com.workshop.Service.StatesService;
import com.workshop.Service.TripService;
import com.workshop.Service.UserDetailServiceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

import com.workshop.Service.TripRateService;

@RestController
// @RequestMapping
public class WtlAdminController {

    private final AuthenticationManagerBuilder authenticationManager;
    @Autowired
    BookingService ser;

    @Autowired
    private TripService tripSer;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SmsService smsService;

    @Autowired
    CabInfoService cabser;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TripRateService tripRateService;

    @Autowired
    private StatesService statesService;

    @Autowired
    private CitiesService citiesService;

    @Autowired
    PopupService service;

    @Autowired
    UserDetailServiceImpl userService;

    WtlAdminController(AuthenticationManagerBuilder authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // private final String apiKey = "AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w"; //
    // Replace with your Google API key

    @GetMapping("/states/{id}")
    public ResponseEntity<States> getStateById(@PathVariable Long id) {
        return this.statesService.getStateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public List<States> getAllState() {
        return this.statesService.getAllState();
    }

    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/api/states")
    public List<StateDTO> getStates() {
        return stateRepository.findAll()
                .stream()
                .map(state -> new StateDTO(state.getId(), state.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/city/all")
    public List<Cities> getAllCities() {
        return citiesService.getAllCities();
    }

    @GetMapping("/{id}")
    public Optional<Cities> getCityById(@PathVariable Long id) {
        return citiesService.getCityById(id);
    }

    @GetMapping("/cities/{stateId}")
    public ResponseEntity<List<CityDTO>> getCitiesByState(@PathVariable Long stateId) {
        List<Cities> cities = citiesService.getCitiesByState(stateId);
        if (cities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        // Convert Cities entities to CityDTO
        List<CityDTO> cityDTOs = cities.stream()
                .map(city -> {
                    CityDTO dto = new CityDTO();
                    dto.setId(city.getId());
                    dto.setName(city.getName());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(cityDTOs);
    }

    @PostMapping("/updateTripPricing")
    public ResponseEntity<Map<String, String>> updateTripPricing(@RequestBody Tripprice tripPricing) {
        // Update trip pricing logic
        this.tripRateService.updateTripPricing(tripPricing);

        // Create a map to return as a JSON object
        Map<String, String> response = new HashMap<>();
        response.put("message", "Trip pricing updated successfully!");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-price/{id}")
    public ResponseEntity<onewayTrip> updateTripPrice(@PathVariable Long id, @RequestBody PriceUpdateRequest request) {
        onewayTrip updatedTrip = tripSer.updatePrice(id, request.getHatchback(), request.getSedan(),
                request.getSedanpremium(), request.getSuv(), request.getSuvplus(), request.getSourceState(),
                request.getSourceCity(), request.getDestinationState(), request.getDestinationCity());
        return ResponseEntity.ok(updatedTrip);
    }

    @PutMapping("/update-prices")
    public ResponseEntity<Map<String, String>> updatePrices(
            @RequestParam String sourceState,
            @RequestParam String destinationState,
            @RequestParam String sourceCity,
            @RequestParam String destinationCity,
            @RequestParam int hatchbackPrice,
            @RequestParam int sedanPrice,
            @RequestParam int sedanPremiumPrice,
            @RequestParam int suvPrice,
            @RequestParam int suvPlusPrice) {

        // Call the service to update trip prices
        tripSer.updatePrices(sourceState, destinationState, sourceCity, destinationCity,
                hatchbackPrice, sedanPrice, sedanPremiumPrice, suvPrice, suvPlusPrice);

        // Construct a JSON response
        Map<String, String> response = new HashMap<>();
        response.put("message", "Prices updated successfully");

        return ResponseEntity.ok(response);
    }

    @Transactional
    @DeleteMapping("/delete-booking/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable String bookingId) {
        String responseMessage = ser.deleteBookingByBookingId(bookingId);
        if (responseMessage.contains("not found")) {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if the booking does not exist
        }
        return ResponseEntity.ok(responseMessage); // Return 200 OK with the success message
    }

    // @PostMapping("/getPrice")
    // public List<Trip> getPrice(@RequestBody Map<String, String> requestBody) {
    // String to = requestBody.get("to");
    // String from = requestBody.get("from");
    // String tripType = requestBody.get("tripType");

    // String city1 = userService.getLongNameByCity(to, apiKey);
    // String[] parts = city1.split(" ");
    // String cityName = parts[0];
    // String city2 = userService.getLongNameByCity(from, apiKey);
    // String[] parts1 = city2.split(" ");
    // String cityName1 = parts1[0];

    // System.out.println(city1);
    // System.out.println(city2);
    // System.out.println(cityName);
    // System.out.println(cityName1);

    // if ("oneWay".equals(tripType)) {
    // return tripSer.getonewayTrip(cityName, cityName1);
    // } else if ("roundTrip".equals(tripType)) {
    // return tripSer.getRoundTrip(cityName, cityName1);
    // } else {
    // // Handle other cases or return an empty list if needed
    // return new ArrayList<>();
    // }
    // }

    @Autowired
    private CabInfoService cabInfoService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/details")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings); // Return the list of bookings with HTTP 200 OK status
    }

    // @GetMapping("/booking/{id}") // Define the path variable for booking ID
    // public ResponseEntity<Booking> getBookingById(@PathVariable int id) {
    // // Fetch the booking by ID using the service
    // Booking booking = bookingService.findBookingbyId(id);

    // // Check if the booking is found
    // if (booking != null) {
    // return new ResponseEntity<>(booking, HttpStatus.OK); // Return the booking
    // with a 200 OK status
    // } else {
    // return new ResponseEntity<>(HttpStatus.NOT_FOUND); // If not found, return
    // 404 Not Found
    // }
    // }

    @GetMapping("/booking/{id}")
    public ResponseEntity<BookingDTO> getBookingSById(@PathVariable int id) {
        // Call the service to fetch the booking as a DTO
        BookingDTO bookingDTO = bookingService.getBooking(id);

        // Check if the booking was found
        if (bookingDTO != null) {
            return ResponseEntity.ok(bookingDTO); // Return HTTP 200 OK with the booking DTO data
        } else {
            return ResponseEntity.notFound().build(); // Return HTTP 404 if booking is not found
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> changeStatus(
            @PathVariable int id,
            @RequestBody Map<String, Integer> requestBody) {

        int newStatus = requestBody.get("status"); // Extract status from the request body

        try {
            // Call the service to update the booking status
            Booking updatedBooking = bookingService.updateStatus(id, newStatus);
            return ResponseEntity.ok(updatedBooking); // Return the updated booking
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if booking not found
        }
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable int id) {
        this.bookingService.deleteBooking(id);
    }

    @GetMapping("/getStatus/{status}")
    public List<Booking> getBookingByStatus(@PathVariable int status) {
        return this.bookingService.getBookingByStatus(status);
    }

    @PutMapping("/update-roundway-prices")
    public ResponseEntity<Map<String, String>> updateRoundWayPrices(
            @RequestParam String sourceState,
            @RequestParam String destinationState,
            @RequestParam String sourceCity,
            @RequestParam String destinationCity,
            @RequestParam int hatchbackPrice,
            @RequestParam int sedanPrice,
            @RequestParam int sedanPremiumPrice,
            @RequestParam int suvPrice,
            @RequestParam int suvPlusPrice,
            @RequestParam int ertiga) {

        // Call the service to update trip prices
        tripSer.updatePricesByRoundWay(sourceState, destinationState, sourceCity, destinationCity,
                hatchbackPrice, sedanPrice, sedanPremiumPrice, suvPrice, suvPlusPrice,ertiga);

        // Construct a JSON response
        Map<String, String> response = new HashMap<>();
        response.put("message", "Prices updated successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bookingId}/assignVendor/{vendorId}")
    public ResponseEntity<Booking> assignVendorToBooking(
            @PathVariable int bookingId,
            @PathVariable Long vendorId) {

        // Call the service method to assign vendor
        Booking updatedBooking = bookingService.assignVendorToBooking(bookingId, vendorId);

        if (updatedBooking == null) {
            // If the booking or vendor was not found, return a 404 Not Found
            return ResponseEntity.notFound().build();
        }

        // If the vendor is assigned successfully, return the updated booking
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/{vendorId}/vendorByBookings")
    public ResponseEntity<List<Booking>> getBookingsByVendor(@PathVariable Long vendorId) {
        List<Booking> bookings = bookingService.getBookingByVendor(vendorId);
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns 204 if no bookings found
        }
        return ResponseEntity.ok(bookings); // Returns 200 with the list of bookings
    }

    @PutMapping("/{bookingId}/assignVendorCab/{vendorCabId}")

    public ResponseEntity<Booking> assignVendorCabToBooking(
            @PathVariable int bookingId,
            @PathVariable int vendorCabId) {

        // Call the service method to assign vendor
        Booking updatedBooking = bookingService.assignVendorCabToBooking(bookingId, vendorCabId);

        if (updatedBooking == null) {
            // If the booking or vendor was not found, return a 404 Not Found
            return ResponseEntity.notFound().build();
        }

        if (updatedBooking.getVendor() == null || updatedBooking.getVendorCab() == null) {
            System.out.println("Vendor is not assigned");
        } else {
            String subject = "Booking Confirmation - " + updatedBooking.getBookid();
            String message = "<!DOCTYPE html>"
                    + "<html lang='en'>"
                    + "<head>"
                    + "<meta charset='UTF-8'>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Booking Confirmation</title>"
                    + "</head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f7f7f7; margin: 0; padding: 0;'>"
                    + "<div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); overflow: hidden;'>"
                    + "<div style='background-color: #007BFF; color: #ffffff; padding: 20px; text-align: center;'>"
                    + "<h1 style='margin: 0; font-size: 24px; font-weight: bold;'>Booking Confirmation</h1>"
                    + "</div>"
                    + "<div style='padding: 20px;'>"
                    + "<h3 style='color: #007BFF; font-size: 20px; margin-bottom: 20px;'>Hello "
                    + updatedBooking.getName() + ",</h3>"
                    + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-bottom: 20px;'>Your booking has been confirmed. Below are the details of your booking:</p>"
                    + "<div style='margin-top: 20px;'>"
                    + "<ul style='list-style-type: none; padding: 0;'>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Booking ID:</strong> "
                    + updatedBooking.getBookid() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Pickup Location:</strong> "
                    + updatedBooking.getUserPickup() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Drop Location:</strong> "
                    + updatedBooking.getUserDrop() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Trip Type:</strong> "
                    + updatedBooking.getTripType() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Date:</strong> "
                    + updatedBooking.getDate() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Time:</strong> "
                    + updatedBooking.getTime() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Amount Paid:</strong> ₹"
                    + updatedBooking.getAmount() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Cab Name:</strong> "
                    + updatedBooking.getVendorCab().getCarName() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Vehicle No:</strong> "
                    + updatedBooking.getVendorCab().getVehicleNo() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver Name:</strong> "
                    + updatedBooking.getVendorDriver().getDriverName() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver Contact:</strong> "
                    + updatedBooking.getVendorDriver().getContactNo() + "</li>"
                    + "</ul>"
                    + "</div>"
                    + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-top: 20px;'>Thank you for choosing us! We wish you a safe and pleasant journey.</p>"
                    + "</div>"
                    + "<div style='text-align: center; padding: 20px; background-color: #f1f1f1; color: #777777; font-size: 14px;'>"
                    + "<p style='margin: 0;'>If you have any questions, feel free to contact us at <a href='mailto:support@example.com' style='color: #007BFF; text-decoration: none;'>support@example.com</a>.</p>"
                    + "<img src='https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExcjc1OGk0ZGVqNHFseDRrM3FvOW0xYnVyenJkcmQ2OXNsODE0djUzZyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/3oKIPhUfA1h2U2Koko/giphy.gif' alt='Namaskar' style='width: 100px; height: auto; margin-top: 10px;'>"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
            boolean emailSent = emailService.sendEmail(message, subject, updatedBooking.getEmail());

            if (emailSent) {
                System.out.println("Booking confirmation email sent successfully.");
            } else {
                System.out.println("Failed to send booking confirmation email.");
            }

        }

        return ResponseEntity.ok(updatedBooking);
    }

    @PutMapping("/{bookingId}/assignVendorDriver/{vendorDriverId}")
    public ResponseEntity<Booking> assignVendorDriverToBooking(
            @PathVariable int bookingId,
            @PathVariable int vendorDriverId) {

        // Call the service method to assign vendor
        Booking updatedBooking = bookingService.assignVendorDriverToBooking(bookingId, vendorDriverId);

        if (updatedBooking == null) {
            // If the booking or vendor was not found, return a 404 Not Found
            return ResponseEntity.notFound().build();
        }

        if (updatedBooking.getVendor() == null || updatedBooking.getVendorCab() == null) {
            System.out.println("Vendor is not assigned");
        } else {
            String subject = "Booking Assign From Your Vendor  - " + updatedBooking.getBookid();
            String message = "<!DOCTYPE html>"
                    + "<html lang='en'>"
                    + "<head>"
                    + "<meta charset='UTF-8'>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Booking Confirmation</title>"
                    + "</head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f7f7f7; margin: 0; padding: 0;'>"
                    + "<div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); overflow: hidden;'>"
                    + "<div style='background-color: #007BFF; color: #ffffff; padding: 20px; text-align: center;'>"
                    + "<h1 style='margin: 0; font-size: 24px; font-weight: bold;'>Booking Confirmation</h1>"
                    + "</div>"
                    + "<div style='padding: 20px;'>"
                    + "<h3 style='color: #007BFF; font-size: 20px; margin-bottom: 20px;'>Hello "
                    + updatedBooking.getName() + ",</h3>"
                    + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-bottom: 20px;'>Your booking has been confirmed. Below are the details of your booking:</p>"
                    + "<div style='margin-top: 20px;'>"
                    + "<ul style='list-style-type: none; padding: 0;'>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Booking ID:</strong> "
                    + updatedBooking.getBookid() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Pickup Location:</strong> "
                    + updatedBooking.getUserPickup() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Drop Location:</strong> "
                    + updatedBooking.getUserDrop() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Trip Type:</strong> "
                    + updatedBooking.getTripType() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Date:</strong> "
                    + updatedBooking.getDate() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Time:</strong> "
                    + updatedBooking.getTime() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Amount Paid:</strong> ₹"
                    + updatedBooking.getAmount() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Cab Name:</strong> "
                    + updatedBooking.getVendorCab().getCarName() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Vehicle No:</strong> "
                    + updatedBooking.getVendorCab().getVehicleNo() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver Name:</strong> "
                    + updatedBooking.getVendorDriver().getDriverName() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver Contact:</strong> "
                    + updatedBooking.getVendorDriver().getContactNo() + "</li>"
                    + "</ul>"
                    + "</div>"
                    + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-top: 20px;'>Thank you for choosing us! We wish you a safe and pleasant journey.</p>"
                    + "</div>"
                    + "<div style='text-align: center; padding: 20px; background-color: #f1f1f1; color: #777777; font-size: 14px;'>"
                    + "<p style='margin: 0;'>If you have any questions, feel free to contact us at <a href='mailto:support@example.com' style='color: #007BFF; text-decoration: none;'>support@example.com</a>.</p>"
                    + "<img src='https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExcjc1OGk0ZGVqNHFseDRrM3FvOW0xYnVyenJkcmQ2OXNsODE0djUzZyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/3oKIPhUfA1h2U2Koko/giphy.gif' alt='Namaskar' style='width: 100px; height: auto; margin-top: 10px;'>"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            boolean emailSent = emailService.sendEmail(message, subject, updatedBooking.getEmail());

            if (emailSent) {
                System.out.println("Booking confirmation email sent successfully.");
            } else {
                System.out.println("Failed to send booking confirmation email.");
            }
        }

        return ResponseEntity.ok(updatedBooking);
    }

    @PostMapping("/wtlLogin")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            user.setPassword(null);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    }

    @Autowired
    private BookingRepo bookingRepo;

    // @PostMapping("/customBooking")
    // public Booking createCustomBooking(@RequestBody Booking b) {
    //     return this.bookingService.createCustomBooking(b);
    // }

   @PostMapping("/create")
public String createBooking(
        @RequestParam(required = false) String fromLocation,
        @RequestParam(required = false) String toLocation,
        @RequestParam(required = false) String tripType,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
        @RequestParam(required = false) String time,
        @RequestParam(required = false) String distance,
        @RequestParam(required = false) String userId,
        @RequestParam(required = false) String bookingId,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String phone,
        @RequestParam(required = false) String userPickup,
        @RequestParam(required = false) String comanyName,

        @RequestParam(required = false) String userDrop,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(required = false) String userTripType,
        @RequestParam(required = false) String car,
        @RequestParam(required = false) String baseAmount,
        @RequestParam(required = false) Integer amount,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) String driverBhata,
        @RequestParam(required = false) Integer nightCharges,
        @RequestParam(required = false) Integer gst,
        @RequestParam(required = false) Integer serviceCharge,
        @RequestParam(required = false) String offer,
        @RequestParam(required = false) Integer offerPartial,
        @RequestParam(required = false) String offerAmount,
        @RequestParam(required = false) String txnId,
        @RequestParam(required = false) String payment,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd,
        @RequestParam(required = false) String timeEnd,
        @RequestParam(required = false) int collection,
        @RequestParam(required = false) String bookingType,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) String carrier,
        @RequestParam(required = false) String driverEnterOtpTimePreStarted,
        @RequestParam(required = false) String odoometerStarted,
        @RequestParam(required = false) String odoometerEnterTimeStarted,
        @RequestParam(required = false) String driverEnterOtpTimePostTrip,
        @RequestParam(required = false) String odometerEnding,
        @RequestParam(required = false) String odoometerEnterTimeEnding
) {
    Booking booking = new Booking();

    // Generate unique booking ID
    String bookids = "CSTM_WTL" + System.currentTimeMillis();
    booking.setBookid(bookids); // use generated ID

    booking.setBookingId(bookids);
    booking.setFromLocation(userPickup);
    booking.setToLocation(userDrop);
    booking.setTripType(tripType);
    booking.setStartDate(startDate);
    booking.setReturnDate(returnDate);
    booking.setCompanyName(comanyName);
    booking.setStatus(0);
    booking.setTime(time);
    booking.setDistance(distance);
    booking.setUserId(userId);
    booking.setName(name);
    booking.setEmail(email);
    booking.setPhone(phone);

    // Use the actual user pickup/drop input values
    booking.setUserPickup(userPickup);
    booking.setUserDrop(userDrop);

    booking.setDate(date);
    booking.setUserTripType(userTripType);
    booking.setCar(car);
    booking.setBaseAmount(baseAmount);
    booking.setAmount(amount);
    booking.setStatus(status);
    booking.setDriverBhata(driverBhata);
    booking.setNightCharges(nightCharges);
    booking.setGst(gst);
    booking.setServiceCharge(serviceCharge);
    booking.setOffer(offer);
    booking.setOfferPartial(offerPartial);
    booking.setOfferAmount(offerAmount);
    booking.setTxnId(txnId);
    booking.setPayment(payment);
    booking.setDateEnd(dateEnd);
    booking.setTimeEnd(timeEnd);
    booking.setCollection(collection);
    booking.setBookingType(bookingType);
    booking.setDescription(description);
    booking.setCarrier(carrier);
    booking.setDriverEnterOtpTimePreStarted(driverEnterOtpTimePreStarted);
    booking.setOdoometerStarted(odoometerStarted);
    booking.setOdoometerEnterTimeStarted(odoometerEnterTimeStarted);
    booking.setDriverEnterOtpTimePostTrip(driverEnterOtpTimePostTrip);
    booking.setOdometerEnding(odometerEnding);
    booking.setOdoometerEnterTimeEnding(odoometerEnterTimeEnding);

    bookingRepo.save(booking);

    return "Booking created successfully!";
}


    @PostMapping("/c")
    public Booking creatBooking(@RequestBody Booking b){
        return this.bookingRepo.save(b);
    }

    @GetMapping("/get/{sourceCity}/{sourceState}/{destinationCity}/{destinationState}")
    public List<onewayTrip> getDate(@PathVariable String sourceCity, @PathVariable String sourceState,
            @PathVariable String destinationState, @PathVariable String destinationCity) {
        return this.tripSer.getAllData(sourceCity, sourceState, destinationState, destinationCity);

    }

    @GetMapping("/oneWay/{pickupLocation}/{dropLocation}")
    public List<onewayTrip> getoneWayTripData(
            @PathVariable String pickupLocation,
            @PathVariable String dropLocation) {
        return tripSer.getoneWayTripData(pickupLocation, dropLocation);
    }

    @GetMapping("/roundTrip/{pickupLocation}/{dropLocation}")
    public List<roundTrip> getRoundWayTripData(
            @PathVariable String pickupLocation,
            @PathVariable String dropLocation) {
        return tripSer.getRoundWayTripData(pickupLocation, dropLocation);
    }

    @PostMapping("/rounprice")
    public ResponseEntity<roundTrip> createRoundWayTripPrice(
            @RequestParam String sourceState,
            @RequestParam String destinationState,
            @RequestParam String sourceCity,
            @RequestParam String destinationCity,
            @RequestParam int hatchbackPrice,
            @RequestParam int sedanPrice,
            @RequestParam int sedanPremiumPrice,
            @RequestParam int suvPrice,
            @RequestParam int suvPlusPrice,
            @RequestParam int ertiaga,
            @RequestParam(required = false, defaultValue = "s") String status) {

        // Call the service method which contains your provided code.
        roundTrip savedTrip = tripSer.postRoundTripprice(
                sourceState,
                destinationState,
                sourceCity,
                destinationCity,
                hatchbackPrice,
                sedanPrice,
                sedanPremiumPrice,
                suvPrice,
                suvPlusPrice,
                status,
                ertiaga);
        return ResponseEntity.ok(savedTrip);
    }

    @GetMapping("/oneWay2/{pickupLocation}/{dropLocation}")
    public List<onewayTrip> getoneWayTripData2(
            @PathVariable String pickupLocation,
            @PathVariable String dropLocation) {
        return tripSer.getoneWayTripData(pickupLocation, dropLocation);
    }

    @PostMapping("/oneprice")
    public ResponseEntity<onewayTrip> createOneWayTripPrice(
            @RequestParam String sourceState,
            @RequestParam String destinationState,
            @RequestParam String sourceCity,
            @RequestParam String destinationCity,
            @RequestParam int hatchbackPrice,
            @RequestParam int sedanPrice,
            @RequestParam int sedanPremiumPrice,
            @RequestParam int suvPrice,
            @RequestParam int suvPlusPrice,
            @RequestParam(required = false, defaultValue = "s") String status) {

        onewayTrip savedTrip = tripSer.postOneWayTripprice(
                sourceState,
                destinationState,
                sourceCity,
                destinationCity,
                hatchbackPrice,
                sedanPrice,
                sedanPremiumPrice,
                suvPrice,
                suvPlusPrice,
                status);

        return ResponseEntity.ok(savedTrip);
    }

    // @GetMapping("/{vendorId}/length/vendorByBookings")
    // public ResponseEntity<List<Booking>> getBookingsByVendorLength(@PathVariable
    // Long vendorId) {
    // List<Booking> bookings = bookingService.getBookingByVendor(vendorId);
    // return bookings.length();

    // }

    // Excel code

    @PutMapping("/{bookingId}/assignCabAdmin/{cabAdminId}")
    public ResponseEntity<Booking> assignCabAdminToBooking(@PathVariable int bookingId, @PathVariable Long cabAdminId) {

        Booking updatedBooking = bookingService.assignCabAdminToBooking(cabAdminId, bookingId);

        if (updatedBooking == null) {
            // If the booking or vendor was not found, return a 404 Not Found
            return ResponseEntity.notFound().build();
        }

        if (updatedBooking.getCabAdmin() == null || updatedBooking.getDriveAdmin() == null) {
            System.out.println("Vendor is not assigned");
        } else {
            String subject = "Booking Confirmation - " + updatedBooking.getBookid();
            String message = "<!DOCTYPE html>"
                    + "<html lang='en'>"
                    + "<head>"
                    + "<meta charset='UTF-8'>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Booking Confirmation</title>"
                    + "</head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f7f7f7; margin: 0; padding: 0;'>"
                    + "<div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); overflow: hidden;'>"
                    + "<div style='background-color: #007BFF; color: #ffffff; padding: 20px; text-align: center;'>"
                    + "<h1 style='margin: 0; font-size: 24px; font-weight: bold;'>Booking Confirmation</h1>"
                    + "</div>"
                    + "<div style='padding: 20px;'>"
                    + "<h3 style='color: #007BFF; font-size: 20px; margin-bottom: 20px;'>Hello "
                    + updatedBooking.getName() + ",</h3>"
                    + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-bottom: 20px;'>Your booking has been confirmed. Below are the details of your booking:</p>"
                    + "<div style='margin-top: 20px;'>"
                    + "<ul style='list-style-type: none; padding: 0;'>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Booking ID:</strong> "
                    + updatedBooking.getBookid() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Pickup Location:</strong> "
                    + updatedBooking.getUserPickup() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Drop Location:</strong> "
                    + updatedBooking.getUserDrop() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Trip Type:</strong> "
                    + updatedBooking.getTripType() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Date:</strong> "
                    + updatedBooking.getDate() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Time:</strong> "
                    + updatedBooking.getTime() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Amount Paid:</strong> ₹"
                    + updatedBooking.getAmount() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Cab Name:</strong> "
                    + updatedBooking.getVendorCab().getCarName() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Vehicle No:</strong> "
                    + updatedBooking.getVendorCab().getVehicleNo() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver Name:</strong> "
                    + updatedBooking.getVendorDriver().getDriverName() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver Contact:</strong> "
                    + updatedBooking.getVendorDriver().getContactNo() + "</li>"
                    + "</ul>"
                    + "</div>"
                    + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-top: 20px;'>Thank you for choosing us! We wish you a safe and pleasant journey.</p>"
                    + "</div>"
                    + "<div style='text-align: center; padding: 20px; background-color: #f1f1f1; color: #777777; font-size: 14px;'>"
                    + "<p style='margin: 0;'>If you have any questions, feel free to contact us at <a href='mailto:support@example.com' style='color: #007BFF; text-decoration: none;'>support@example.com</a>.</p>"
                    + "<img src='https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExcjc1OGk0ZGVqNHFseDRrM3FvOW0xYnVyenJkcmQ2OXNsODE0djUzZyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/3oKIPhUfA1h2U2Koko/giphy.gif' alt='Namaskar' style='width: 100px; height: auto; margin-top: 10px;'>"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            boolean emailSent = emailService.sendEmail(message, subject, updatedBooking.getEmail());

            if (emailSent) {
                System.out.println("Booking confirmation email sent successfully.");
            } else {
                System.out.println("Failed to send booking confirmation email.");
            }
        }

        return ResponseEntity.ok(updatedBooking);

    }

    @PutMapping("/{bookingId}/assignDriveAdmin/{driveAdminId}")
    public ResponseEntity<Booking> assignDriveAdminToBooking(@PathVariable int bookingId,
            @PathVariable int driveAdminId) {

        Booking updatedBooking = bookingService.assignDriveAdminToBooking(driveAdminId, bookingId);

        if (updatedBooking == null) {
            // If the booking or vendor was not found, return a 404 Not Found
            return ResponseEntity.notFound().build();
        }

        if (updatedBooking.getCabAdmin() == null || updatedBooking.getDriveAdmin() == null) {
            System.out.println("Vendor is not assigned");
        } else {
            String subject = "Booking Confirmation - " + updatedBooking.getBookid();
            String message = "<!DOCTYPE html>"
                    + "<html lang='en'>"
                    + "<head>"
                    + "<meta charset='UTF-8'>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Booking Confirmation</title>"
                    + "</head>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #f7f7f7; margin: 0; padding: 0;'>"
                    + "<div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); overflow: hidden;'>"
                    + "<div style='background-color: #007BFF; color: #ffffff; padding: 20px; text-align: center;'>"
                    + "<h1 style='margin: 0; font-size: 24px; font-weight: bold;'>Booking Confirmation</h1>"
                    + "</div>"
                    + "<div style='padding: 20px;'>"
                    + "<h3 style='color: #007BFF; font-size: 20px; margin-bottom: 20px;'>Hello "
                    + updatedBooking.getName() + ",</h3>"
                    + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-bottom: 20px;'>Your booking has been confirmed. Below are the details of your booking:</p>"
                    + "<div style='margin-top: 20px;'>"
                    + "<ul style='list-style-type: none; padding: 0;'>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Booking ID:</strong> "
                    + updatedBooking.getBookid() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Pickup Location:</strong> "
                    + updatedBooking.getUserPickup() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Drop Location:</strong> "
                    + updatedBooking.getUserDrop() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Trip Type:</strong> "
                    + updatedBooking.getTripType() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Date:</strong> "
                    + updatedBooking.getDate() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Time:</strong> "
                    + updatedBooking.getTime() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Amount Paid:</strong> ₹"
                    + updatedBooking.getAmount() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Cab Name:</strong> "
                    + updatedBooking.getVendorCab().getCarName() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Vehicle No:</strong> "
                    + updatedBooking.getVendorCab().getVehicleNo() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver Name:</strong> "
                    + updatedBooking.getVendorDriver().getDriverName() + "</li>"
                    + "<li style='margin-bottom: 10px; font-size: 14px; color: #555555;'><strong style='color: #007BFF;'>Driver Contact:</strong> "
                    + updatedBooking.getVendorDriver().getContactNo() + "</li>"
                    + "</ul>"
                    + "</div>"
                    + "<p style='font-size: 16px; line-height: 1.5; color: #333333; margin-top: 20px;'>Thank you for choosing us! We wish you a safe and pleasant journey.</p>"
                    + "</div>"
                    + "<div style='text-align: center; padding: 20px; background-color: #f1f1f1; color: #777777; font-size: 14px;'>"
                    + "<p style='margin: 0;'>If you have any questions, feel free to contact us at <a href='mailto:support@example.com' style='color: #007BFF; text-decoration: none;'>support@example.com</a>.</p>"
                    + "<img src='https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExcjc1OGk0ZGVqNHFseDRrM3FvOW0xYnVyenJkcmQ2OXNsODE0djUzZyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/3oKIPhUfA1h2U2Koko/giphy.gif' alt='Namaskar' style='width: 100px; height: auto; margin-top: 10px;'>"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            boolean emailSent = emailService.sendEmail(message, subject, updatedBooking.getEmail());

            if (emailSent) {
                System.out.println("Booking confirmation email sent successfully.");
            } else {
                System.out.println("Failed to send booking confirmation email.");
            }
        }

        return ResponseEntity.ok(updatedBooking);

    }

    @GetMapping("/send-sms")
    public String sendSms(
            @RequestParam String phoneNumber,
            @RequestParam String carrier,
            @RequestParam String message) { // Add the 'message' parameter
        boolean isSent = smsService.sendSms(phoneNumber, carrier, message); // Pass all three parameters
        if (isSent) {
            return "SMS sent successfully!";
        } else {
            return "Failed to send SMS.";
        }
    }

    // send manual

    @PostMapping("/send-manual/{email:.+}")
    public ResponseEntity<?> sendManualEmailWithPath(@PathVariable("email") String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("jaywantmhala928@gmail.com"); // Use your sender email
            helper.setTo(email);
            helper.setSubject("Vendor Registration - Please Fill the Details");

            // HTML formatted email content
            String emailContent = "<html><body>" +
                    "<p>Hi,</p>" +
                    "<p>Please click on the following link to fill in the vendor registration details:</p>" +
                    "<p><a href='http://192.168.231.233:3001/vendor-registration'  " +
                    "style='color: #007bff; text-decoration: none; font-weight: bold;'>Complete Vendor Registration</a></p>"
                    +
                    "<p>Thank you.</p>" +
                    "<p>Best Regards,<br>WTL Tourism Pvt. Ltd.</p>" +
                    "</body></html>";

            helper.setText(emailContent, true); // Enable HTML content
            mailSender.send(mimeMessage); // Send email

            return ResponseEntity.ok("Email sent successfully");
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + ex.getMessage());
        }

    }

    @PostMapping("/validate")
    public Map<String, Object> bookingValidated(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email) {
        return bookingService.bookingValidated(name, phone, email);
    }


    @PutMapping("/updateBooking/{id}")
    public ResponseEntity<BookingDTO> updateBookingById(@PathVariable int id, @RequestBody BookingDTO bookingDTO) {
        // Debug: Log incoming DTO
        System.out.println("[DEBUG] updateBookingById called for id=" + id);
        System.out.println("[DEBUG] Incoming BookingDTO: " + bookingDTO);
        try {
            BookingDTO updatedBooking = bookingService.updateBooking(id, bookingDTO);
            if (updatedBooking != null) {
                System.out.println("[DEBUG] Booking updated successfully: " + updatedBooking);
                return ResponseEntity.ok(updatedBooking); // Return HTTP 200 OK with the updated booking DTO
            } else {
                System.out.println("[DEBUG] Booking not found for id=" + id);
                return ResponseEntity.notFound().build(); // Return HTTP 404 if booking is not found
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Exception in updateBookingById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    
    
}
