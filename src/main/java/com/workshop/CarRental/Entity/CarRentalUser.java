package com.workshop.CarRental.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.workshop.Entity.Booking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class CarRentalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String username;

    private String lastName;

    private String email;

    private String password;

    private String phone;

    private String gender;

    private float latitude;

    private float longitude;

    private String address;

    private String role="USER";

    // private String city;

    // private String state;

    // @JsonManagedReference
    @JsonIgnore
    @OneToMany(mappedBy = "carRentalUser")
    private List<Booking> bookings;

    public CarRentalUser(int id, String username, String lastName, String email, String password, String phone, float latitude,
            float longitude, String address, List<Booking> bookings, String role, String gender) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.bookings = bookings;
        this.role=role;
        this.lastName=lastName;
        this.gender=gender;
    }


    public CarRentalUser() {
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public float getLatitude() {
        return latitude;
    }


    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }


    public float getLongitude() {
        return longitude;
    }


    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public List<Booking> getBookings() {
        return bookings;
    }


    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    
    
    



    
}
