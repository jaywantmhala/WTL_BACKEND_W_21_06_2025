package com.workshop.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.Entity.Enquiry;
import com.workshop.Repo.EnquiryRepo;

@Service
public class EnquiryService {
    

    @Autowired
    private EnquiryRepo repo;



    public Enquiry save(Enquiry enquiry){
        LocalDateTime now = LocalDateTime.now();
        enquiry.setTimeStamp(now.toString());
       return this.repo.save(enquiry);
    }

    public List<Enquiry> getAllEnquiry(){
        return this.repo.findAll();
    }

    public Enquiry getEnquiryById(int id){
        return this.repo.findById(id).get();
    }

    public void deleteEnquiry(int id){
        this.repo.deleteById(id);
    }




}
