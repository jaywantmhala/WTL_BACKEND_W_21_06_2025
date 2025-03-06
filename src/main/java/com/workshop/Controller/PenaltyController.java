package com.workshop.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workshop.Entity.Penalty;
import com.workshop.Entity.Vendor;
import com.workshop.Repo.VendorRepository;
import com.workshop.Service.PenaltyService;

@RestController
@RequestMapping("/penalty")
public class PenaltyController {

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private VendorRepository vendorRepository;

    @PostMapping("/createPenalty/{vendorId}")
    public Penalty createPenalty(@RequestBody Penalty penalty, @PathVariable Long vendorId) {
        Vendor vendor = this.vendorRepository.findById(vendorId).orElse(null);
        penalty.setVendor(vendor);
        return this.penaltyService.createPenalty(penalty, vendorId);

    }

}
