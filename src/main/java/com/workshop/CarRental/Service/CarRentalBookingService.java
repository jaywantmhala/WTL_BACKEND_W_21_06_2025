package com.workshop.CarRental.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.workshop.CarRental.Entity.CarRentalUser;
import com.workshop.CarRental.Repository.CarRentalRepository;

@Service
public class CarRentalBookingService {

@Autowired
private CarRentalRepository carRentalRepository;



public CarRentalUser updateLocation(int id, double latitude, double longitude){
    CarRentalUser carRentalUser = this.carRentalRepository.findById(id).orElse(null);
    carRentalUser.setUserlatitude(latitude);
    carRentalUser.setUserlongitude(longitude);
    return this.carRentalRepository.save(carRentalUser);
}

}
