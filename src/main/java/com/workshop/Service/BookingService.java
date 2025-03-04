package com.workshop.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.DTO.BookingDTO;
import com.workshop.Entity.Booking;
import com.workshop.Entity.Vendor;
import com.workshop.Entity.VendorCabs;
import com.workshop.Entity.VendorDrivers;
import com.workshop.Repo.BookingRepo;
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

}
