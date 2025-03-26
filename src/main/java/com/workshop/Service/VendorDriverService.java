package com.workshop.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.Entity.VendorCabs;
import com.workshop.Entity.VendorDrivers;
import com.workshop.Repo.VendorDriverRepo;

@Service
public class VendorDriverService {

	
	@Autowired
	private VendorDriverRepo vendorDriverRepo;
	
	
	
	public VendorDrivers addVendorsDriver(VendorDrivers vendorDrivers) {
		return this.vendorDriverRepo.save(vendorDrivers);
	}
	
	public Optional<VendorDrivers> getVendorDriversById(int vendorDriverId) {
		return this.vendorDriverRepo.findById(vendorDriverId);
	}
	
	public List<VendorDrivers> getAllDrivers(){
		return this.vendorDriverRepo.findAll();
	}
	
	public List<VendorDrivers> getOrder(Long vendorId) {
	    return this.vendorDriverRepo.findByVendorId(vendorId);
	}

}
