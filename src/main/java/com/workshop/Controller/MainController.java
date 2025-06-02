package com.workshop.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.workshop.Entity.City;
import com.workshop.Service.CityService;

@Controller
public class MainController {

	// @GetMapping("/")
	// public String showHome() {
	// return "home";
	// }

	@Autowired
	CityService cityser;

	@GetMapping("/user/dashboard")
	public String patientDashboard() {
		// TODO: Implement logic to display patient dashboard
		return "user";
	}


	@GetMapping("/contact")
	public String contact() {
		return "contact1";
	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}
	

	@GetMapping("/services")
	public String services() {
		return "services";
	}

	@GetMapping("/Cab-Service-Pune")
	public String pune() {
		return "Cab-Service-Pune";
	}

	@GetMapping("/error")
	public String error() {
		return "error";
	}

	@GetMapping("/Cab-Service-Osmanabad")
	public String osmanabad() {
		return "Cab-Service-Osmanabad";
	}

	@GetMapping("/Cab-Service-Washim")
	public String washim() {
		return "Cab-Service-Washim";
	}

	@GetMapping("/Cab-Service-Palghar")
	public String palghar() {
		return "Cab-Service-Palghar";
	}

	@GetMapping("/Cab-Service-Gondia")
	public String gondia() {
		return "Cab-Service-Gondia";
	}

	@GetMapping("/Cab-Service-Amravati")
	public String amravati() {
		return "Cab-Service-Amravati";
	}

	@GetMapping("/Cab-Service-Akola")
	public String akola() {
		return "Cab-Service-Akola";
	}

	@GetMapping("/Cab-Service-Latur")
	public String latur() {
		return "Cab-Service-Latur";
	}

	@GetMapping("/Cab-Service-Nanded")
	public String nanded() {
		return "Cab-Service-Nanded";
	}

	@GetMapping("/Cab-Service-Wardha")
	public String wardha() {
		return "Cab-Service-Wardha";
	}

	@GetMapping("/Cab-Service-Chandarpur")
	public String chandarpur() {
		return "Cab-Service-Chandarpur";
	}

	@GetMapping("/Cab-Service-Gadchiroli")
	public String gadchiroli() {
		return "Cab-Service-Gadchiroli";
	}

	@GetMapping("/Cab-Service-Aurangabad")
	public String aurangabad() {
		return "Cab-Service-Aurangabad";
	}

	@GetMapping("/Cab-Service-Mumbai")
	public String mumbai() {
		return "Cab-Service-Mumbai";
	}

	@GetMapping("/Panchagani")
	public String Panchagani() {
		return "Panchagani";
	}

	@GetMapping("/Cab-Service-Nashik")
	public String nashik() {
		return "Cab-Service-Nashik";
	}

	@GetMapping("/Cab-Service-Satara")
	public String satara() {
		return "Cab-Service-Satara";
	}

	@GetMapping("/Cab-Service-Kolhapur")
	public String kolhapur() {
		return "Cab-Service-Kolhapur";
	}

	@GetMapping("/Cab-Service-Shirdi")
	public String shirdi() {
		return "Cab-Service-Shirdi";
	}

	@GetMapping("/Cab-Service-Ahmednagar")
	public String ahmednagar() {
		return "Cab-Service-Ahmednagar";
	}

	@GetMapping("/Sangamner")
	public String sangamner() {
		return "Sangamner";
	}

	@GetMapping("/Cab-Service-Ratnagiri")
	public String ratnagiri() {
		return "Cab-Service-Ratnagiri";
	}

	@GetMapping("/Cab-Service-Jalna")
	public String jalana() {
		return "Cab-Service-Jalana";
	}

	@GetMapping("/Cab-Service-Beed")
	public String beed() {
		return "Cab-Service-Beed";
	}

	@GetMapping("/Cab-Service-Sindhudurg")
	public String sindhudurg() {
		return "Cab-Service-Sindhudurg";
	}

	@GetMapping("/Cab-Service-Lonavala")
	public String lonavala() {
		return "Cab-Service-Lonavala";
	}

	@GetMapping("/Trimbkeshwar")
	public String Trimbkeshwar() {
		return "Trimbkeshwar";
	}

	@GetMapping("/Lgatpuri")
	public String Lgatpuri() {
		return "Lgatpuri";
	}

	@GetMapping("/Alibag")
	public String Alibag() {
		return "Alibag";
	}

	@GetMapping("/Dapoli")
	public String Dapoli() {
		return "Dapoli";
	}

	@GetMapping("/Mahabaleshwar")
	public String Mahabaleshwar() {
		return "Mahabaleshwar";
	}

	@GetMapping("/Une")
	public String Une() {
		return "Une";
	}

	@GetMapping("/Bhimashankar")
	public String bhimashankar() {
		return "Bhimashankar";
	}

	// @GetMapping("/Corporate")
	// public String Corporate() {
	// 	return "Corporate";
	// }

	// @GetMapping("/Corporate-Cab-Service-Mumbai")
	// public String Mumbai1() {
	// 	return "Corporate-Cab-Service-Mumbai";
	// }

	// @GetMapping("/Corporate2")
	// public String Bangalore1() {
	// 	return "Corporate2";
	// }

	// @GetMapping("/Corporate-Cab-Service-Pune")
	// public String Pune1() {
	// 	return "Corporate-Cab-Service-Pune";
	// }

	// @GetMapping("/Telangana")
	// public String Telangana1() {
	// 	return "Telangana";
	// }

	// @GetMapping("/Corporate5")
	// public String Chennai1() {
	// 	return "Corporate5";
	// }

	

	// @GetMapping("/Corporate6")
	// public String Indor1() {
	// 	return "Corporate6";
	// }

	// @GetMapping("/Corporate7")
	// public String Surat1() {
	// 	return "Corporate7";
	// }


	@GetMapping("/Corporate-Employee-Transport-Services-Delhi")
	public String Corporate() {
		return "Corporate-Employee-Transport-Services-Delhi";
	}

	@GetMapping("/Corporate-Cab-Service-Mumbai")
	public String Mumbai1() {
		return "Corporate-Cab-Service-Mumbai";
	}

	@GetMapping("/Monthly-Cab-Service-Bangalore")
	public String Bangalore1() {
		return "Monthly-Cab-Service-Bangalore";
	}

	@GetMapping("/Corporate-Cab-Service-Pune")
	public String Pune1() {
		return "Corporate-Cab-Service-Pune";
	}
	
	
	@GetMapping("/Corporate-Cab-Services-Telangana")
	public String Telangana1() {
		return "Corporate-Cab-Services-Telangana";
	}



	@GetMapping("/Corporate-Cab-Services-Chennai")
	public String Chennai1() {
		return "Corporate-Cab-Services-Chennai";
	}

	@GetMapping("/Corporate-Cab-Services-Indore")
	public String Indor1() {
		return "Corporate-Cab-Services-Indore";
	}



	@GetMapping("/Corporate-Cab-Services-Surat")
	public String Surat1() {
		return "Corporate-Cab-Services-Surat";
	}



	@GetMapping("/Airport")
	public String Delhi2() {
		return "Airport";
	}



	@GetMapping("/Airport1")
	public String Mumbai2() {
		return "Airport1";
	}

	


	@GetMapping("/Airport2")
	public String Bangalore2() {
		return "Airport2";
	}

	@GetMapping("/Airport3")
	public String Pune2() {
		return "Airport3";
	}

	@GetMapping("/Airport4")
	public String Hyderabad2() {
		return "Airport4";
	}

	@GetMapping("/Airport5")
	public String Chennai2() {
		return "Airport5";
	}

	@GetMapping("/Airport6")
	public String Indor2() {
		return "Airport6";
	}

	@GetMapping("/Airport7")
	public String Surat2() {
		return "Airport7";
	}

	@GetMapping("/Bangalore")
	public String Bangalore() {
		return "Bangalore";
	}

	@GetMapping("/Goa")
	public String Goa() {
		return "Goa";
	}

	@GetMapping("/Delhi")
	public String Delhi() {
		return "Delhi";
	}

	@GetMapping("/Surat")
	public String Surat() {
		return "Surat";
	}

	@GetMapping("/Corporate-Cab-Services-Indoree")
	public String Indor() {
		return "Indor";
	}

	@GetMapping("/Pune")
	public String Pune() {
		return "Pune";
	}

	@GetMapping("/city/{name}")
	public String citt(@PathVariable String name, Model model) {
		// System.out.println(name);
		List<City> cityinfo = cityser.findAll();
		City city = cityser.findCityByName(name);
		System.out.println(city);
		model.addAttribute("city", city);
		model.addAttribute("cityinfo", cityinfo);
		return "city";
	}

	// @GetMapping("/city/{name}")
	// public String city(@PathVariable String name, Model model) {
	// // List of cities
	// System.out.println(name);
	// try {
	// List<String> cities = Arrays.asList(
	// "Pune", "Mumbai", "Kolhapur", "Satara", "Nashik", "Ratnagiri",
	// "Shirdi", "Ahmednagar", "Beed", "Jalna", "Lonavala", "Akola",
	// "Sindhudurg", "Latur", "Osmanabad", "Nanded", "Washim", "Wardha",
	// "Palghar", "Chandrapur", "Gondia", "Gadchiroli", "Amravati",
	// "Aurangabad", "Bhandara", "Buldhana", "Dhule", "Hingoli", "Nagpur",
	// "Parbhani", "Sangli", "Raigad", "Solapur", "Thane", "Yavatmal");

	// // Check if the city name exists in the list

	// if (cities.contains(name)) {
	// model.addAttribute("city", name);
	// return "Pune"; // Returns the dynamic HTML page based on city
	// } else {
	// // Return an error or fallback page
	// return "Pune"; // Optional: You can create a page for not found cities
	// }
	// } catch (Exception e) {
	// System.out.println("+++++++++++++++++++++"+e.getMessage());
	// return "error ouccur in city" + e.getMessage();
	// // TODO: handle exception
	// }
	// }

	@GetMapping("/citytocity/{name}")
	public String cityRange(@PathVariable String name, Model model) {
		// Your logic to handle the city range
		// You can split the name parameter to get both city names

		String[] cityNames = name.split("_to_");
		String name1 = cityNames[0];
		String name2 = cityNames[1];
		// System.out.println(name1+" "+name2);
		List<City> cityinfo = cityser.findAll();
		model.addAttribute("cityinfo", cityinfo);
		// Assuming you have a service method like findCityByName
		City city1 = cityser.findCityByName(name1);
		// System.out.println(city1);
		City city2 = cityser.findCityByName(name2);
		// System.out.println(city2);
		model.addAttribute("city1", city1);
		model.addAttribute("city2", city2);
		// System.out.println(cityinfo);
		return "citytocity";
	}

	@GetMapping("/state/{name}")
	public String state(@PathVariable String name, Model model) {
		// System.out.println("check.............................................");
		List<City> city = cityser.findCityByState(name);
		// System.out.println(city);
		model.addAttribute("cityinfo", city);
		model.addAttribute("state", name);
		// model.addAttribute("cityinfo", cityinfo);
		return "state";
	}
	@Controller
public class PrivacyPolicyController {

    @GetMapping("/privacy-policy")
    public String showPrivacyPolicy() {
        return "privacy-policy"; // This corresponds to a view (e.g., `privacy-policy.html` or `privacy-policy.jsp`).
    }
	@GetMapping("/tour-search-result.html")
    public String showtoursearchresult() {
        return "tour-search-result.html"; 
    }
}
	
}
