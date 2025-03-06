package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

    @Autowired
    private TwilioService twilioService;

    @PostMapping("/send")
    public String sendWhatsAppMessage(@RequestBody WhatsAppMessageRequest request) {
        return twilioService.sendWhatsAppMessage(request.getTo());
    }
}
