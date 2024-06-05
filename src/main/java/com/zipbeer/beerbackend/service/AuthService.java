package com.zipbeer.beerbackend.service;

import com.beer_back.dto.request.auth.EmailCertificationRequestDto;
import com.beer_back.dto.request.auth.IdCheckRequestDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.request.auth.CheckCertificationRequestDto;
import com.zipbeer.beerbackend.dto.response.ResponseDto;
import com.zipbeer.beerbackend.dto.response.auth.CheckCertificationResponseDto;
import com.zipbeer.beerbackend.dto.response.auth.EmailCertificationResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    // 아이디 중복 체크
    ResponseEntity<? super ResponseDto> idCheck(IdCheckRequestDto dto);
    // 회원가입
    ResponseEntity<? super ResponseDto> join(UserDto dto);
    // 이메일 인증번호 전송
    ResponseEntity<? super EmailCertificationResponseDto> emailCertification(EmailCertificationRequestDto dto);
    // 이메일 인증번호 확인
    ResponseEntity<? super CheckCertificationResponseDto> checkCertification(CheckCertificationRequestDto request);
    // 아이디 찾기
    ResponseEntity<? super ResponseDto> findIdByEmail(String email);
}
