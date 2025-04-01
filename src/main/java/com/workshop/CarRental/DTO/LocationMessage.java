package com.workshop.CarRental.DTO;

import java.util.Date;

public class LocationMessage {
    private Double latitude;
    private Double longitude;
    private int userId;
    private String role;
    private Date timestamp;


    public LocationMessage(){

    }

    public LocationMessage(Double latitude, Double longitude, int userId, String role, Date timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.role = role;
        this.timestamp = timestamp;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // Constructors, getters, setters

    
}
