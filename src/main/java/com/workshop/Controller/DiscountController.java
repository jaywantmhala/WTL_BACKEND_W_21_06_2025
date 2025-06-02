package com.workshop.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workshop.Entity.Discount;
import com.workshop.Service.DiscountService;

@RestController
@RequestMapping("/discount")
public class DiscountController {
    

    @Autowired
    private DiscountService discountService;

    @PostMapping("/create")
    public Discount createDiscount(@RequestBody Discount discount){
        return this.discountService.createDiscount(discount);
    }

    @GetMapping("/getAll")
    public List<Discount> getAllCouponCode(){
        return this.discountService.getAllCouponCode();
    }

    @GetMapping("/getById/{id}")
    public Discount getCouponById(@PathVariable int id){
        return this.discountService.getCouponCodeById(id);
    }

    @PutMapping("/update/{id}")
    public Discount updateCDiscount(@RequestBody Discount discount, @PathVariable int id){
        return this.discountService.updateCouponCode(discount, id);
    }   

    @DeleteMapping("/delete/{id}")
    public void deleteCouponCode(@PathVariable int id){
        this.discountService.deleteCouponCode(id);
    }
    

}
