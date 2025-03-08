package com.workshop.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.Entity.Booking;
import com.workshop.Entity.Penalty;
import com.workshop.Entity.Vendor;
import com.workshop.Repo.BookingRepo;
import com.workshop.Repo.PenaltyRepo;
import com.workshop.Repo.VendorRepository;

@Service
public class PenaltyService {

    @Autowired
    private PenaltyRepo penaltyRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private VendorRepository vendorRepo;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Penalty createPenalty(int bookingId, Long vendorId) {
        // Fetch booking details
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + vendorId));

        // Get current date and time
        LocalDate cancelDate = LocalDate.now();
        LocalTime cancelTime = LocalTime.now();

        // Get booking date and time
        LocalDate bookingDate = booking.getStartDate();
        LocalTime bookingTime = LocalTime.parse(booking.getTime(), TIME_FORMATTER);

        int penaltyAmount = 0;
        StringBuilder reason = new StringBuilder();

        // Check if cancellation is on the same day
        if (cancelDate.isEqual(bookingDate)) {
            Duration duration = Duration.between(cancelTime, bookingTime);
            long minutesDifference = Math.abs(duration.toMinutes());

            if (minutesDifference <= 60) {
                penaltyAmount += 2000;
                reason.append("Booking Change Penalty (₹2000), ");
            }
            if (cancelTime.isAfter(bookingTime)) {
                penaltyAmount += 500;
                reason.append("Late Cancellation Penalty (₹500), ");
            }
        } else if (cancelDate.isAfter(bookingDate)) {
            penaltyAmount += 500;
            reason.append("Late Cancellation (Next Day) Penalty (₹500), ");
        } else if (!cancelDate.isAfter(bookingDate.minusDays(1))) {
            reason.append("Cancellation allowed without penalty.");
        }

        // Create and save penalty
        Penalty penalty = new Penalty();
        penalty.setBooking(booking);
        penalty.setVendor(vendor);
        penalty.setAmount(penaltyAmount);
        penalty.setReason(reason.toString().trim());

        return penaltyRepo.save(penalty);
    }
}
