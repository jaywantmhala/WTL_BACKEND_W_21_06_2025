package com.example.demo;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Autowired
    private TwilioConfig twilioConfig;

    public String sendWhatsAppMessage(String to) {
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());

        // Static message
        String body = "Name: Jaywant Mhala\nCity: Pune\nEducation: MCA";

        // Send the message
        Message message = Message.creator(
                new PhoneNumber("whatsapp:" + to), // Recipient's WhatsApp number
                new PhoneNumber(twilioConfig.getWhatsappFrom()), // Twilio WhatsApp number
                body) // Static message body
                .create();

        return message.getSid();
    }
}