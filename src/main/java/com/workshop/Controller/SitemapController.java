package com.workshop.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Controller
@RequestMapping("/sitemap.xml")
public class SitemapController {

    @GetMapping("")
    public void generateSitemap(HttpServletResponse response) throws IOException {
        // Set content type to XML
        response.setContentType("application/xml");

        // Initialize a StringBuilder to build the XML structure
        StringBuilder sitemap = new StringBuilder();

        // Write the XML header
        sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        // Define the URLs and their properties
        String[][] urls = {
            {"https://worldtriplink.com/", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/about", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/services", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Pune", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Mumbai", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Kolhapur", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Satara", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Nashik", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Ratnagiri", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Shirdi", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Ahmednagar", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Beed", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Lonavala", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Akola", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Sindhudurg", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Latur", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Osmanabad", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Nanded", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Washim", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Wardha", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Palghar", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Chandarpur", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Gondia", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Gadchiroli", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Amravati", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Cab-Service-Aurangabad", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Corporate-Employee-Transport-Services-Delhi", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Corporate-Cab-Service-Pune", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Monthly-Cab-Service-Bangalore", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Corporate-Cab-Service-Mumbai", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Corporate-Cab-Services-Telangana", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Corporate-Cab-Services-Chennai", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Corporate-Cab-Services-Indore", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/Corporate-Cab-Services-Surat", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/state/Maharashtra", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/Goa", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/Delhi", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/Gujarat", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/MadhyaPradesh", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/Karnataka", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/Telangana", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/UttarPradesh", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/Rajasthan", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/WestBengal", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/HP", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/Kerala", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/TamilNadu", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/AndhraPradesh", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/Punjab", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/Odisha", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/Chhattisgarh", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/state/ArunachalPradesh", "2025-02-25T15:13:01+01:00", "0.8"},
            {"https://worldtriplink.com/privacy-policy", "2025-02-25T15:13:01+01:00", "1.0"},
            {"https://worldtriplink.com/state/Madhya%20Pradesh", "2025-02-25T15:13:06+01:00", "0.8"},
            {"https://worldtriplink.com/state/Uttar%20Pradesh", "2025-02-25T15:13:06+01:00", "0.8"},
            {"https://worldtriplink.com/state/West%20Bengal", "2025-02-25T15:13:06+01:00", "0.8"},
            {"https://worldtriplink.com/state/Tamil%20Nadu", "2025-02-25T15:13:06+01:00", "0.8"},
            {"https://worldtriplink.com/state/Andhra%20Pradesh", "2025-02-25T15:13:06+01:00", "0.8"},
            {"https://worldtriplink.com/state/Arunachal%20Pradesh", "2025-02-25T15:13:06+01:00", "0.8"},
            {"https://worldtriplink.com/tour-search-result.html", "2025-02-25T15:13:12+01:00", "1.0"},
            {"https://worldtriplink.com/state/index.html", "2025-02-25T15:13:14+01:00", "0.8"},
            {"https://worldtriplink.com/state/activity-search-result.html", "2025-02-25T15:13:14+01:00", "0.8"},
            {"http://worldtriplink.com/", "2025-02-25T15:13:17+01:00", "1.0"}
        };

        // Loop through the URL data and build the XML
        for (String[] urlData : urls) {
            sitemap.append("<url>");
            sitemap.append("<loc>").append(urlData[0]).append("</loc>");
            sitemap.append("<lastmod>").append(urlData[1]).append("</lastmod>");
            sitemap.append("<changefreq>").append(urlData[2].equals("1.0") ? "daily" : "monthly").append("</changefreq>");
            sitemap.append("<priority>").append(urlData[2]).append("</priority>");
            sitemap.append("</url>");
        }

        // Close the URL set and the XML document
        sitemap.append("</urlset>");

        // Write the XML content to the HTTP response
        response.getWriter().write(sitemap.toString());
    }
}
