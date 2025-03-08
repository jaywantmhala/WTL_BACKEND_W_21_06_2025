package com.example.demo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicle")
@CrossOrigin(origins = "*") // Allow frontend to access the backend
public class VehicleController {
	
	
	@Autowired
    private VehicleScraper vehicleScraper;

	 @GetMapping("/details")
	    public Map<String, String> getVehicleDetails(@RequestParam String first, @RequestParam String second) {
	        return vehicleScraper.getVehicleDetails(first, second);
	    }
}
