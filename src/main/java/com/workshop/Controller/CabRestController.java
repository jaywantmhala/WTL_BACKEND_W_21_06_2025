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
import com.workshop.CarRental.Entity.CarRentalUser;
import com.workshop.CarRental.Repository.CarRentalRepository;
import com.workshop.CarRental.Service.CarRentalBookingService;
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
import com.workshop.Entity.VendorDrivers;
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
import com.workshop.Service.VendorDriverService;
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

    @Autowired
    private CarRentalRepository carRentalRepository;

    @Autowired
    private CarRentalBookingService carRentalBookingService;

    @Autowired
    private VendorDriverService vendorDriverService;

    private String apiKey = "AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w"; // Replace with your Google API key

    private static final String DISTANCE_MATRIX_API_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
    private static final String API_KEY = "AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w"; // Your Google API key

    // @PostMapping("/cab1")
    // public ResponseEntity<Map<String, Object>> processForm(
    // @RequestParam("tripType") String tripType,
    // @RequestParam("pickupLocation") String pickupLocation,
    // @RequestParam("dropLocation") String dropLocation,
    // @RequestParam("date") String date,
    // @RequestParam(value = "Returndate", required = false) String returndate,
    // @RequestParam("time") String time,
    // @RequestParam(value = "distance", required = false) String distance) {

    // Map<String, Object> response = new HashMap<>();

    // try {
    // // Validate required parameters
    // if (pickupLocation == null || dropLocation == null || date == null || time ==
    // null) {
    // throw new IllegalArgumentException("Missing required parameters");
    // }

    // // Calculate distance using Google Maps API if not provided
    // int oneWayDistance = 0;
    // if (distance == null || distance.isEmpty()) {
    // oneWayDistance = getDistanceFromGoogleMaps(pickupLocation, dropLocation);
    // } else {
    // // Use provided distance after cleaning
    // String numericString = distance.replaceAll("[^0-9.]", "");
    // oneWayDistance = (int) Double.parseDouble(numericString);
    // }

    // List<Trip> tripinfo = new ArrayList<>();
    // int days = 0;
    // int totalDistance = oneWayDistance;

    // String cityName = extractCityName(userService.getLongNameByCity(dropLocation,
    // apiKey), dropLocation);
    // String cityName1 =
    // extractCityName(userService.getLongNameByCity(pickupLocation, apiKey),
    // pickupLocation);

    // // Handle Bengaluru/Bangalore naming inconsistency
    // cityName = cityName.equals("Bengaluru") ? "Bangalore" : cityName;
    // cityName1 = cityName1.equals("Bengaluru") ? "Bangalore" : cityName1;

    // if ("oneWay".equals(tripType)) {
    // tripinfo = tripSer.getonewayTrip(cityName, cityName1);
    // if (tripinfo.isEmpty()) {
    // tripinfo.add(createDefaultOneWayTrip());
    // }
    // } else if ("roundTrip".equals(tripType)) {
    // if (returndate == null || returndate.isEmpty()) {
    // throw new IllegalArgumentException("Return date is required for round
    // trips");
    // }

    // LocalDate localDate1 = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    // LocalDate localDate2 = LocalDate.parse(returndate,
    // DateTimeFormatter.ISO_DATE);

    // // Validate return date is not before pickup date
    // if (localDate2.isBefore(localDate1)) {
    // throw new IllegalArgumentException("Return date cannot be before pickup
    // date");
    // }

    // days = (int) ChronoUnit.DAYS.between(localDate1, localDate2) + 1;

    // // For round trip, total distance is one way distance multiplied by 2 (going
    // and returning)
    // totalDistance = oneWayDistance * 2;

    // tripinfo = tripSer.getRoundTrip(cityName, cityName1);

    // if (tripinfo.isEmpty()) {
    // tripinfo.add(createDefaultRoundTrip());
    // }
    // } else {
    // throw new IllegalArgumentException("Invalid trip type. Must be 'oneWay' or
    // 'roundTrip'");
    // }

    // List<CabInfo> cabs = cabser.getAll();

    // // Calculate price based on distance and trip type
    // double price = calculatePrice(oneWayDistance, "roundTrip".equals(tripType),
    // days);

    // // Prepare response
    // response.put("tripType", tripType);
    // response.put("pickupLocation", pickupLocation);
    // response.put("dropLocation", dropLocation);
    // response.put("date", date);
    // response.put("returndate", returndate);
    // response.put("time", time);
    // response.put("oneWayDistance", oneWayDistance);
    // response.put("totalDistance", totalDistance);
    // response.put("cabinfo", cabs);
    // response.put("price", price);
    // response.put("tripinfo", tripinfo);
    // response.put("days", days);

    // return ResponseEntity.ok(response);

    // } catch (IllegalArgumentException e) {
    // response.put("error", e.getMessage());
    // return ResponseEntity.badRequest().body(response);
    // } catch (Exception e) {
    // response.put("error", "Failed to process request: " + e.getMessage());
    // return
    // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    // }
    // }

    // private int getDistanceFromGoogleMaps(String origin, String destination)
    // throws Exception {
    // String urlString = String.format("%s?origins=%s&destinations=%s&key=%s",
    // DISTANCE_MATRIX_API_URL,
    // URLEncoder.encode(origin, StandardCharsets.UTF_8.toString()),
    // URLEncoder.encode(destination, StandardCharsets.UTF_8.toString()),
    // API_KEY);

    // URL url = new URL(urlString);
    // HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    // connection.setRequestMethod("GET");

    // if (connection.getResponseCode() != 200) {
    // throw new RuntimeException("Failed to get distance from Google Maps API");
    // }

    // BufferedReader reader = new BufferedReader(new
    // InputStreamReader(connection.getInputStream()));
    // StringBuilder response = new StringBuilder();
    // String line;
    // while ((line = reader.readLine()) != null) {
    // response.append(line);
    // }
    // reader.close();

    // // Parse JSON response
    // JSONObject jsonResponse = (JSONObject) new
    // org.json.simple.parser.JSONParser().parse(response.toString());
    // JSONObject firstRow = (JSONObject) ((List<?>)
    // jsonResponse.get("rows")).get(0);
    // JSONObject firstElement = (JSONObject) ((List<?>)
    // firstRow.get("elements")).get(0);
    // JSONObject distanceObj = (JSONObject) firstElement.get("distance");

    // // Distance in meters
    // long distanceInMeters = (long) distanceObj.get("value");
    // // Convert to kilometers
    // return (int) (distanceInMeters / 1000);
    // }

    // private String extractCityName(String apiResult, String fallbackLocation) {
    // if (apiResult == null || "Administrative Area Level 3 not
    // found.".equals(apiResult)) {
    // String[] parts = fallbackLocation.split(", ");
    // return parts[0];
    // } else if (apiResult.equals("North Goa") || apiResult.equals("South Goa")) {
    // String[] parts = apiResult.split(", ");
    // return parts[0];
    // } else {
    // String[] parts = apiResult.split(" ");
    // return parts[0];
    // }
    // }

    // private double calculatePrice(int distance, boolean isRoundTrip, int days) {
    // // Basic pricing logic - adjust as needed
    // double baseRatePerKm = 10.0;
    // double price = distance * baseRatePerKm;

    // if (isRoundTrip) {
    // price *= 2; // Round trip is double the one-way price
    // }

    // // Additional charges for multi-day trips
    // if (days > 1) {
    // price += (days - 1) * 500; // 500 per additional day
    // }

    // return price;
    // }

    // private onewayTrip createDefaultOneWayTrip() {
    // return new onewayTrip(
    // null, "", "", "", "",
    // 12, 15, 18, 21, 26,
    // "", null, null, 0
    // );
    // }

    // private roundTrip createDefaultRoundTrip() {
    // return new roundTrip(
    // null, "", "", "", "",
    // 10, 11, 14, 14, 21,
    // ""
    // );
    // }

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

            // Calculate distance using Google Maps API if not provided or equals "0"

            int calculatedDistance = 0;

            if (distance == null || distance.isEmpty() || distance.equals("0")) {

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

                // For round trips, double the calculated distance (one way → round trip)

                // Only if we've calculated it dynamically from Google Maps

                if (distance == null || distance.isEmpty() || distance.equals("0") || distance.equals("-1")) {

                    
                    calculatedDistance = calculatedDistance * days; // Double for round trip

                    

                }

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

    // GET endpoint to retrieve pricing information by cities

    @GetMapping("/pricing")

    public ResponseEntity<Map<String, Object>> getPricing(

            @RequestParam("sourceCity") String sourceCity,

            @RequestParam("destCity") String destCity,

            @RequestParam(value = "tripType", required = false, defaultValue = "oneWay") String tripType

    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            // Handle Bengaluru/Bangalore naming inconsistency

            String normalizedSourceCity = sourceCity.equals("Bengaluru") ? "Bangalore" : sourceCity;

            String normalizedDestCity = destCity.equals("Bengaluru") ? "Bangalore" : destCity;

            List<Trip> tripinfo = new ArrayList<>();

            if ("oneWay".equals(tripType)) {

                tripinfo = tripSer.getonewayTrip(normalizedDestCity, normalizedSourceCity);

                if (tripinfo.isEmpty()) {

                    tripinfo.add(createDefaultOneWayTrip());

                }

            } else if ("roundTrip".equals(tripType)) {

                tripinfo = tripSer.getRoundTrip(normalizedDestCity, normalizedSourceCity);

                if (tripinfo.isEmpty()) {

                    tripinfo.add(createDefaultRoundTrip());

                }

            }

            // Calculate dynamic distance using Google Maps instead of estimation

            int dynamicDistance;

            try {

                dynamicDistance = getDistanceFromGoogleMaps(sourceCity, destCity);

                // Double the distance for round trips

                if ("roundTrip".equals(tripType)) {

                    dynamicDistance = dynamicDistance * 2;

                }

            } catch (Exception e) {

                // Fallback to default if Google Maps API fails

                dynamicDistance = 100;

                if ("roundTrip".equals(tripType)) {

                    dynamicDistance = 200; // Double for round trips

                }

            }

            response.put("sourceCity", sourceCity);

            response.put("destCity", destCity);

            response.put("tripType", tripType);

            response.put("distance", dynamicDistance);

            response.put("pricingInfo", tripinfo);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("error", "Failed to retrieve pricing: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }

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

    // for invoice

    @PostMapping("/invoice1")
    public Map<String, Object> bookCab(
            // @RequestParam(value = "cabId", required = false) String cabId,
            @RequestParam(value = "modelName", required = false) String modelName,
            @RequestParam(value = "modelType", required = false) String modelType,
            @RequestParam(value = "seats", required = false) String seats,
            @RequestParam(value = "fuelType", required = false) String fuelType,
            @RequestParam(value = "availability", required = false) String availability,
            @RequestParam(value = "price", required = false) String price,
            @RequestParam(value = "pickupLocation", required = false) String pickupLocation,
            @RequestParam(value = "dropLocation", required = false) String dropLocation,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "returndate", required = false) String returndate,
            @RequestParam(value = "time", required = false) String time,
            @RequestParam(value = "tripType", required = false) String tripType,
            @RequestParam(value = "distance", required = false) String distance,
            @RequestParam(value = "days", required = false) String days) {

        // System.out.println("Cab ID: " + cabId);
        System.out.println("Model Name: " + modelName);

        int driverrate = 0;
        int gst = 0;
        int service = 0;

        if ("oneWay".equals(tripType)) {
            gst = (Integer.parseInt(price) / 100) * 15;
            service = (Integer.parseInt(price) / 100) * 10;
        } else if ("roundTrip".equals(tripType)) {
            driverrate = Integer.parseInt(days) * 300;
            gst = ((Integer.parseInt(price) + driverrate) / 100) * 15;
            service = ((Integer.parseInt(price) + driverrate) / 100) * 10;
        }

        int totalAmount = Integer.parseInt(price) + gst + service + driverrate;

        Map<String, Object> response = new HashMap<>();
        response.put("driverrate", driverrate);
        response.put("days", days);
        response.put("gst", gst);
        response.put("service", service);
        response.put("total", totalAmount);
        // response.put("cabId", cabId);
        response.put("modelName", modelName);
        response.put("modelType", modelType);
        response.put("seats", seats);
        response.put("fuelType", fuelType);
        response.put("availability", availability);
        response.put("price", price);
        response.put("pickupLocation", pickupLocation);
        response.put("dropLocation", dropLocation);
        response.put("date", date);
        response.put("returndate", returndate);
        response.put("time", time);
        response.put("tripType", tripType);
        response.put("distance", distance);

        return response;
    }

    // for booking confirm
    @PostMapping("/bookingConfirm")
    public ResponseEntity<Map<String, Object>> createBooking(
            @RequestParam String cabId,
            @RequestParam String modelName,
            @RequestParam String modelType,
            @RequestParam String seats,
            @RequestParam String fuelType,
            @RequestParam String availability,
            @RequestParam String price,
            @RequestParam String pickupLocation,
            @RequestParam String dropLocation,
            @RequestParam String date,
            @RequestParam(required = false) String returndate,
            @RequestParam String time,
            @RequestParam String tripType,
            @RequestParam String distance,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String service,
            @RequestParam String gst,
            @RequestParam String total,
            @RequestParam String days,
            @RequestParam String driverrate,
            @RequestParam String phone,
            @RequestParam int userId) {

        try {
            // Create booking object

            CarRentalUser carRentalUser = this.carRentalRepository.findById(userId).orElse(null);
            Booking booking = new Booking();
            booking.setFromLocation(pickupLocation);
            booking.setToLocation(dropLocation);
            booking.setTripType(tripType);
            booking.setStartDate(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
            booking.setTime(time);
            booking.setDistance(distance);
            booking.setName(name);
            booking.setEmail(email);
            booking.setPhone(phone);
            booking.setCarRentalUser(carRentalUser);
            booking.setUserDrop(dropLocation);
            booking.setUserPickup(pickupLocation);
            booking.setUserTripType(tripType);
            booking.setBookingType("website");
            booking.setDate(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
            booking.setCar(modelType);
            booking.setAmount(Integer.parseInt(price));
            booking.setGst(Integer.parseInt(gst));
            booking.setServiceCharge(Integer.parseInt(service));

            if ("roundTrip".equals(tripType) && returndate != null) {
                booking.setReturnDate(LocalDate.parse(returndate, DateTimeFormatter.ISO_DATE));
            }

            // Generate booking ID
            String bookid = "WTL" + System.currentTimeMillis();
            booking.setBookingId(bookid);
            booking.setBookid(bookid);

            // Save booking
            ser.saveBooking(booking);

            // Call external API
            callExternalBookingApi(
                    date, name, email, modelName, distance, phone,
                    pickupLocation, dropLocation, time, returndate,
                    days, tripType, total, price, service, gst, driverrate);

            // Send confirmation email
            sendConfirmationEmail(name, email, bookid, pickupLocation,
                    dropLocation, tripType, date, time, total);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "bookingId", bookid,
                    "message", "Booking created successfully"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Booking creation failed"));
        }
    }

    private void callExternalBookingApi(
            String date, String name, String email, String car,
            String distance, String phone, String userPickup,
            String userDrop, String time, String dateend,
            String days, String userTripType, String amount,
            String baseAmount, String serviceCharge, String gst,
            String driverBhata) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("date1", date);
            requestBody.put("name", name);
            requestBody.put("email", email);
            requestBody.put("car", car);
            requestBody.put("distance", distance);
            requestBody.put("phone", phone);
            requestBody.put("user_pickup", userPickup);
            requestBody.put("user_drop", userDrop);
            requestBody.put("time", time);
            requestBody.put("dateend", dateend);
            requestBody.put("timeend", "");
            requestBody.put("days", days);
            requestBody.put("user_trip_type", userTripType);
            requestBody.put("amount", amount);
            requestBody.put("baseAmount", baseAmount);
            requestBody.put("service_charge", serviceCharge);
            requestBody.put("gst", gst);
            requestBody.put("driver_bhata", driverBhata);

            String apiURL = "https://aimcabbooking.com/confirm-round-api-wtl.php";
            HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.toString().getBytes("UTF-8"));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println("API Response: " + response.toString());
            } else {
                System.out.println("API request failed with response code: " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendConfirmationEmail(String name, String email, String bookingId,
            String pickupLocation, String dropLocation,
            String tripType, String date, String time,
            String total) {
        String subject = "Booking Confirmation - " + bookingId;
        String message = "<h3>Hello " + name + ",</h3>" +
                "<p>Your booking has been confirmed.</p>" +
                "<p><strong>Booking Details:</strong></p>" +
                "<ul>" +
                "<li><strong>Booking ID:</strong> " + bookingId + "</li>" +
                "<li><strong>Pickup Location:</strong> " + pickupLocation + "</li>" +
                "<li><strong>Drop Location:</strong> " + dropLocation + "</li>" +
                "<li><strong>Trip Type:</strong> " + tripType + "</li>" +
                "<li><strong>Date:</strong> " + date + "</li>" +
                "<li><strong>Time:</strong> " + time + "</li>" +
                "<li><strong>Amount Paid:</strong> ₹" + total + "</li>" +
                "</ul>" +
                "<p>Thank you for choosing us!</p>";

        emailService.sendEmail(message, subject, email);
    }

    @PostMapping("/get-user-location/{id}")
    public CarRentalUser saveUserLocation(@PathVariable int id) {
        return ser.saveUserLocation(id);
    }


    @PutMapping("/update-Location/{id}")
    public CarRentalUser updateUserLocation(@PathVariable int id,@RequestParam double latitude,
    @RequestParam double longitude){
        return this.carRentalBookingService.updateLocation(id, latitude,
        longitude);
    }

    @PutMapping("/update-driver-location/{vendorDriverId}")
    public VendorDrivers updateDriverLocation(
        @PathVariable int vendorDriverId,
        @RequestParam double latitude,
        @RequestParam double longitude) {
        
        return this.vendorDriverService.updateLocationDriver(
            vendorDriverId,
            latitude,
            longitude
        );
    }
}
