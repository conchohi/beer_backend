package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.EmailCertificationDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/id-check")
    public ResponseEntity<?> idCheck(@RequestBody String id){
        return ResponseEntity.ok(Map.of("message", "Success."));
    }
    
    //join의 경우 json 형식으로 username, password, nickname, email 받을 수 있음
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserDto userDto) {
        return authService.join(userDto);
    }

    @PostMapping("/email-certification")
    public ResponseEntity<?> emailCertificaton
            (@RequestBody @Valid EmailCertificationDto requestBody)
    {ResponseEntity<?> response = authService.emailCertification(requestBody);
        return response;
    }

    @PostMapping("/check-certification")
    public ResponseEntity<?> checkCertification
            (@RequestBody @Valid EmailCertificationDto requestBody){
        ResponseEntity<?> response = authService.checkCertification(requestBody);
        return response;
    }

    //아이디 찾기
    @PostMapping("/findId-email-certification")
    public ResponseEntity<?> sendCertificationEmail(@RequestBody String email) {
        return authService.findIdByEmail(email);
    }




}

    

