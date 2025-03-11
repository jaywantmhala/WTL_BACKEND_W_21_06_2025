package com.workshop.Service;

import org.springframework.stereotype.Service;

import com.workshop.DTO.VendorLoginResponse;
import com.workshop.Entity.Booking;
import com.workshop.Entity.Vendor;
import com.workshop.Repo.VendorRepository;
import java.util.List;
import java.util.Optional;

@Service
public class VendorService {
    private final VendorRepository repository;

    public VendorService(VendorRepository repository) {
        this.repository = repository;
    }

    // ✅ Save Vendor
    public Vendor saveVendor(Vendor vendor) {
        return repository.save(vendor);
    }

    // ✅ Get All Vendors
    public List<Vendor> getAllVendors() {
        return repository.findAll();
    }

    // ✅ Get Vendor by ID
    public Vendor getVendorById(Long id) {
        return repository.findById(id).orElse(null);
    }

    // ✅ Get Vendor by Company Name
    public Vendor getVendorByCompanyName(String vendorCompanyName) {
        return repository.findByVendorCompanyName(vendorCompanyName).orElse(null);
    }

    // // ✅ Get Vendor by Email (NEW METHOD)
    // public Vendor getVendorByEmail(String email) {
    // return repository.findByVendorEmail(email).orElse(null);
    // }

    // ✅ Delete Vendor
    public String deleteVendor(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "Vendor deleted successfully";
        }
        return "Vendor not found";
    }

    public VendorLoginResponse vendorLogin(String email, String password) {
        Vendor vendor = repository.findByVendorEmail(email); // Find vendor by email

        if (vendor != null && password.equals(vendor.getPassword())) { // Check if passwords match
            // Return VendorLoginResponse with the desired fields
            return new VendorLoginResponse(vendor.getId(), vendor.getVendorEmail(), vendor.getPassword());
        } else {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    // public List<Booking> getBookingsByVendor(Long vendorId) {
    // Optional<Vendor> vendorOpt = repository.findById(vendorId);
    // if (vendorOpt.isPresent()) {
    // return vendorOpt.get().getBooking();
    // } else {
    // throw new RuntimeException("Vendor not found with id " + vendorId);
    // }
    // }

    // public List<Booking> getBookingByVendor(Long vendorId) {
    // return repository.findByVendorId(vendorId);
    // }

    

}
