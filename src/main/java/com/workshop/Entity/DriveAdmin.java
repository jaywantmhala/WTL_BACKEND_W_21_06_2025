package com.workshop.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class DriveAdmin {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String DriverName; 
	
	private String ContactNo;
	
	private String AltMobNum;
	
	private String emailId;
	
	private String Adress;
	
	private String aadhaNo;
	
	private String drLicenseNo;
	
	private String pvcNo2;
	
	private String DriverImgSelfie; //Image with filename & path
	
	private String Aadhar; //Image with filename & path
	
	private String DrLicenceNum; //Image with filename & path
	
	private String PvcNo; //Image with filename & path
	
	private String otherDetails;
	
	private String status;


	@OneToMany(mappedBy = "driveAdmin")
	@JsonBackReference
	private List<Booking> booking;

	public DriveAdmin() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public DriveAdmin(int id, String driverName, String contactNo, String altMobNum, String emailId, String adress,
			String aadhaNo, String drLicenseNo, String pvcNo, String driverImgSelfie, String aadhar,
			String drLicenceNum, String pvcNo2, String otherDetails, String status, List<Booking> booking) {
		super();
		this.id = id;
		DriverName = driverName;
		ContactNo = contactNo;
		AltMobNum = altMobNum;
		this.emailId = emailId;
		Adress = adress;
		this.aadhaNo = aadhaNo;
		this.drLicenseNo = drLicenseNo;
		this.pvcNo2 = pvcNo2;
		DriverImgSelfie = driverImgSelfie;
		Aadhar = aadhar;
		DrLicenceNum = drLicenceNum;
		PvcNo = pvcNo2;
		this.otherDetails = otherDetails;
		this.status=status;
		this.booking=booking;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDriverName() {
		return DriverName;
	}

	public void setDriverName(String driverName) {
		DriverName = driverName;
	}

	public String getContactNo() {
		return ContactNo;
	}

	public void setContactNo(String contactNo) {
		ContactNo = contactNo;
	}

	public String getAltMobNum() {
		return AltMobNum;
	}

	public void setAltMobNum(String altMobNum) {
		AltMobNum = altMobNum;
	}

	public String getAdress() {
		return Adress;
	}

	public void setAdress(String adress) {
		Adress = adress;
	}

	public String getDriverImgSelfie() {
		return DriverImgSelfie;
	}

	public void setDriverImgSelfie(String driverImgSelfie) {
		DriverImgSelfie = driverImgSelfie;
	}

	public String getAadhar() {
		return Aadhar;
	}

	public void setAadhar(String aadhar) {
		Aadhar = aadhar;
	}

	public String getDrLicenceNum() {
		return DrLicenceNum;
	}

	public void setDrLicenceNum(String drLicenceNum) {
		DrLicenceNum = drLicenceNum;
	}

	public String getPvcNo() {
		return PvcNo;
	}

	public void setPvcNo(String pvcNo) {
		PvcNo = pvcNo;
	}

	public String getOtherDetails() {
		return otherDetails;
	}

	public void setOtherDetails(String otherDetails) {
		this.otherDetails = otherDetails;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}



	public String getAadhaNo() {
		return aadhaNo;
	}



	public void setAadhaNo(String aadhaNo) {
		this.aadhaNo = aadhaNo;
	}



	public String getDrLicenseNo() {
		return drLicenseNo;
	}



	public void setDrLicenseNo(String drLicenseNo) {
		this.drLicenseNo = drLicenseNo;
	}



	public String getPvcNo2() {
		return pvcNo2;
	}



	public void setPvcNo2(String pvcNo2) {
		this.pvcNo2 = pvcNo2;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public List<Booking> getBooking() {
		return booking;
	}



	public void setBooking(List<Booking> booking) {
		this.booking = booking;
	} 
	
	
	
	
	
	
	
}
