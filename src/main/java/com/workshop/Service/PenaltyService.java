package com.workshop.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.Entity.Penalty;
import com.workshop.Repo.PenaltyRepo;
import com.workshop.Repo.VendorRepository;

@Service
public class PenaltyService {

    @Autowired
    private PenaltyRepo penaltyRepo;

    @Autowired
    private VendorRepository vendorRepository;

    public Penalty createPenalty(Penalty penalty, Long vendorId) {
        return this.penaltyRepo.save(penalty);
    }
}
