package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.EmailCertificationDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    boolean idCheck(String id);
    ResponseEntity<?> join(UserDto dto);
    ResponseEntity<?> emailCertification(EmailCertificationDto dto);
    ResponseEntity<?> checkCertification(EmailCertificationDto dto);
    ResponseEntity<? super ResponseDto> findIdByEmail(String email);
    ResponseEntity<?> sendPasswordResetCode(String userId, String email);
    ResponseEntity<?> updatePassword(String userId, String email, String newPassword);
}
