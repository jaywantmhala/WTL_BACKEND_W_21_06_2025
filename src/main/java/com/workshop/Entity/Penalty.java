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
import jakarta.persistence.OneToOne;

@Entity
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int pId;

    private String reason;

    private int amount;

    @JsonBackReference
    @JoinColumn(name = "vendor_id")
    @ManyToOne
    private Vendor vendor;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    public Penalty(int pId, String reason, int amount, Vendor vendor, Booking booking) {
        this.pId = pId;
        this.reason = reason;
        this.amount = amount;
        this.vendor = vendor;
        this.booking = booking;
    }

    public Penalty() {
        super();
    }

    public Penalty(Booking booking2, Long vendorId, int i, String string) {
        // TODO Auto-generated constructor stub
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
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

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

}
