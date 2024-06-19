package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.provider.EmailProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private EmailProvider emailProvider;


    private Map<String, String> emailVerificationMap = new HashMap<>();

    public void sendVerificationEmail(String to) {
        String verificationCode = generateVerificationCode();
        emailVerificationMap.put(to, verificationCode);

        boolean isSent = emailProvider.sendCertificationMail(to, verificationCode);
        if (!isSent) {
            throw new RuntimeException("Failed to send verification email.");
        }
    }

    public boolean verifyEmail(String email, String code) {
        return code.equals(emailVerificationMap.get(email));
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000; // Generates a random number between 100000 and 999999
        return String.valueOf(code);
    }
}
