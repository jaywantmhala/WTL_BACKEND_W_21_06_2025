package com.workshop.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.workshop.DTO.CityDTO;
import com.workshop.DTO.PriceUpdateRequest;
import com.workshop.DTO.StateDTO;
import com.workshop.Entity.Booking;
import com.workshop.Entity.CabInfo;
import com.workshop.Entity.Cities;
import com.workshop.Entity.Popup;
import com.workshop.Entity.States;
import com.workshop.Entity.Tripprice;
import com.workshop.Entity.User;
import com.workshop.Entity.onewayTrip;
import com.workshop.Entity.roundTrip;
import com.workshop.Repo.StateRepository;
import com.workshop.Repo.Trip;
import com.workshop.Service.BookingService;
import com.workshop.Service.CabInfoService;
import com.workshop.Service.CitiesService;
import com.workshop.Service.EmailService;
import com.workshop.Service.PopupService;
import com.workshop.Service.StatesService;
import com.workshop.Service.TripService;
import com.workshop.Service.UserDetailServiceImpl;
import com.workshop.Service.TripRateService;

@RestController
@RequestMapping("/api")
public class CabRestController {

    @Autowired
	BookingService ser;

	@Autowired
	private TripService tripSer;

	@Autowired
	CabInfoService cabser;

	@Autowired
	private TripRateService tripRateService;

	@Autowired
	private StatesService statesService;

	@Autowired
	private CitiesService citiesService;

	@Autowired
	private EmailService emailService;

	@Autowired
	PopupService service;

	@Autowired
	UserDetailServiceImpl userService;

    private String apiKey = "AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w"; // Replace with your Google API key


    private static final String DISTANCE_MATRIX_API_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
    private static final String API_KEY = "AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w"; // Your Google API key

    @PostMapping("/cab1")
    public ResponseEntity<Map<String, Object>> processForm(
            @RequestParam("tripType") String tripType,
            @RequestParam("pickupLocation") String pickupLocation,
            @RequestParam("dropLocation") String dropLocation,
            @RequestParam("date") String date,
            @RequestParam(value = "Returndate", required = false) String returndate,
            @RequestParam("time") String time,
            @RequestParam(value = "distance", required = false) String distance
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Calculate distance using Google Maps API if not provided
            int calculatedDistance = 0;
            if (distance == null || distance.isEmpty()) {
                calculatedDistance = getDistanceFromGoogleMaps(pickupLocation, dropLocation);
            } else {
                // Use provided distance after cleaning
                String numericString = distance.replaceAll("[^0-9.]", "");
                calculatedDistance = (int) Double.parseDouble(numericString);
            }

            // Rest of your existing processing logic
            List<Trip> tripinfo = new ArrayList<>();
            int days = 0;
            
            String cityName = extractCityName(userService.getLongNameByCity(dropLocation, apiKey), dropLocation);
            String cityName1 = extractCityName(userService.getLongNameByCity(pickupLocation, apiKey), pickupLocation);

            // Handle Bengaluru/Bangalore naming inconsistency
            cityName = cityName.equals("Bengaluru") ? "Bangalore" : cityName;
            cityName1 = cityName1.equals("Bengaluru") ? "Bangalore" : cityName1;

            if ("oneWay".equals(tripType)) {
                tripinfo = tripSer.getonewayTrip(cityName, cityName1);
                if (tripinfo.isEmpty()) {
                    tripinfo.add(createDefaultOneWayTrip());
                }
            } else if ("roundTrip".equals(tripType)) {
                LocalDate localDate1 = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
                LocalDate localDate2 = LocalDate.parse(returndate, DateTimeFormatter.ISO_DATE);
                days = (int) ChronoUnit.DAYS.between(localDate1, localDate2) + 1;
                
                LocalTime localTime1 = LocalTime.parse(time, DateTimeFormatter.ISO_TIME);
                LocalTime localTime2 = LocalTime.parse("23:30:00", DateTimeFormatter.ISO_TIME);
                
                calculatedDistance = tripSer.getRoundDistance(localDate1, localTime1, localDate2, localTime2, 
                    String.valueOf(calculatedDistance));
                tripinfo = tripSer.getRoundTrip(cityName, cityName1);
                
                if (tripinfo.isEmpty()) {
                    tripinfo.add(createDefaultRoundTrip());
                }
            }
            
            List<CabInfo> cabs = cabser.getAll();
            
            response.put("tripType", tripType);
            response.put("pickupLocation", pickupLocation);
            response.put("dropLocation", dropLocation);
            response.put("date", date);
            response.put("returndate", returndate);
            response.put("time", time);
            response.put("distance", calculatedDistance);
            response.put("cabinfo", cabs);
            response.put("price", 10);
            response.put("tripinfo", tripinfo);
            response.put("days", days);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Failed to process request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private int getDistanceFromGoogleMaps(String origin, String destination) throws Exception {
        String urlString = String.format("%s?origins=%s&destinations=%s&key=%s",
                DISTANCE_MATRIX_API_URL,
                URLEncoder.encode(origin, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(destination, StandardCharsets.UTF_8.toString()),
                API_KEY);

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed to get distance from Google Maps API");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Parse JSON response
        JSONObject jsonResponse = (JSONObject) new org.json.simple.parser.JSONParser().parse(response.toString());
        JSONObject firstRow = (JSONObject) ((List<?>) jsonResponse.get("rows")).get(0);
        JSONObject firstElement = (JSONObject) ((List<?>) firstRow.get("elements")).get(0);
        JSONObject distanceObj = (JSONObject) firstElement.get("distance");
        
        // Distance in meters
        long distanceInMeters = (long) distanceObj.get("value");
        // Convert to kilometers
        return (int) (distanceInMeters / 1000);
    }

    private String extractCityName(String apiResult, String fallbackLocation) {
        if (apiResult == null || "Administrative Area Level 3 not found.".equals(apiResult)) {
            String[] parts = fallbackLocation.split(", ");
            return parts[0];
        } else if (apiResult.equals("North Goa") || apiResult.equals("South Goa")) {
            String[] parts = apiResult.split(", ");
            return parts[0];
        } else {
            String[] parts = apiResult.split(" ");
            return parts[0];
        }
    }

    private onewayTrip createDefaultOneWayTrip() {
        return new onewayTrip(
            null, "", "", "", "", 
            12, 15, 18, 21, 26, 
            "", null, null, 0
        );
    }

    private roundTrip createDefaultRoundTrip() {
        return new roundTrip(
            null, "", "", "", "", 
            10, 11, 14, 14, 21, 
            ""
        );


    }




    @GetMapping("/by-driver/{vendorDriverId}")
    public ResponseEntity<List<Booking>> getBookingsByVendorDriver(
            @PathVariable int vendorDriverId) {
        
        List<Booking> bookings = ser.getBookingsByVendorDriverId(vendorDriverId);
        
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(bookings);
    }


    @GetMapping("/by-user/{id}")
    public ResponseEntity<List<Booking>> getBookingsByCarRental(
            @PathVariable int id) {
        
        List<Booking> bookings = ser.getBookingByCarRentalUserId(id);
        
        if (bookings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(bookings);
    }

}

