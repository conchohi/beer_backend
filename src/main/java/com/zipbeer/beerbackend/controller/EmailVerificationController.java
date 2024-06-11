package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.EmailCertificationDto;
import com.zipbeer.beerbackend.repository.UserRepository;
import com.zipbeer.beerbackend.service.AuthService;
import com.zipbeer.beerbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class EmailVerificationController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    @PostMapping("/email-verify")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        emailService.sendVerificationEmail(email);
        return ResponseEntity.ok("Verification email sent.");
    }

    @PostMapping("/email-verify-check")
    public ResponseEntity<String> verifyEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean isVerified = emailService.verifyEmail(email, code);

        if (isVerified) {
            return ResponseEntity.ok("Email verified.");
        } else {
            return ResponseEntity.status(400).body("Invalid verification code.");
        }
    }

    @PostMapping("/retrieve-ids")
    public ResponseEntity<?> retrieveUserIds(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        boolean isVerified = emailService.verifyEmail(email, code);

        if (isVerified) {
            List<String> userIds = userRepository.findUserIdsByEmail(email);
            if (userIds.isEmpty()) {
                return ResponseEntity.status(404).body("아이디가 존재하지 않습니다.");
            }
            return ResponseEntity.ok(userIds);
        } else {
            return ResponseEntity.status(400).body("인증번호가 일치하지 않습니다.");
        }
    }


    @PostMapping("/send-password-reset-code")
    public ResponseEntity<?> sendPasswordResetCode(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String email = request.get("email");
        return authService.sendPasswordResetCode(userId, email);
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody EmailCertificationDto dto) {
        return authService.checkCertification(dto);
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        return authService.updatePassword(userId, email, newPassword);
    }
}
