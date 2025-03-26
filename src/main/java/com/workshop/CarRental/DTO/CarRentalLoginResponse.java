package com.workshop.CarRental.DTO;

public class CarRentalLoginResponse {
    

    private String message;



    private String username;

    private String role;

    public CarRentalLoginResponse(){
        super();
    }

    public CarRentalLoginResponse(String message,String username, String role) {
        this.username = username;
        this.role = role;
        this.message=message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
    

    
}
