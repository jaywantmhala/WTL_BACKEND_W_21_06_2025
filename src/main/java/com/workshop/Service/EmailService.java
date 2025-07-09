package com.workshop.Service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public boolean sendEmail(String message, String subject, String to) {
        boolean f = false;
        String from = "contactwtltourism@gmail.com"; // Replace with your email
        String host = "smtp.gmail.com";

        // Set up properties for the mail session
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", 465); // Use 465 for SSL
        properties.put("mail.smtp.ssl.enable", true);
        properties.put("mail.smtp.auth", true);

        // Create a session with email credentials (username and password)
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("contactwtltourism@gmail.com", "vweq fehl equa ydwf"); // Replace with
                                                                                                       // actual app
                                                                                                //f password
            }

        });

        session.setDebug(true);

        // Create a MimeMessage for the email
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom(from);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(message, "text/html"); // HTML content for the email body

            // Send the email
            Transport.send(mimeMessage);
            f = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
}