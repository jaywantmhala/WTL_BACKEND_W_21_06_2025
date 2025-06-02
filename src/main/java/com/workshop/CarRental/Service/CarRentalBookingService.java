package com.workshop.CarRental.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.workshop.CarRental.Entity.CarRentalUser;
import com.workshop.CarRental.Repository.CarRentalRepository;
import com.workshop.Entity.User;

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


public boolean updatePassword(String email, String newPassword) {
    Optional<CarRentalUser> optionalUser = carRentalRepository.findByEmail(email);
    
    if (optionalUser.isPresent()) {
        CarRentalUser user = optionalUser.get();
        user.setPassword(newPassword); // Update password
        carRentalRepository.save(user); // Save changes
        return true;
    }
    return false;
}

public CarRentalUser getUserById(int id){
    return this.carRentalRepository.findById(id).get();
}

public CarRentalUser getProfile(int id){
    return this.carRentalRepository.findById(id).get();
}

public CarRentalUser updateProfileById(int id, CarRentalUser carRentalUser) {
    CarRentalUser existingUser = this.carRentalRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    existingUser.setUserName(carRentalUser.getUserName());
    existingUser.setEmail(carRentalUser.getEmail());
    existingUser.setPhone(carRentalUser.getPhone());
    existingUser.setAddress(carRentalUser.getAddress());
    return this.carRentalRepository.save(existingUser);
}

public CarRentalUser getCarRentalUserById(int id){
    return this.carRentalRepository.findById(id).get();
}


}
