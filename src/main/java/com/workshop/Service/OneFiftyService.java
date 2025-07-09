package com.workshop.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.DTO.PricingResponse;
import com.workshop.Entity.OneFifty;
import com.workshop.Entity.roundTrip;
import com.workshop.Repo.OneFiftyRepo;

@Service
public class OneFiftyService {
    

    @Autowired
     private  OneFiftyRepo repo;

    

    // Parses "City, State" into [city, state]
    private String[] extractCityAndState(String loc) {
        String[] parts = loc.split(",");
        if (parts.length >= 2) {
            return new String[]{parts[0].trim(), parts[1].trim()};
        }
        return new String[]{"Unknown City", "Unknown State"};
    }

    // public PricingResponse applyPricing(String pickupLoc, String dropLoc, int distance) {
    //     if (distance < 1 || distance > 150) {
    //         throw new IllegalArgumentException("Distance must be between 1 and 150 km");
    //     }

    //     String[] p = extractCityAndState(pickupLoc);
    //     String[] d = extractCityAndState(dropLoc);

    //     List<OneFifty> matches = repo.findBySourceStateAndDestinationStateAndSourceCityAndDestinationCityAndMinDistanceLessThanEqualAndMaxDistanceGreaterThanEqual(
    //             p[1], d[1], p[0], d[0], distance, distance
    //     );

        

    //     OneFifty data = matches.stream().findFirst()
    //             .orElseThrow(() -> new RuntimeException(
    //                     "No pricing data found for route " + pickupLoc + " → " + dropLoc + " at " + distance + "km"));

    //     PricingResponse resp = new PricingResponse();
    //     resp.setHatchback(data.getHatchback());
    //     resp.setSedan(data.getSedan());
    //     resp.setSedanpremium(data.getSedanpremium());
    //     resp.setSuv(data.getSuv());
    //     resp.setSuvplus(data.getSuvplus());
    //     resp.setErtiga(data.getErtiga());
    //     return resp;
    // }



    public PricingResponse applyPricing(int distance) {
    if (distance < 1 || distance > 150) {
        throw new IllegalArgumentException("Distance must be between 1 and 150 km");
    }

    // Fetch matching record based only on distance
    List<OneFifty> matches = repo.findByMinDistanceLessThanEqualAndMaxDistanceGreaterThanEqual(distance, distance);

    OneFifty data = matches.stream().findFirst()
            .orElseThrow(() -> new RuntimeException(
                    "No pricing data found for distance: " + distance + " km"));

    PricingResponse resp = new PricingResponse();
    resp.setId(data.getId());
    resp.setMinDistance(data.getMinDistance());
    resp.setMaxDistance(data.getMaxDistance());
    resp.setHatchback(data.getHatchback());
    resp.setSedan(data.getSedan());
    resp.setSedanpremium(data.getSedanpremium());
    resp.setSuv(data.getSuv());
    resp.setSuvplus(data.getSuvplus());
    resp.setErtiga(data.getErtiga());
    return resp;
}


public PricingResponse updatePricingWithParams(int id,
                                               int minDistance, int maxDistance,
                                               int hatchback, int sedan, int sedanpremium,
                                               int suv, int suvplus, int ertiga) {

    OneFifty existing = repo.findById(id).orElse(null);

    if (existing == null) {
        throw new RuntimeException("Pricing record not found with ID: " + id);
    }

    existing.setMinDistance(minDistance);
    existing.setMaxDistance(maxDistance);
    existing.setHatchback(hatchback);
    existing.setSedan(sedan);
    existing.setSedanpremium(sedanpremium);
    existing.setSuv(suv);
    existing.setSuvplus(suvplus);
    existing.setErtiga(ertiga);

    OneFifty updated = repo.save(existing);

    PricingResponse response = new PricingResponse();
    response.setHatchback(updated.getHatchback());
    response.setSedan(updated.getSedan());
    response.setSedanpremium(updated.getSedanpremium());
    response.setSuv(updated.getSuv());
    response.setSuvplus(updated.getSuvplus());
    response.setErtiga(updated.getErtiga());

    return response;
}


public PricingResponse createPricingWithParams(int minDistance, int maxDistance,
                                                   int hatchback, int sedan, int sedanpremium,
                                                   int suv, int suvplus, int ertiga) {

        OneFifty newEntry = new OneFifty();
        newEntry.setMinDistance(minDistance);
        newEntry.setMaxDistance(maxDistance);
        newEntry.setHatchback(hatchback);
        newEntry.setSedan(sedan);
        newEntry.setSedanpremium(sedanpremium);
        newEntry.setSuv(suv);
        newEntry.setSuvplus(suvplus);
        newEntry.setErtiga(ertiga);

        OneFifty saved = repo.save(newEntry);

        PricingResponse response = new PricingResponse();
        response.setHatchback(saved.getHatchback());
        response.setSedan(saved.getSedan());
        response.setSedanpremium(saved.getSedanpremium());
        response.setSuv(saved.getSuv());
        response.setSuvplus(saved.getSuvplus());
        response.setErtiga(saved.getErtiga());

        return response;
    }



    
}
