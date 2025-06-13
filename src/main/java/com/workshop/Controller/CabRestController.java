package com.workshop.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.workshop.Service.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.workshop.CarRental.Entity.CarRentalUser;
import com.workshop.CarRental.Repository.CarRentalRepository;
import com.workshop.CarRental.Service.CarRentalBookingService;
import com.workshop.Entity.Booking;
import com.workshop.Entity.CabInfo;
import com.workshop.Entity.VendorDrivers;
import com.workshop.Entity.Visitors;
import com.workshop.Entity.onewayTrip;
import com.workshop.Entity.roundTrip;
import com.workshop.Repo.Trip;

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
    private VisitorService visitorService;

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


    @Autowired
    private CityExtractionService cityExtractionService;

    private String apiKey = "AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w";

    private static final String DISTANCE_MATRIX_API_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";
    private static final String API_KEY = "AIzaSyCelDo4I5cPQ72TfCTQW-arhPZ7ALNcp8w";

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

    // @PostMapping("/cab1")
    // public ResponseEntity<Map<String, Object>> processForm(
    //         @RequestParam("tripType") String tripType,
    //         @RequestParam("pickupLocation") String pickupLocation,
    //         @RequestParam("dropLocation") String dropLocation,
    //         @RequestParam("date") String date,
    //         @RequestParam(value = "Returndate", required = false) String returndate,
    //         @RequestParam("time") String time,
    //         @RequestParam(value = "distance", required = false) String distance,
    //         @RequestParam(value = "packageName", required = false) String packageName,
    //         @RequestParam(value="Endtime", required = false) String Endtime,
    //         @RequestParam(value="carType", required = false) String carType)

    // {

    //     Map<String, Object> response = new HashMap<>();

    //     try {
    //         int calculatedDistance = 0;

    //         if (distance == null || distance.isEmpty() || distance.equals("0")) {
    //             calculatedDistance = getDistanceFromGoogleMaps(pickupLocation, dropLocation);
    //         } else {
    //             String numericString = distance.replaceAll("[^0-9.]", "");
    //             calculatedDistance = (int) Double.parseDouble(numericString);
    //         }

    //         List<Trip> tripinfo = new ArrayList<>();
    //         int days = 0;

    //         CityExtractionService.CityStateResult pickupResult = cityExtractionService.extractCityAndState(pickupLocation);
    //         CityExtractionService.CityStateResult dropResult = cityExtractionService.extractCityAndState(dropLocation);

    //         String cityName1 = pickupResult.getCity();   // Pickup city
    //         String cityName = dropResult.getCity();      // Drop city

    //         cityName = cityName.equals("Bengaluru") ? "Bangalore" : cityName;
    //         cityName1 = cityName1.equals("Bengaluru") ? "Bangalore" : cityName1;

    //         if ("oneWay".equalsIgnoreCase(tripType)) {
    //             List<onewayTrip> oneWayTrips = tripSer.getOneWayTripsWithDefaults(
    //                     cityName1,
    //                     pickupResult.getState(),
    //                     cityName,
    //                     dropResult.getState()
    //             );

    //             for (onewayTrip o : oneWayTrips) {
    //                 tripinfo.add(o);
    //             }
    //         } 
    //         else if ("roundTrip".equalsIgnoreCase(tripType)) {
    //             LocalDate localDate1 = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    //             LocalDate localDate2 = LocalDate.parse(returndate, DateTimeFormatter.ISO_DATE);

    //             days = (int) ChronoUnit.DAYS.between(localDate1, localDate2) + 1;

    //             if (distance == null || distance.isEmpty() || distance.equals("0") || distance.equals("-1")) {
    //                 calculatedDistance = calculatedDistance * days;
    //             }

    //             tripinfo = tripSer.getRoundTrip(cityName, cityName1);

    //             if (tripinfo.isEmpty()) {
    //                 tripinfo.add(createDefaultRoundTrip());
    //             }
    //         }

    //         if ("transfer".equalsIgnoreCase(tripType)) {
    //             // --- Transfer Pricing Logic ---
    //             // Car types: sedan, ertiga, innova crysta
    //             String car = carType != null ? carType.toLowerCase() : "sedan";
    //             double basePrice = 0;
    //             double extraHourRate = 0;
    //             double extraKmRate = 0;
    //             int maxHours = 0;
    //             int maxKm = 0;
    //             double total = 0;
    //             double gst = 0;
    //             double da = 0;
    //             double toll = 0;
    //             double parking = 0;
    //             double subtotal = 0;
    //             String packageNameUsed = packageName != null ? packageName.trim().toLowerCase() : "";
    //             int usedKm = calculatedDistance;
    //             int usedHours = 0;
    //             try {
    //                 LocalTime start = LocalTime.parse(time);
    //                 LocalTime end = LocalTime.parse(Endtime);
    //                 usedHours = (int) ChronoUnit.HOURS.between(start, end);
    //                 if (usedHours <= 0) usedHours = 8; // default for 8hr/80km
    //             } catch (Exception ex) {
    //                 usedHours = 8;
    //             }
    //             // 1. Distance slabs (point-to-point, not packages)
    //             if (packageNameUsed.isEmpty() || packageNameUsed.contains("transfer") || packageNameUsed.contains("slab")) {
    //                 // Distance slab pricing
    //                 if (car.equals("sedan")) {
    //                     if (usedKm <= 10) basePrice = 450;
    //                     else if (usedKm <= 20) basePrice = 600;
    //                     else if (usedKm <= 30) basePrice = 800;
    //                     else if (usedKm <= 40) basePrice = 950;
    //                     else basePrice = 950 + (usedKm - 40) * 12; // extra km
    //                     extraHourRate = 150;
    //                     extraKmRate = 12;
    //                 } else if (car.equals("ertiga")) {
    //                     if (usedKm <= 10) basePrice = 650;
    //                     else if (usedKm <= 20) basePrice = 850;
    //                     else if (usedKm <= 30) basePrice = 1250;
    //                     else if (usedKm <= 40) basePrice = 1600;
    //                     else basePrice = 1600 + (usedKm - 40) * 16; // extra km
    //                     extraHourRate = 200;
    //                     extraKmRate = 16;
    //                 } else if (car.equals("innova crysta")) {
    //                     if (usedKm <= 40) basePrice = 2200;
    //                     else basePrice = 2200 + (usedKm - 40) * 22; // extra km
    //                     extraHourRate = 400;
    //                     extraKmRate = 22;
    //                 } else {
    //                     basePrice = 600; // fallback
    //                     extraHourRate = 150;
    //                     extraKmRate = 12;
    //                 }
    //                 subtotal = basePrice;
    //                 gst = subtotal * 0.05;
    //                 total = subtotal + gst;
    //                 // No DA in transfer/regular slabs
    //                 // User can add parking/toll if needed
    //             }
    //             // 2. 8hr/80km package
    //             else if (packageNameUsed.contains("8hr") || packageNameUsed.contains("80km")) {
    //                 maxHours = 8;
    //                 maxKm = 80;
    //                 if (car.equals("sedan")) {
    //                     basePrice = 2200;
    //                     extraHourRate = 150;
    //                     extraKmRate = 12;
    //                 } else if (car.equals("ertiga")) {
    //                     basePrice = 2200;
    //                     extraHourRate = 200;
    //                     extraKmRate = 16;
    //                 } else if (car.equals("innova crysta")) {
    //                     basePrice = 2200;
    //                     extraHourRate = 400;
    //                     extraKmRate = 22;
    //                 } else {
    //                     basePrice = 2200;
    //                     extraHourRate = 150;
    //                     extraKmRate = 12;
    //                 }
    //                 int extraHours = Math.max(0, usedHours - maxHours);
    //                 int extraKms = Math.max(0, usedKm - maxKm);
    //                 double extraHourCharges = extraHours * extraHourRate;
    //                 double extraKmCharges = extraKms * extraKmRate;
    //                 // For demo, get parking/toll from request or set 0
    //                 try { parking = Double.parseDouble(response.getOrDefault("parking", "0").toString()); } catch (Exception e) { parking = 0; }
    //                 try { toll = Double.parseDouble(response.getOrDefault("toll", "0").toString()); } catch (Exception e) { toll = 0; }
    //                 subtotal = basePrice + extraHourCharges + extraKmCharges + parking + toll;
    //                 gst = subtotal * 0.05;
    //                 total = subtotal + gst;
    //             }
    //             // 3. Full Day/300km package
    //             else if (packageNameUsed.contains("full day") || packageNameUsed.contains("300km")) {
    //                 maxHours = 24;
    //                 maxKm = 300;
    //                 if (car.equals("sedan")) {
    //                     basePrice = 12 * 300;
    //                     extraHourRate = 150;
    //                     extraKmRate = 12;
    //                 } else if (car.equals("ertiga")) {
    //                     basePrice = 16 * 300;
    //                     extraHourRate = 200;
    //                     extraKmRate = 16;
    //                 } else if (car.equals("innova crysta")) {
    //                     basePrice = 22 * 300;
    //                     extraHourRate = 400;
    //                     extraKmRate = 22;
    //                 } else {
    //                     basePrice = 12 * 300;
    //                     extraHourRate = 150;
    //                     extraKmRate = 12;
    //                 }
    //                 int extraHours = Math.max(0, usedHours - maxHours);
    //                 int extraKms = Math.max(0, usedKm - maxKm);
    //                 double extraHourCharges = extraHours * extraHourRate;
    //                 double extraKmCharges = extraKms * extraKmRate;
    //                 // DA, parking, toll extra in full day
    //                 da = 300;   
    //                 // For demo, get parking/toll from request or set 0
    //                 try { parking = Double.parseDouble(response.getOrDefault("parking", "0").toString()); } catch (Exception e) { parking = 0; }
    //                 try { toll = Double.parseDouble(response.getOrDefault("toll", "0").toString()); } catch (Exception e) { toll = 0; }
    //                 subtotal = basePrice + extraHourCharges + extraKmCharges + da + parking + toll;
    //                 gst = subtotal * 0.05;
    //                 total = subtotal + gst;
    //             }
    //             // Build response
    //             Map<String, Object> transferDetails = new HashMap<>();
    //             transferDetails.put("package", packageNameUsed.isEmpty() ? "slab" : packageNameUsed);
    //             transferDetails.put("basePrice", basePrice);
    //             transferDetails.put("usedKm", usedKm);
    //             transferDetails.put("usedHours", usedHours);
    //             transferDetails.put("extraHourRate", extraHourRate);
    //             transferDetails.put("extraKmRate", extraKmRate);
    //             transferDetails.put("subtotal", subtotal);
    //             transferDetails.put("gst", gst);
    //             transferDetails.put("total", total);
    //             transferDetails.put("carType", carType);
    //             transferDetails.put("da", da);
    //             transferDetails.put("parking", parking);
    //             transferDetails.put("toll", toll);
    //             response.put("transferDetails", transferDetails);
    //             response.put("tripType", tripType);
    //             response.put("pickupLocation", pickupLocation);
    //             response.put("dropLocation", dropLocation);
    //             response.put("date", date);
    //             response.put("returndate", returndate);
    //             response.put("time", time);
    //             response.put("Endtime", Endtime);
    //             response.put("distance", calculatedDistance);
    //             response.put("cabinfo", cabser.getAll());
    //             response.put("days", 1);
    //             return ResponseEntity.ok(response);
    //         }
    
    //         if ("car-rental".equalsIgnoreCase(tripType)) {
    //             // --- All Car Rental Logic ---
    //             // Dynamic per km rate based on carType
    //             double perKmRate = 0.0;
    //             switch (carType.toLowerCase()) {
    //                 case "hatchback": perKmRate = 11.0; break;
    //                 case "sedan": perKmRate = 12.0; break;
    //                 case "sedanpremium": perKmRate = 13.0; break;
    //                 case "suv": perKmRate = 14.0; break;
    //                 case "suvplus": perKmRate = 26.0; break;
    //                 default: perKmRate = 12.0; // fallback
    //             }
    //             // Rental packages calculated dynamically
    //             class RentalPackage {
    //                 String name;
    //                 int maxHours;
    //                 int maxKm;
    //                 double price;
    //                 RentalPackage(String name, int maxHours, int maxKm, double price) {
    //                     this.name = name;
    //                     this.maxHours = maxHours;
    //                     this.maxKm = maxKm;
    //                     this.price = price;
    //                 }
    //             }
    //             List<RentalPackage> packages = Arrays.asList(
    //                 new RentalPackage("4hr/40km", 4, 40, 40 * perKmRate),
    //                 new RentalPackage("8hr/80km", 8, 80, 80 * perKmRate),
    //                 new RentalPackage("Full Day/300km", 24, 300, 300 * perKmRate)
    //             );
    //             double extraHourRate = 150.0; // Rs/hr
    
    //             // 1. Validate city
    //             if (!cityName.equalsIgnoreCase(cityName1)) {
    //                 response.put("error", "City package not applicable: Pickup and drop must be in the same city for rental.");
    //                 return ResponseEntity.ok(response);
    //             }
    
    //             // 2. Get user-selected package (for demo, pick first package; in real use, get from request)
    //             // Select package based on user input, default to first if not provided or invalid
    //             RentalPackage selectedPkg = packages.get(0);
    //             if (packageName != null && !packageName.isEmpty()) {
    //                 for (RentalPackage pkg : packages) {
    //                     if (pkg.name.equalsIgnoreCase(packageName.trim())) {
    //                         selectedPkg = pkg;
    //                         break;
    //                     }
    //                 }
    //             }
    //             // 3. Parse time (assume 'time' is in format HH:mm, and Endtime param is available)
    //             int usedHours = 0;
    //             try {
    //                 LocalTime start = LocalTime.parse(time);
    //                 LocalTime end = LocalTime.parse(Endtime);
    //                 usedHours = (int) ChronoUnit.HOURS.between(start, end);
    //                 if (usedHours <= 0) usedHours = selectedPkg.maxHours; // fallback
    //             } catch (Exception ex) {
    //                 usedHours = selectedPkg.maxHours;
    //             }
    //             int usedKm = calculatedDistance;
    //             // 4. Calculate extra charges
    //             int extraHours = Math.max(0, usedHours - selectedPkg.maxHours);
    //             int extraKms = Math.max(0, usedKm - selectedPkg.maxKm);
    //             double extraHourCharges = extraHours * extraHourRate;
    //             double extraKmCharges = extraKms * perKmRate;
    //             double basePrice = selectedPkg.price;
    //             double subtotal = basePrice + extraHourCharges + extraKmCharges;
    //             double gst = subtotal * 0.05;
    //             double total = subtotal + gst;
    //             // 5. Build detailed response
    //             Map<String, Object> rentalDetails = new HashMap<>();
    //             rentalDetails.put("package", selectedPkg.name);
    //             rentalDetails.put("basePrice", basePrice);
    //             rentalDetails.put("maxHours", selectedPkg.maxHours);
    //             rentalDetails.put("maxKm", selectedPkg.maxKm);
    //             rentalDetails.put("usedHours", usedHours);
    //             rentalDetails.put("usedKm", usedKm);
    //             rentalDetails.put("extraHours", extraHours);
    //             rentalDetails.put("extraHourCharges", extraHourCharges);
    //             rentalDetails.put("extraKms", extraKms);
    //             rentalDetails.put("extraKmCharges", extraKmCharges);
    //             rentalDetails.put("subtotal", subtotal);
    //             rentalDetails.put("gst", gst);
    //             rentalDetails.put("total", total);
    //             rentalDetails.put("carType", carType);
    //             rentalDetails.put("perKmRate", perKmRate);
    //             response.put("rentalDetails", rentalDetails);
    //             response.put("tripType", tripType);
    //             response.put("pickupLocation", pickupLocation);
    //             response.put("dropLocation", dropLocation);
    //             response.put("date", date);
    //             response.put("returndate", returndate);
    //             response.put("time", time);
    //             response.put("Endtime", Endtime);
    //             response.put("distance", calculatedDistance);
    //             response.put("cabinfo", cabser.getAll());
    //             response.put("days", 1);
    //             return ResponseEntity.ok(response);
    //         }
    

    //         List<CabInfo> cabs = cabser.getAll();

    //         response.put("tripType", tripType);
    //         response.put("pickupLocation", pickupLocation);
    //         response.put("dropLocation", dropLocation);
    //         response.put("date", date);
    //         response.put("returndate", returndate);
    //         response.put("time", time);
    //         response.put("distance", calculatedDistance);
    //         response.put("cabinfo", cabs);
    //         response.put("price", 10);
    //         response.put("tripinfo", tripinfo);
    //         response.put("days", days);

    //         return ResponseEntity.ok(response);

    //     } catch (Exception e) {
    //         response.put("error", "Failed to process request: " + e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    //     }
    // }

// for short route
    private int getShortRouteFare(int distance, String cabType) {
    int rate = 0;

    if (distance <= 50) {
        switch (cabType.toLowerCase()) {
            case "hatchback": rate = 15; break;
            case "sedan": rate = 16; break;
            case "sedanpremium": rate = 18; break;
            case "suv": rate = 20; break;
            case "suvplus": rate = 22; break;
            case "ertiga": rate = 19; break;
        }
    } else if (distance <= 100) {
        switch (cabType.toLowerCase()) {
            case "hatchback": rate = 14; break;
            case "sedan": rate = 15; break;
            case "sedanpremium": rate = 17; break;
            case "suv": rate = 19; break;
            case "suvplus": rate = 21; break;
            case "ertiga": rate = 18; break;
        }
    } else if (distance <= 150) {
        switch (cabType.toLowerCase()) {
            case "hatchback": rate = 13; break;
            case "sedan": rate = 14; break;
            case "sedanpremium": rate = 16; break;
            case "suv": rate = 18; break;
            case "suvplus": rate = 20; break;
            case "ertiga": rate = 17; break;
        }
    }

    return rate * distance;
}


private int calculateDynamicFare(int distance, String cabType) {
    double baseRate = 0, maxRate = 0;

    switch (cabType.toLowerCase()) {
        case "hatchback": baseRate = 18; maxRate = 20; break;
        case "sedan": baseRate = 20; maxRate = 22; break;
        case "sedanpremium": baseRate = 22; maxRate = 24; break;
        case "suv": baseRate = 24; maxRate = 26; break;
        case "suvplus": baseRate = 26; maxRate = 28; break;
        case "ertiga": baseRate = 28; maxRate = 30; break;
    }

    double totalFare = 0.0;

    for (int i = 1; i <= distance; i++) {
        double ratePerKm = baseRate + ((maxRate - baseRate) / 5.0) * (i - 1);
        totalFare += ratePerKm;
    }

    return (int) Math.round(totalFare);
}



   @PostMapping("/cab1")
public ResponseEntity<Map<String, Object>> processForm(
        @RequestParam("tripType") String tripType,
        @RequestParam("pickupLocation") String pickupLocation,
        @RequestParam("dropLocation") String dropLocation,
        @RequestParam("date") String date,
        @RequestParam(value = "Returndate", required = false) String returndate,
        @RequestParam("time") String time,
        @RequestParam(value = "distance", required = false) String distance
        // @RequestParam(value="package",required = false) String package
) {
    Map<String, Object> response = new HashMap<>();

    try {
        int calculatedDistance = 0;

        if (distance == null || distance.isEmpty() || distance.equals("0") || distance.equals("-1")) {
            calculatedDistance = getDistanceFromGoogleMaps(pickupLocation, dropLocation);
        } else {
            String numericString = distance.replaceAll("[^0-9.]", "");
            calculatedDistance = (int) Double.parseDouble(numericString);
        }

        List<Trip> tripinfo = new ArrayList<>();
        int days = 0;

        String cityName = extractCityName(userService.getLongNameByCity(dropLocation, apiKey), dropLocation);
        String cityName1 = extractCityName(userService.getLongNameByCity(pickupLocation, apiKey), pickupLocation);

        cityName = cityName.equals("Bengaluru") ? "Bangalore" : cityName;
        cityName1 = cityName1.equals("Bengaluru") ? "Bangalore" : cityName1;

        if ("oneWay".equalsIgnoreCase(tripType)) {
            List<onewayTrip> oneWayTrips = tripSer.getoneWayTripData(pickupLocation, dropLocation);

            if (oneWayTrips.isEmpty()) {
                oneWayTrips.add(createDefaultOneWayTrip());
            }

            for (onewayTrip o : oneWayTrips) {
                if (calculatedDistance <= 150) {
                 o.setHatchback(calculateDynamicFare(calculatedDistance, "hatchback"));
        o.setSedan(calculateDynamicFare(calculatedDistance, "sedan"));
        o.setSedanpremium(calculateDynamicFare(calculatedDistance, "sedanpremium"));
        o.setSuv(calculateDynamicFare(calculatedDistance, "suv"));
        o.setSuvplus(calculateDynamicFare(calculatedDistance, "suvplus"));
        o.setErtiga(calculateDynamicFare(calculatedDistance, "ertiga"));
                } else {
                    o.setHatchback(o.getHatchback() * calculatedDistance);
                    o.setSedan(o.getSedan() * calculatedDistance);
                    o.setSedanpremium(o.getSedanpremium() * calculatedDistance);
                    o.setSuv(o.getSuv() * calculatedDistance);
                    o.setSuvplus(o.getSuvplus() * calculatedDistance);
                    o.setErtiga(o.getErtiga() * calculatedDistance);
                }

                o.setDistance((double) calculatedDistance);
                tripinfo.add(o);
            }

        } else if ("roundTrip".equalsIgnoreCase(tripType)) {
            LocalDate localDate1 = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            LocalDate localDate2 = LocalDate.parse(returndate, DateTimeFormatter.ISO_DATE);
            days = (int) ChronoUnit.DAYS.between(localDate1, localDate2) + 1;

            if (distance == null || distance.isEmpty() || distance.equals("0") || distance.equals("-1")) {
                calculatedDistance = calculatedDistance * days;
            }

            int perKm = 300;
            int baseKm = perKm * days;

            List<roundTrip> roundTrips = tripSer.getRoundWayTripData(cityName, cityName1);

            if (roundTrips.isEmpty()) {
                roundTrips.add(createDefaultRoundTrip());
            }

            for (roundTrip t : roundTrips) {
                int hatchbackPrice;
                if (calculatedDistance <= baseKm) {
                    hatchbackPrice = perKm * t.getHatchback();
                } else {
                    hatchbackPrice = (perKm * t.getHatchback()) + (calculatedDistance - baseKm) * t.getHatchback();
                }

                int sedanPrice;
                if (calculatedDistance <= baseKm) {
                    sedanPrice = perKm * t.getSedan();
                } else {
                    sedanPrice = (perKm * t.getSedan()) + (calculatedDistance - baseKm) * t.getSedan();
                }

                int sedanPremiumPrice;
                if (calculatedDistance <= baseKm) {
                    sedanPremiumPrice = perKm * t.getSedanpremium();
                } else {
                    sedanPremiumPrice = (perKm * t.getSedanpremium()) + (calculatedDistance - baseKm) * t.getSedanpremium();
                }

                int suvPrice;
                if (calculatedDistance <= baseKm) {
                    suvPrice = perKm * t.getSuv();
                } else {
                    suvPrice = (perKm * t.getSuv()) + (calculatedDistance - baseKm) * t.getSuv();
                }

                int suvPlusPrice;
                if (calculatedDistance <= baseKm) {
                    suvPlusPrice = perKm * t.getSuvplus();
                } else {
                    suvPlusPrice = (perKm * t.getSuvplus()) + (calculatedDistance - baseKm) * t.getSuvplus();
                }

                int ertigaPrice;
                if (calculatedDistance <= baseKm) {
                    ertigaPrice = perKm * t.getErtiga();
                } else {
                    ertigaPrice = (perKm * t.getErtiga()) + (calculatedDistance - baseKm) * t.getErtiga();
                }

                t.setHatchback(hatchbackPrice);
                t.setSedan(sedanPrice);
                t.setSedanpremium(sedanPremiumPrice);
                t.setSuv(suvPrice);
                t.setSuvplus(suvPlusPrice);
                t.setErtiga(ertigaPrice);

                tripinfo.add(t);
            }
        }

        else if("transfer".equalsIgnoreCase(tripType)){

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

        JSONObject jsonResponse = (JSONObject) new org.json.simple.parser.JSONParser().parse(response.toString());
        JSONObject firstRow = (JSONObject) ((List<?>) jsonResponse.get("rows")).get(0);
        JSONObject firstElement = (JSONObject) ((List<?>) firstRow.get("elements")).get(0);
        JSONObject distanceObj = (JSONObject) firstElement.get("distance");

        long distanceInMeters = (long) distanceObj.get("value");
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


    public static onewayTrip getDefaultOneWayTrip() {
        return new onewayTrip(
                null, "", "", "", "",
                12, 15, 18, 21, 26,
                "", null, null, 0,15
        );
    }

    private onewayTrip createDefaultOneWayTrip() {
        return getDefaultOneWayTrip();
    }


    private roundTrip createDefaultRoundTrip() {
        return new roundTrip(
                null, "", "", "", "",
                11, 12, 13, 14, 26,15,
                "", null, null
        );
    }



    @GetMapping("/pricing")
    public ResponseEntity<Map<String, Object>> getPricing(
            @RequestParam("sourceCity") String sourceCity,
            @RequestParam("destCity") String destCity,
            @RequestParam(value = "tripType", required = false, defaultValue = "oneWay") String tripType
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            CityExtractionService.CityStateResult sourceResult = cityExtractionService.extractCityAndState(sourceCity);
            CityExtractionService.CityStateResult destResult = cityExtractionService.extractCityAndState(destCity);

            String normalizedSourceCity = sourceResult.getCity().equals("Bengaluru") ? "Bangalore" : sourceResult.getCity();
            String normalizedDestCity = destResult.getCity().equals("Bengaluru") ? "Bangalore" : destResult.getCity();

            List<Trip> tripinfo = new ArrayList<>();

            if ("oneWay".equals(tripType)) {
                List<onewayTrip> oneWayTrips = tripSer.getOneWayTripsWithDefaults(
                        normalizedSourceCity,
                        sourceResult.getState(),
                        normalizedDestCity,
                        destResult.getState()
                );

                for (onewayTrip o : oneWayTrips) {
                    tripinfo.add(o);
                }
            } else if ("roundTrip".equals(tripType)) {
                // UNCHANGED: Keep original roundTrip logic
                tripinfo = tripSer.getRoundTrip(normalizedDestCity, normalizedSourceCity);
                if (tripinfo.isEmpty()) {
                    tripinfo.add(createDefaultRoundTrip());
                }
            }

            int dynamicDistance;
            try {
                dynamicDistance = getDistanceFromGoogleMaps(sourceCity, destCity);
                if ("roundTrip".equals(tripType)) {
                    dynamicDistance = dynamicDistance * 2;
                }
            } catch (Exception e) {
                dynamicDistance = 100;
                if ("roundTrip".equals(tripType)) {
                    dynamicDistance = 200;
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

    @PostMapping("/invoice1")
    public Map<String, Object> bookCab(
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
            @RequestParam(required = false) Integer userId
    ) {

        try {
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
            booking.setUserDrop(dropLocation);
            booking.setUserPickup(pickupLocation);
            booking.setUserTripType(tripType);
            booking.setBookingType("website");
            booking.setDate(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
            booking.setCar(modelType);
            booking.setBaseAmount(price);
            booking.setAmount(Integer.parseInt(total));
            booking.setGst(Integer.parseInt(gst));
            booking.setServiceCharge(Integer.parseInt(service));

            if ("roundTrip".equals(tripType) && returndate != null) {
                booking.setReturnDate(LocalDate.parse(returndate, DateTimeFormatter.ISO_DATE));
            }

            if (userId != null) {
                CarRentalUser carRentalUser = this.carRentalRepository.findById(userId).orElse(null);
                booking.setCarRentalUser(carRentalUser);
            }

            String bookid = "WTL" + System.currentTimeMillis();
            booking.setBookingId(bookid);
            booking.setBookid(bookid);

            ser.saveBooking(booking);

            callExternalBookingApi(
                    date, name, email, modelName, distance, phone,
                    pickupLocation, dropLocation, time, returndate,
                    days, tripType, total, price, service, gst, driverrate);

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

    @PostMapping("/websiteBooking")
    public ResponseEntity<Map<String, Object>> createWebsiteBooking(
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
            @RequestParam String phone) {

        try {
            String bookid = "WTL" + System.currentTimeMillis();

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
            booking.setUserDrop(dropLocation);
            booking.setUserPickup(pickupLocation);
            booking.setUserTripType(tripType);
            booking.setBookingType("website");
            booking.setDate(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
            booking.setCar(modelType);
            booking.setAmount(Integer.parseInt(price));
            booking.setGst(Integer.parseInt(gst));
            booking.setServiceCharge(Integer.parseInt(service));

            if ("roundTrip".equals(tripType) || "round-trip".equals(tripType)) {
                try {
                    if (returndate != null && !returndate.trim().isEmpty()) {
                        LocalDate returnDate = LocalDate.parse(returndate, DateTimeFormatter.ISO_DATE);
                        booking.setReturnDate(returnDate);
                    } else {
                        booking.setReturnDate(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
                    }
                } catch (Exception e) {
                    booking.setReturnDate(LocalDate.parse(date, DateTimeFormatter.ISO_DATE));
                }
            }

            booking.setBookingId(bookid);
            booking.setBookid(bookid);

            ser.saveBooking(booking);

            sendConfirmationEmail(name, email, bookid, pickupLocation,
                    dropLocation, tripType, date, time, total);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "bookingId", bookid,
                    "message", "Booking created successfully"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Booking creation failed: " + e.getMessage()));
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
        String message = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Booking Confirmation</title>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; color: #333; background-color: #f5f5f5;\">\n" +
                "    <table role=\"presentation\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "        <tr>\n" +
                "            <td style=\"padding: 20px 0; text-align: center; background-color: #1a1f2e;\">\n" +
                "                <h1 style=\"color: #ffffff; margin: 0;\">WTL Cab Service</h1>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"padding: 20px;\">\n" +
                "                <table role=\"presentation\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.05);\">\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 30px 30px 20px 30px;\">\n" +
                "                            <h2 style=\"color: #1a1f2e; margin-top: 0;\">Booking Confirmation</h2>\n" +
                "                            <p style=\"font-size: 16px; line-height: 24px; margin-bottom: 20px;\">Hello " + name + ",</p>\n" +
                "                            <p style=\"font-size: 16px; line-height: 24px; margin-bottom: 25px;\">Your booking has been confirmed. Here are your booking details:</p>\n" +
                "                            \n" +
                "                            <table role=\"presentation\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border-collapse: separate; border-spacing: 0; border: 1px solid #e0e0e0; border-radius: 6px; overflow: hidden; margin-bottom: 25px;\">\n" +
                "                                <tr style=\"background-color: #f8f9fa;\">\n" +
                "                                    <th style=\"padding: 12px 15px; text-align: left; border-bottom: 1px solid #e0e0e0; width: 40%;\">Booking Details</th>\n" +
                "                                    <th style=\"padding: 12px 15px; text-align: left; border-bottom: 1px solid #e0e0e0; width: 60%;\">Information</th>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding: 12px 15px; border-bottom: 1px solid #e0e0e0; font-weight: bold;\">Drop Location</td>\n" +
                "                                    <td style=\"padding: 12px 15px; border-bottom: 1px solid #e0e0e0;\">" + dropLocation + "</td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding: 12px 15px; border-bottom: 1px solid #e0e0e0; font-weight: bold;\">Trip Type</td>\n" +
                "                                    <td style=\"padding: 12px 15px; border-bottom: 1px solid #e0e0e0;\">" + tripType + "</td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding: 12px 15px; border-bottom: 1px solid #e0e0e0; font-weight: bold;\">Date</td>\n" +
                "                                    <td style=\"padding: 12px 15px; border-bottom: 1px solid #e0e0e0;\">" + date + "</td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding: 12px 15px; border-bottom: 1px solid #e0e0e0; font-weight: bold;\">Time</td>\n" +
                "                                    <td style=\"padding: 12px 15px; border-bottom: 1px solid #e0e0e0;\">" + time + "</td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding: 12px 15px; font-weight: bold; background-color: #f8f9fa;\">Amount Paid</td>\n" +
                "                                    <td style=\"padding: 12px 15px; font-weight: bold; color: #2e7d32; background-color: #f8f9fa;\">" + total + "</td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                            \n" +
                "                            <p style=\"font-size: 16px; line-height: 24px; margin-bottom: 20px;\">Thank you for choosing our service. We hope you have a comfortable journey!</p>\n" +
                "<p style=\"font-size: 14px; margin: 25px 0 10px 0;\"><strong>Terms and Conditions:</strong></p>\n" +
                "<ul style=\"font-size: 13px; color: #666; margin-bottom: 25px;\">\n" +
                "  <li>Toll charges, parking fees, and other taxes are not included and will be charged as applicable.</li>\n" +
                "  <li>Prices may be extended in case of route changes, additional stops, or waiting time.</li>\n" +
                "  <li>Please refer to our website or contact support for detailed terms and conditions.</li>\n" +
                "</ul>\n" +
                "                            <p style=\"font-size: 16px; line-height: 24px; margin-bottom: 0;\">Best regards,<br>WTL Cab Service Team</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td style=\"padding: 20px; text-align: center; font-size: 12px; color: #666;\">\n" +
                "                <p>&copy; 2025 WTL Cab Service. All rights reserved.</p>\n" +
                "                <p>If you have any questions, please contact our customer support at <a href=\"mailto:support@wtlcabs.com\" style=\"color: #1a1f2e;\">support@wtlcabs.com</a></p>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>";
 
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

    @PutMapping("/enterOtpTimePre/{id}")
    public Booking updateEnterOtpTimePreTrip(@PathVariable int id){
        return this.ser.updateDriverEnterOtpTimePreStarted(id);
    }

    @PutMapping("/enterOtpTimePost/{id}")
    public Booking udpateEnterOtpTimePostTrip(@PathVariable int id){
        return this.ser.updateDriverEnterOtpTimePostStarted(id);
    }

    @PutMapping("startingmeter/{id}")
    public Booking startOdoometerStarting(@PathVariable int id, @RequestParam String meter){
        return this.ser.enterOdooMeterStarted(id, meter);
    }

    @PutMapping("/endTimer/{id}")
    public Booking endOdoometerEnding(@PathVariable int id, @RequestParam String meter){
        return this.ser.enterOdoometerEnding(id, meter);
    }


    @PutMapping("/udpatePrice/{id}")
    public Booking updatePrice(@PathVariable int id, @RequestParam int amount){
        return this.ser.updatePrice(id, amount);
    }

    @PostMapping("/createFirstPage")
    public Visitors visitorFirstPage(@RequestBody Visitors visitors){
        return this.visitorService.createFirstPage(visitors);
    }

    @PutMapping("/{id}/secondPage")
    public Visitors createSecondPage(@PathVariable int id, @RequestBody Visitors visitors){
        return this.visitorService.createSecondPage(id, visitors);
    }

    @GetMapping("/getAllVisitor")
    public List<Visitors> getAllVisitor(){
        return this.visitorService.getAllVisitor();
    }


}