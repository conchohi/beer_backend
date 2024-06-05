package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.EmailCertificationDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.response.ResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    // 아이디 중복 체크
    boolean idCheck(String id);
    // 회원가입
    ResponseEntity<?> join(UserDto dto);
    // 이메일 인증번호 전송
    ResponseEntity<?> emailCertification(EmailCertificationDto dto);
    // 이메일 인증번호 확인
    ResponseEntity<?> checkCertification(EmailCertificationDto request);
    // 아이디 찾기
    ResponseEntity<? super ResponseDto> findIdByEmail(String email);
}
