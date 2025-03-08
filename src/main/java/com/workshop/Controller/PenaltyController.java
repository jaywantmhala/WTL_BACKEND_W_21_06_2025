package com.workshop.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.workshop.Entity.Penalty;
import com.workshop.Entity.Vendor;
import com.workshop.Repo.VendorRepository;
import com.workshop.Service.PenaltyService;

@RestController
public class PenaltyController {

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private VendorRepository vendorRepository;

    @PostMapping("/penalty/{bookingId}/{vendorId}")
    public ResponseEntity<Penalty> createPenalty(
            @PathVariable int bookingId,
            @PathVariable Long vendorId) {

        Penalty createdPenalty = penaltyService.createPenalty(bookingId, vendorId);
        return ResponseEntity.ok(createdPenalty);
    }

}
