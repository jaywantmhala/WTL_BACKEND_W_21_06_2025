package com.workshop.CarRental.DTO;

import java.time.LocalDateTime;

import lombok.Data;

public class LocationUpdate {
    private String userId;
    private String type; 
    private double latitude;
    private double longitude;
    private String tripId; 
    private LocalDateTime timestamp;


    public LocationUpdate(){
        super();
    }


    public LocationUpdate(String userId, String type, double latitude, double longitude, String tripId,
            LocalDateTime timestamp) {
        this.userId = userId;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tripId = tripId;
        this.timestamp = timestamp;
    }


    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public double getLatitude() {
        return latitude;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String getTripId() {
        return tripId;
    }


    public void setTripId(String tripId) {
        this.tripId = tripId;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    
}
