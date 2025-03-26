package com.workshop.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.Entity.onewayTrip;
import com.workshop.Entity.roundTrip;
import com.workshop.Repo.OnewayTripRepo;
import com.workshop.Repo.RoundTripRepo;
import com.workshop.Repo.Trip;

import jakarta.transaction.Transactional;

@Service
public class TripService {

	@Autowired
	private OnewayTripRepo repo;

	private  String apiKey = "AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w"; // Replace with your Google API key


	@Autowired
	RoundTripRepo roundrepo;

	public List<Trip> getonewayTrip(String from, String to) {
		return repo.findBySourceCityAndDestinationCity(to, from);

	}

	public List<Trip> getRoundTrip(String from, String to) {
		return roundrepo.findBySourceCityAndDestinationCity(to, from);

	}

	public int getRoundDistance(LocalDate localDate1, LocalTime time1, LocalDate localDate2, LocalTime time2,
			String distance) {

		// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

		// LocalDate localDate1 = LocalDate.parse(localDate1);
		// LocalDate localDate2 = LocalDate.parse(localDate2);
		// LocalTime time1 = LocalTime.parse(time1, DateTimeFormatter.ISO_DATE);
		// LocalTime time2 = LocalTime.parse(time2, DateTimeFormatter.ISO_DATE);

		int Distance = Integer.parseInt(distance);

		int days = localDate1.until(localDate2).getDays() + 1;

		// Calculate service and driver based on distance and days
		int driver = (int) (days * 300);
		int service = Distance * 2;

		if (service > 300 && days == 1) {
			return -1; // Service is unavailable
		} else if (service > 600 && days == 2) {
			return -2; // Service is unavailable
		} else if (service > 900 && days == 3) {
			return -3; // Service is unavailable
		} else if (service > 1200 && days == 4) {
			return -4; // Service is unavailable
		} else if (service > 1500 && days == 5) {
			return -5; // Service is unavailable
		} else if (service > 1800 && days == 6) {
			return -6; // Service is unavailable
		} else if (service > 2100 && days == 7) {
			return -7; // Service is unavailable
		} else if (service > 2400 && days == 8) {
			return -8; // Service is unavailable
		} else if (service > 2700 && days == 9) {
			return -9; // Service is unavailable
		} else if (service > 3000 && days == 10) {
			return -10; // Service is unavailable
		}

		int roundDist = Distance * 2;
		int intAmount = -1;
		int dr = days * 300;

		if (roundDist <= 300 && days == 1) {
			intAmount = 300 * days;
		} else if (roundDist > 300 && days == 1) {
			int dPlus = roundDist - 300;
			intAmount = 300 * days + dPlus;
		} else if (roundDist <= 600 && days == 2) {
			intAmount = 300 * days;
		} else if (roundDist > 600 && days == 2) {
			int dPlus = roundDist - 600;
			intAmount = 300 * days + dPlus;
		} else if (roundDist <= 900 && days == 3) {
			intAmount = 300 * days;
		} else if (roundDist > 900 && days == 3) {
			int dPlus = roundDist - 900;
			intAmount = 300 * days + dPlus;
		} else if (roundDist <= 1200 && days == 4) {
			intAmount = 300 * days;
		} else if (roundDist > 1200 && days == 4) {
			int dPlus = roundDist - 1200;
			intAmount = 300 * days + dPlus;
		} else if (roundDist <= 1500 && days == 5) {
			intAmount = 300 * days;
		} else if (roundDist > 1500 && days == 5) {
			int dPlus = roundDist - 1500;
			intAmount = 300 * days + dPlus;
		} else if (roundDist <= 1800 && days == 6) {
			intAmount = 300 * days;
		} else if (roundDist > 1800 && days == 6) {
			int dPlus = roundDist - 1800;
			intAmount = 300 * days + dPlus;
		} else if (roundDist <= 2100 && days == 7) {
			intAmount = 300 * days;
		} else if (roundDist > 2100 && days == 7) {
			int dPlus = roundDist - 1500;
			intAmount = 300 * days + dPlus;
		} else if (roundDist <= 2400 && days == 8) {
			intAmount = 300 * days;
		} else if (roundDist > 2400 && days == 8) {
			int dPlus = roundDist - 2400;
			intAmount = 300 * days + dPlus;
		} else if (roundDist <= 2700 && days == 9) {
			intAmount = 300 * days;
		} else if (roundDist > 2700 && days == 9) {
			int dPlus = roundDist - 2700;
			intAmount = 300 * days + dPlus;
		} else if (roundDist <= 3000 && days == 10) {
			intAmount = 300 * days;
		} else if (roundDist > 3000 && days == 10) {
			int dPlus = roundDist - 3000;
			intAmount = 300 * days + dPlus;
		} else if (roundDist <= 30) {
			// Handle the response here or return a specific value
			intAmount = -1; // Service is unavailable for the given distance
		}

		if (intAmount == -1) {
			// Handle the response here or return a specific value
			System.out.println("Service is unavailable for the given distance");
			// You can replace the print statement with your preferred way of handling this
			// case.
		}

		return intAmount;

	}

	public onewayTrip updatePrice(Long id, int hatchback, int sedan, int sedanpremium, int suv, int suvplus,
			String sourceState, String sourceCity, String destinationState, String destinationCity) {
		Optional<onewayTrip> tripOptional = repo.findById(id);
		if (tripOptional.isPresent()) {
			onewayTrip trip = tripOptional.get();
			trip.setHatchback(hatchback);
			trip.setSedan(sedan);
			trip.setSedanpremium(sedanpremium);
			trip.setSuv(suv);
			trip.setSuvplus(suvplus);
			trip.setSourceState(sourceState);
			trip.setSourceCity(sourceCity);
			trip.setDestinationState(destinationState);
			trip.setDestinationCity(destinationCity);
			return repo.save(trip);
		} else {
			throw new RuntimeException("Trip not found with ID: " + id);
		}
	}

	// Method to update trip prices based on sourceState and destinationState
	public void updatePrices(String sourceState, String destinationState, String sourceCity, String destinationCity,
			int hatchbackPrice, int sedanPrice, int sedanPremiumPrice, int suvPrice, int suvPlusPrice) {
		List<onewayTrip> trips = this.repo.findBySourceStateAndDestinationStateAndSourceCityAndDestinationCity(
				sourceState, destinationState, sourceCity, destinationCity);

		for (onewayTrip trip : trips) {
			trip.setHatchback(hatchbackPrice);
			trip.setSedan(sedanPrice);
			trip.setSedanpremium(sedanPremiumPrice);
			trip.setSuv(suvPrice);
			trip.setSuvplus(suvPlusPrice);
		}
		this.repo.saveAll(trips);
	}
	// public onewayTrip updatePrice(String sourceState, onewayTrip oneway){
	// oneway.setSourceState(sourceState);
	// return this.repo.save(oneway);
	// }

	public onewayTrip updatePrice(String sourceState, String destinationState, onewayTrip oneway) {
		oneway.setSourceState(sourceState);
		oneway.setDestinationState(destinationState);
		return this.repo.save(oneway);
	}

	public void updatePricesByRoundWay(String sourceState, String destinationState, String sourceCity,
			String destinationCity, int hatchbackPrice, int sedanPrice, int sedanPremiumPrice, int suvPrice,
			int suvPlusPrice) {
		List<roundTrip> trips = this.roundrepo.findBySourceStateAndDestinationStateAndSourceCityAndDestinationCity(
				sourceState, destinationState, sourceCity, destinationCity);

		for (roundTrip trip : trips) {
			trip.setHatchback(hatchbackPrice);
			trip.setSedan(sedanPrice);
			trip.setSedanpremium(sedanPremiumPrice);
			trip.setSuv(suvPrice);
			trip.setSuvplus(suvPlusPrice);
		}
		this.roundrepo.saveAll(trips);
	}

	// public String extractLocation(
	// @RequestParam("pickup") String pickupLocation,
	// @RequestParam("drop") String dropLocation) {

	// try {
	// return locationExtractor(pickupLocation, dropLocation);
	// } catch (IllegalArgumentException e) {
	// return "Error: " + e.getMessage();
	// }
	// }

	// public List<onewayTrip> getOneWayTripData(String pickupLocation, String dropLocation) {
	// 	if (pickupLocation == null || dropLocation == null) {
	// 		throw new IllegalArgumentException("Pickup and drop locations cannot be null");
	// 	}

	// 	// Extract city and state from the pickup location
	// 	String[] pickup = extractCityAndState(pickupLocation);
	// 	// Extract city and state from the drop location
	// 	String[] drop = extractCityAndState(dropLocation);

	// 	// pickup[0] = source city, pickup[1] = source state
	// 	// drop[0] = destination city, drop[1] = destination state
	// 	String sourceCity = pickup[0];
	// 	String sourceState = pickup[1];
	// 	String destinationCity = drop[0];
	// 	String destinationState = drop[1];

	// 	// Debug prints
	// 	System.out.println("Source City: " + sourceCity);
	// 	System.out.println("Source State: " + sourceState);
	// 	System.out.println("Destination City: " + destinationCity);
	// 	System.out.println("Destination State: " + destinationState);

	// 	// Fetch matching trip details from the database
	// 	return getAllData(sourceCity, sourceState, destinationState, destinationCity);
	// }
	
public List<onewayTrip> getOneWayTripData(String pickupLocation, String dropLocation) {
    if (pickupLocation == null || dropLocation == null) {
        throw new IllegalArgumentException("Pickup and drop locations cannot be null");
    }

    // Extract city and state from the locations
    String[] pickup = extractCityAndState(pickupLocation);
    String[] drop = extractCityAndState(dropLocation);

    String sourceCity = pickup[0];
    String sourceState = pickup[1];
    String destinationCity = drop[0];
    String destinationState = drop[1];

    // Calculate distance between locations
    Map<String, Object> distanceResult = calculateDistanceBetweenLocations(pickupLocation, dropLocation);
    
    if (!(boolean)distanceResult.get("success")) {
        throw new RuntimeException("Failed to calculate distance: " + distanceResult.get("message"));
    }

    String distanceText = (String)distanceResult.get("distanceText");
    double distanceInKm = (double)distanceResult.get("distanceInKm");

    System.out.println("Calculated Distance: " + distanceText + " (" + distanceInKm + " km)");

    // Fetch matching trip details from database
    List<onewayTrip> trips = getAllData(sourceCity, sourceState, destinationState, destinationCity);
    
    // Set both the formatted text and exact distance to each trip object
    trips.forEach(trip -> {
        trip.setDistance(distanceInKm);
        // trip.setDistanceText(distanceText);
    });
    
    return trips;
}

/**
 * Calculates distance between locations using Google Maps API
 * Returns a Map containing:
 * - success: boolean
 * - message: String (if error)
 * - distanceText: String (human-readable)
 * - distanceInKm: double
 */
private Map<String, Object> calculateDistanceBetweenLocations(String origin, String destination) {
    Map<String, Object> result = new HashMap<>();
    
    try {
        String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json" +
            "?origins=" + URLEncoder.encode(origin, StandardCharsets.UTF_8) +
            "&destinations=" + URLEncoder.encode(destination, StandardCharsets.UTF_8) +
            "&key=" + apiKey +
            "&units=metric" +
            "&mode=driving";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        // Check API response status
        String apiStatus = root.path("status").asText();
        if (!"OK".equals(apiStatus)) {
            result.put("success", false);
            result.put("message", "Google API error: " + apiStatus + 
                ". Error message: " + root.path("error_message").asText(""));
            return result;
        }

        JsonNode elements = root.path("rows").get(0).path("elements").get(0);
        String elementStatus = elements.path("status").asText();
        if (!"OK".equals(elementStatus)) {
            result.put("success", false);
            result.put("message", "Distance calculation failed: " + elementStatus);
            return result;
        }

        // Get both the human-readable text and exact numeric value
        String distanceText = elements.path("distance").path("text").asText();
        double distanceInKm = elements.path("distance").path("value").asDouble() / 1000.0;
        
        result.put("success", true);
        result.put("distanceText", distanceText);
        result.put("distanceInKm", distanceInKm);
        return result;

    } catch (Exception e) {
        result.put("success", false);
        result.put("message", "Error calculating distance: " + e.getMessage());
        return result;
    }
}

	private String[] extractCityAndState(String location) {
		String[] parts = location.split(",");
		if (parts.length >= 2) {
			return new String[] { parts[0].trim(), parts[1].trim() };
		}
		return new String[] { "Unknown City", "Unknown State" };
	}

	public List<onewayTrip> getAllData(String sourceCity, String sourceState, String destinationState, String destinationCity) {
		List<onewayTrip> trips = repo.findBySourceStateAndSourceCityAndDestinationStateAndDestinationCity(
				sourceState, sourceCity, destinationState, destinationCity);
	
		// If no data is found, return a list with a default onewayTrip
		if (trips == null || trips.isEmpty()) {
			onewayTrip newTrip = new onewayTrip();
			newTrip.setId(null); // Adjust as needed
			newTrip.setSourceState(sourceState);
			newTrip.setSourceCity(sourceCity);
			newTrip.setDestinationState(destinationState);
			newTrip.setDestinationCity(destinationCity);
			newTrip.setHatchback(10);
			newTrip.setSedan(11);
			newTrip.setSedanpremium(14);
			newTrip.setSuv(14);
			newTrip.setSuvplus(21);
			newTrip.setStatus("");
	
			return List.of(newTrip);
		}
	
		return trips;
	}
	

	// -------------------------

	public List<roundTrip> getRoundWayTripData(String pickupLocation, String dropLocation) {
		if (pickupLocation == null || dropLocation == null) {
			throw new IllegalArgumentException("Pickup and drop locations cannot be null");
		}

		// Extract city and state from the pickup location
		String[] pickup = extractCityAndState(pickupLocation);
		// Extract city and state from the drop location
		String[] drop = extractCityAndState(dropLocation);

		// pickup[0] = source city, pickup[1] = source state
		// drop[0] = destination city, drop[1] = destination state
		String sourceCity = pickup[0];
		String sourceState = pickup[1];
		String destinationCity = drop[0];
		String destinationState = drop[1];

		// Debug prints
		System.out.println("Source City: " + sourceCity);
		System.out.println("Source State: " + sourceState);
		System.out.println("Destination City: " + destinationCity);
		System.out.println("Destination State: " + destinationState);

		// Fetch matching trip details from the database
		return getAllData1(sourceCity, sourceState, destinationState,
				destinationCity);
	}

	// private String[] extractCityAndState(String location) {
	// String[] parts = location.split(",");
	// if (parts.length >= 2) {
	// return new String[] { parts[0].trim(), parts[1].trim() };
	// }
	// return new String[] { "Unknown City", "Unknown State" };
	// }

	public List<roundTrip> getAllData1(String sourceCity, String sourceState,
			String destinationState,
			String destinationCity) {
		// Note: The repository method expects parameters in the order: sourceState,
		// sourceCity, destinationState, destinationCity
		return roundrepo.findBySourceStateAndDestinationStateAndSourceCityAndDestinationCity(
				sourceState, destinationState, sourceCity, destinationCity);
	}

}
