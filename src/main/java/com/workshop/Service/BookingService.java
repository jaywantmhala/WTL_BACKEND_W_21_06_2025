package com.workshop.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
import com.workshop.DTO.CabAdminDTO;
import com.workshop.DTO.CancellationResult;
import com.workshop.DTO.DriverAdminDTO;
import com.workshop.DTO.VendorCabsDTO;
import com.workshop.DTO.VendorDTO;
import com.workshop.DTO.VendorDriversDTO;
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


    // enter otp time trip start hone se pahle
    public Booking updateDriverEnterOtpTimePreStarted(int id){
        Booking booking = this.repo.findById(id).orElse(null);
        
        LocalDateTime now = LocalDateTime.now();
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = now.format(formatter);

        booking.setDriverEnterOtpTimePreStarted(formattedDateTime);
        return this.repo.save(booking);
    }

    // driver enter otp trip end htoe time

    public Booking updateDriverEnterOtpTimePostStarted(int id){

        Booking booking = this.repo.findById(id).orElse(null);
        
        LocalDateTime now = LocalDateTime.now();
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = now.format(formatter);
        booking.setDriverEnterOtpTimePostTrip(formattedDateTime);
        return this.repo.save(booking);

    }


    //  starting odd meter and enter time
    public Booking enterOdooMeterStarted(int id, String meter){

        Booking booking = this.repo.findById(id).orElse(null);
        
        LocalDateTime now = LocalDateTime.now();
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = now.format(formatter);
        booking.setOdoometerEnterTimeStarted(formattedDateTime);
        booking.setOdoometerStarted(meter);
        return this.repo.save(booking);
 

    }


    // ending odoometer  and enter time
    public Booking enterOdoometerEnding(int id, String meter){
        Booking booking = this.repo.findById(id).orElse(null);
        
        LocalDateTime now = LocalDateTime.now();
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = now.format(formatter);
        booking.setOdoometerEnterTimeEnding(formattedDateTime);

        booking.setOdometerEnding(meter);
        return this.repo.save(booking);
    }

    public Booking updatePrice(int id, int amount){
        Booking b = this.repo.findById(id).orElse(null);
        b.setAmount(amount);
        return b;
    }




    public Map<String, Object> bookingValidated(String name, String phone, String email) {
        // Create new Booking object
        Booking booking = new Booking();
        booking.setName(name);
        booking.setPhone(phone);
        booking.setEmail(email);

        // Generate and set booking ID
        String bookid = "WTL" + System.currentTimeMillis();
        booking.setBookingId(bookid);
        booking.setBookid(bookid);

        // Save booking to database
        Booking savedBooking = repo.save(booking);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Booking saved successfully");
        response.put("booking", savedBooking);

        return response;
    }

    public BookingDTO updateBooking(int id, BookingDTO bookingDTO) {
        Optional<Booking> optionalBooking = repo.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            // Update fields from DTO to entity
            booking.setFromLocation(bookingDTO.getFromLocation());
            booking.setToLocation(bookingDTO.getToLocation());
            booking.setTripType(bookingDTO.getTripType());
            booking.setStartDate(bookingDTO.getStartDate());
            booking.setReturnDate(bookingDTO.getReturnDate());
            booking.setTime(bookingDTO.getTime());
            booking.setDistance(bookingDTO.getDistance());
            booking.setUserId(bookingDTO.getUserId());
            booking.setBookingId(bookingDTO.getBookingId());
            booking.setName(bookingDTO.getName());
            booking.setEmail(bookingDTO.getEmail());
            booking.setPhone(bookingDTO.getPhone());
            booking.setUserPickup(bookingDTO.getUserPickup());
            booking.setUserDrop(bookingDTO.getUserDrop());
            booking.setDate(bookingDTO.getDate());
            booking.setUserTripType(bookingDTO.getUserTripType());
            booking.setBookid(bookingDTO.getBookid());
            booking.setCar(bookingDTO.getCar());
            booking.setBaseAmount(bookingDTO.getBaseAmount());
            booking.setAmount(bookingDTO.getAmount());
            booking.setStatus(bookingDTO.getStatus());
            booking.setDriverBhata(bookingDTO.getDriverBhata());
            booking.setNightCharges(bookingDTO.getNightCharges());
            booking.setGst(bookingDTO.getGst());
            booking.setServiceCharge(bookingDTO.getServiceCharge());
            booking.setOffer(bookingDTO.getOffer());
            booking.setOfferPartial(bookingDTO.getOfferPartial());
            booking.setOfferAmount(bookingDTO.getOfferAmount());
            booking.setTxnId(bookingDTO.getTxnId());
            booking.setPayment(bookingDTO.getPayment());
            booking.setDateEnd(bookingDTO.getDateEnd());
            booking.setTimeEnd(bookingDTO.getTimeEnd());
            booking.setBookingType(bookingDTO.getBookingType());
            booking.setDescription(bookingDTO.getDescription());
            booking.setCollection(bookingDTO.getCollection());
            // Save updated booking
            Booking updated = repo.save(booking);
            return new BookingDTO(updated);
        }
        return null;
    }

     public List<BookingDTO> getBookingByCompanyName(String companyName) {
        List<Booking> bookings = repo.findByCompanyName(companyName);
        return bookings.stream()
                       .map(this::convertToDTO)
                       .collect(Collectors.toList());
    }


    public List<BookingDTO> getBookingByVendorId(Long vendorId) {
        List<Booking> bookings = repo.findByVendorId(vendorId);
        return bookings.stream()
                       .map(this::convertToDTO)
                       .collect(Collectors.toList());
    }

    

    public Set<String> getUniqueCompanyNames() {
        List<String> companyNames = repo.findAll()
            .stream()
            .map(Booking::getCompanyName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
            
        return new HashSet<>(companyNames);
    }
    

    

    private BookingDTO convertToDTO(Booking booking) {
    BookingDTO dto = new BookingDTO();
    
    dto.setId(booking.getId());
    dto.setFromLocation(booking.getFromLocation());
    dto.setToLocation(booking.getToLocation());
    dto.setTripType(booking.getTripType());
    dto.setStartDate(booking.getStartDate());
    dto.setReturnDate(booking.getReturnDate());
    dto.setTime(booking.getTime());
    dto.setDistance(booking.getDistance());
    dto.setUserId(booking.getUserId());
    dto.setBookingId(booking.getBookingId());
    dto.setName(booking.getName());
    dto.setEmail(booking.getEmail());
    dto.setPhone(booking.getPhone());
    dto.setUserPickup(booking.getUserPickup());
    dto.setUserDrop(booking.getUserDrop());
    dto.setDate(booking.getDate());
    dto.setUserTripType(booking.getUserTripType());
    dto.setBookid(booking.getBookid());
    dto.setCar(booking.getCar());
    dto.setBaseAmount(booking.getBaseAmount());
    dto.setAmount(booking.getAmount());
    dto.setStatus(booking.getStatus());
    dto.setDriverBhata(booking.getDriverBhata());
    dto.setNightCharges(booking.getNightCharges());
    dto.setGst(booking.getGst());
    dto.setServiceCharge(booking.getServiceCharge());
    dto.setOffer(booking.getOffer());
    dto.setOfferPartial(booking.getOfferPartial());
    dto.setOfferAmount(booking.getOfferAmount());
    dto.setTxnId(booking.getTxnId());
    dto.setPayment(booking.getPayment());
    dto.setDateEnd(booking.getDateEnd());
    dto.setTimeEnd(booking.getTimeEnd());
    dto.setBookingType(booking.getBookingType());
    dto.setDescription(booking.getDescription());
    dto.setCompanyName(booking.getCompanyName());
    dto.setCollection(booking.getCollection());
    
    if (booking.getVendor() != null) {
        dto.setVendor(new VendorDTO(booking.getVendor()));
        dto.setVendorName(booking.getVendor().getVendorCompanyName());
    }
    
    if (booking.getVendorCab() != null) {
        dto.setVendorCab(new VendorCabsDTO(booking.getVendorCab()));
        dto.setVendorCabName(booking.getVendorCab().getCarName());
        dto.setCabPlateNo(booking.getVendorCab().getVehicleNo());
    }
    
    if (booking.getVendorDriver() != null) {
        dto.setVendorDriver(new VendorDriversDTO(booking.getVendorDriver()));
        dto.setVendorDriverName(booking.getVendorDriver().getDriverName());
    }
    
    if (booking.getCabAdmin() != null) {
        dto.setCabAdmin(new CabAdminDTO(booking.getCabAdmin()));
        dto.setMasterAdminCabName(booking.getCabAdmin().getVehicleNameAndRegNo());
        dto.setMasterAdminCabNoPlate(booking.getCabAdmin().getVehicleRcNo());
    }
    
    if (booking.getDriveAdmin() != null) {
        dto.setDriverAdmin(new DriverAdminDTO(booking.getDriveAdmin()));
        dto.setMasterAdminDriverName(booking.getDriveAdmin().getDriverName());
    }
    
    return dto;
}


public Map<Long, String> getAllVendorCompanyName() {
    List<Vendor> vendor = this.vendorRepo.findAll();
    return vendor.stream()
                 .collect(Collectors.toMap(
                     Vendor::getId,     
                     Vendor::getVendorCompanyName,  
                     (existing, replacement) -> existing 
                 ));
}

    
}
