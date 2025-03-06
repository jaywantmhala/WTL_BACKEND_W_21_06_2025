package com.workshop.Entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID pId;

    private String reason;

    private int amount;

    @JsonBackReference
    @JoinColumn(name = "vendor_id")
    @ManyToOne
    private Vendor vendor;

    public Penalty(UUID pId, String reason, int amount, Vendor vendor) {
        this.pId = pId;
        this.reason = reason;
        this.amount = amount;
        this.vendor = vendor;
    }

    public Penalty() {
        super();
    }

    public UUID getpId() {
        return pId;
    }

    public void setpId(UUID pId) {
        this.pId = pId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

}
