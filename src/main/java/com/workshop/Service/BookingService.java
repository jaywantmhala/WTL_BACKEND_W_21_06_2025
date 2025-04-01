package com.workshop.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

import com.workshop.CarRental.Entity.CarRentalUser;
import com.workshop.CarRental.Repository.CarRentalRepository;
import com.workshop.DTO.BookingDTO;
import com.workshop.DTO.CancellationResult;
import com.workshop.Entity.Booking;
import com.workshop.Entity.CabAdmin;
import com.workshop.Entity.DriveAdmin;
import com.workshop.Entity.Penalty;
import com.workshop.Entity.Vendor;
import com.workshop.Entity.VendorCabs;
import com.workshop.Entity.VendorDrivers;
import com.workshop.Repo.BookingRepo;
import com.workshop.Repo.CabAdminRepo;
import com.workshop.Repo.DriverAdminRepo;
import com.workshop.Repo.PenaltyRepo;
import com.workshop.Repo.VendorCabRepo;
import com.workshop.Repo.VendorDriverRepo;
import com.workshop.Repo.VendorRepository;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    @Autowired
    BookingRepo repo;

    @Autowired
    private VendorRepository vendorRepo;

    @Autowired
    private VendorCabRepo vendorCabRepo;

    @Autowired
    private VendorDriverRepo vendorDriverRepo;

    @Autowired
    private PenaltyRepo penaltyRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CabAdminRepo cabAdminRepo;

    @Autowired
    private DriverAdminRepo driverAdminRepo;

    @Autowired
    private CarRentalRepository carRentalRepository;


    private static final String API_KEY = "AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w"; // Replace with a secure API key


    public void saveBooking(Booking booking) {
        repo.save(booking);
    }

    public String getLastUsedBookingId() {
        // Fetch the maximum booking ID from the database
        String lastUsedId = repo.findMaxBookingId();

        // If lastUsedId is null, return 0
        if (lastUsedId == null) {
            return "";
        }

        return lastUsedId;
    }

    public List<Booking> getBooking(String userid) {
        return repo.findByUserId(userid);
    }

    public List<Booking> getAll() {
        return repo.findAll();
    }

    // public Booking findBookingbyId(int id) {
    // // Fetch the booking from the repository by ID with its associated vendor
    // (EAGER
    // // fetch)
    // Booking b = this.repo.findById(id).orElse(null);

    // // Check if the booking exists
    // if (b != null) {
    // // Return the booking with the vendor included
    // return b;
    // } else {
    // // If no booking is found, return null
    // return null;
    // }
    // }

    public BookingDTO getBooking(int id) {
        Booking booking = this.repo.findById(id).orElse(null);

        // Return the DTO if booking is found
        if (booking != null) {
            return new BookingDTO(booking); // Convert to DTO
        }
        return null; // If not found, return null
    }

    public List<Booking> getAllBookings() {
        return repo.findAllByOrderByIdDesc();
    }

    public void deleteBooking(int id) {
        this.repo.deleteById(id);
    }

    // public void deleteBooking1(int id) {
    // repo.delete(id);

    // }

    @Transactional
    public String deleteBookingByBookingId(String bookingId) {
        // Check if the booking exists before attempting to delete
        // System.out.println(bookingId);
        if (repo.existsByBookingId(bookingId)) {
            repo.deleteByBookingId(bookingId); // Use the custom delete method
            return "Booking with ID " + bookingId + " has been deleted successfully.";
        } else {
            return "Booking with ID " + bookingId + " not found.";
        }
    }

    public Booking updateStatus(int id, int newStatus) {
        Booking b = this.repo.findById(id).get();
        b.setStatus(newStatus);
        return this.repo.save(b);
    }

    public List<Booking> getBookingByStatus(int status) {
        return this.repo.findAll()
                .stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList());
    }

    public Booking assignVendorToBooking(int bookingId, Long vendorId) {
        // Step 1: Fetch the existing booking from the database using the bookingId
        Optional<Booking> bookingOptional = this.repo.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get(); // Get the existing booking

            // Step 2: Fetch the vendor by vendorId
            Optional<Vendor> vendorOptional = this.vendorRepo.findById(vendorId);

            if (vendorOptional.isPresent()) {
                Vendor vendor = vendorOptional.get(); // Get the existing vendor

                // Step 3: Assign the vendor to the booking
                booking.setVendor(vendor);

                // Step 4: Save the updated booking (the vendor is now assigned to this specific
                // booking row)
                return this.repo.save(booking);
            } else {
                // Vendor not found, returning null or can log the error if necessary
                System.out.println("Vendor with ID " + vendorId + " not found.");
                return null;
            }
        } else {
            // Booking not found, returning null or can log the error if necessary
            System.out.println("Booking with ID " + bookingId + " not found.");
            return null;
        }
    }

    public List<Booking> getBookingByVendor(Long vendorId) {
        return repo.findByVendorId(vendorId);
    }

    public Booking assignVendorCabToBooking(int bookingId, int vendorCabId) {
        // Step 1: Fetch the existing booking from the database using the bookingId
        Optional<Booking> bookingOptional = this.repo.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get(); // Get the existing booking

            // Step 2: Fetch the vendor by vendorId
            Optional<VendorCabs> vendorOptional = this.vendorCabRepo.findById(vendorCabId);

            if (vendorOptional.isPresent()) {
                VendorCabs vendor = vendorOptional.get(); // Get the existing vendor

                // Step 3: Assign the vendor to the booking
                booking.setVendorCab(vendor);

                // Step 4: Save the updated booking (the vendor is now assigned to this specific
                // booking row)
                return this.repo.save(booking);
            } else {
                // Vendor not found, returning null or can log the error if necessary
                System.out.println("Vendor with ID " + vendorCabId + " not found.");
                return null;
            }
        } else {
            // Booking not found, returning null or can log the error if necessary
            System.out.println("Booking with ID " + bookingId + " not found.");
            return null;
        }
    }

    public Booking assignVendorDriverToBooking(int bookingId, int vendorDriverId) {
        // Step 1: Fetch the existing booking from the database using the bookingId
        Optional<Booking> bookingOptional = this.repo.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get(); // Get the existing booking

            // Step 2: Fetch the vendor by vendorId
            Optional<VendorDrivers> vendorOptional = this.vendorDriverRepo.findById(vendorDriverId);

            if (vendorOptional.isPresent()) {
                VendorDrivers vendor = vendorOptional.get(); // Get the existing vendor

                // Step 3: Assign the vendor to the booking
                booking.setVendorDriver(vendor);

                // Step 4: Save the updated booking (the vendor is now assigned to this specific
                // booking row)
                return this.repo.save(booking);
            } else {
                // Vendor not found, returning null or can log the error if necessary
                System.out.println("Vendor with ID " + vendorDriverId + " not found.");
                return null;
            }
        } else {
            // Booking not found, returning null or can log the error if necessary
            System.out.println("Booking with ID " + bookingId + " not found.");
            return null;
        }
    }

    public Booking createCustomBooking(Booking b) {
        return repo.save(b);
    }


    public Penalty createPenalty(int bookingId, Long vendorId, Penalty penalty){
      Booking b = this.repo.findById(bookingId).orElse(null);
      Vendor v = this.vendorRepo.findById(vendorId).orElse(null);
      b.setVendorCab(null);
      b.setVendorDriver(null);
    //   b.setVendor(null);

      String subject = "For booking Cancellation Penalty - " + b.getUserPickup() + " "+b.getUserDrop();
      String message = "<h3>Hello " + v.getVendorCompanyName() + ",</h3>" +
              "<p>Your booking has been has been cancelled.</p>" +
              "<p><strong>Booking Details:</strong></p>" +
              "<ul>" +
              "<li><strong>Booking ID:</strong> " + b.getBookid() + "</li>" +
              "<li><strong>Pickup Location:</strong> " + b.getUserPickup() + "</li>" +
              "<li><strong>Drop Location:</strong> " + b.getUserDrop() + "</li>" +
              "<li><strong>Trip Type:</strong> " + b.getTripType() + "</li>" +
              "<li><strong>Date:</strong> " + b.getDate() + "</li>" +
              "<li><strong>Time:</strong> " + b.getTime() + "</li>" +
              "<li><strong>Fine: </strong>₹ " + 2000 + "</li>" +
              "</ul>" +
              "<p>Thank you for choosing us!</p>";

      boolean emailSent = emailService.sendEmail(message, subject, v.getVendorEmail());

      if (emailSent) {
          System.out.println("Booking confirmation email sent successfully.");
      } else {
          System.out.println("Failed to send booking confirmation email.");
      }


      penalty.setVendor(v);
      penalty.setBooking(b);
      penalty.setDate(LocalDate.now()); 
       return this.penaltyRepo.save(penalty);
    }


    public Booking assignCabAdminToBooking(Long cabAdminId, int bookingId){
        Booking existingbooking = this.repo.findById(bookingId).orElse(null);
        CabAdmin cabAdmin = this.cabAdminRepo.findById(cabAdminId).orElse(null);
        existingbooking.setVendor(null);
        existingbooking.setVendorCab(null);
        existingbooking.setVendorDriver(null);


        existingbooking.setCabAdmin(cabAdmin);
        return this.repo.save(existingbooking);

    }


    public Booking assignDriveAdminToBooking(int driveAdminId, int bookingId){
        Booking booking = this.repo.findById(bookingId).orElse(null);
        DriveAdmin driveAdmin = this.driverAdminRepo.findById(driveAdminId).orElse(null);
        booking.setVendor(null);
        booking.setVendorCab(null);
        booking.setVendorDriver(null);
        booking.setDriveAdmin(driveAdmin);
        return this.repo.save(booking);

    }


    public List<Booking> getBookingsByVendorDriverId(int vendorDriverId) {
        // Choose one of these methods based on your needs:
        return repo.findByVendorDriverVendorDriverId(vendorDriverId);
        // return bookingRepository.findByVendorDriverIdWithUser(vendorDriverId);
        // return bookingRepository.findByVendorDriverIdWithAllRelations(vendorDriverId);
    }


    public List<Booking> getBookingByCarRentalUserId(int id){
        return repo.findByCarRentalUserId(id);
    }


    //  for user current location


    public CarRentalUser saveUserLocation(int id) {
        String url = "https://www.googleapis.com/geolocation/v1/geolocate?key=" + API_KEY;
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("considerIp", true);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(url, requestBody, String.class);

        if (response != null && !response.isEmpty()) {
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("location")) {
                JSONObject location = jsonResponse.getJSONObject("location");
                double latitude = location.optDouble("lat", 0.0);
                double longitude = location.optDouble("lng", 0.0);

                CarRentalUser carRentalUser = carRentalRepository.findById(id).orElse(null);
                if (carRentalUser != null) {
                    carRentalUser.setUserlatitude(latitude);
                    carRentalUser.setUserlongitude(longitude);
                    return carRentalRepository.save(carRentalUser);
                }
            }
        }

        return null; // Return null if location is not found
    } 
}
