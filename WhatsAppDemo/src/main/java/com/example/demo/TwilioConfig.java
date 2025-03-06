package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.whatsapp-from}")
    private String whatsappFrom;

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getWhatsappFrom() {
        return whatsappFrom;
    }
}
