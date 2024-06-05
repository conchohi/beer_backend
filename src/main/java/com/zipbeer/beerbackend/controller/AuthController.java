package com.zipbeer.beerbackend.controller;

import com.beer_back.dto.request.auth.EmailCertificationRequestDto;
import com.beer_back.dto.request.auth.IdCheckRequestDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.request.auth.CheckCertificationRequestDto;
import com.zipbeer.beerbackend.dto.response.ResponseDto;
import com.zipbeer.beerbackend.dto.response.auth.CheckCertificationResponseDto;
import com.zipbeer.beerbackend.dto.response.auth.EmailCertificationResponseDto;
import com.zipbeer.beerbackend.dto.response.auth.IdCheckResponseDto;
import com.zipbeer.beerbackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/id-check")
    public ResponseEntity<? super IdCheckResponseDto> idCheck(
        @RequestBody @Valid IdCheckRequestDto requestBody){
            ResponseEntity<? super IdCheckResponseDto>  response = authService.idCheck(requestBody);
            return response;
    }
    
    //join의 경우 json 형식으로 username, password, nickname, email 받을 수 있음
    @PostMapping("/join")
    public ResponseEntity<? super ResponseDto> join(@ModelAttribute UserDto userDto) {
        return authService.join(userDto);
    }

    @PostMapping("/email-certification")
    public ResponseEntity<? super EmailCertificationResponseDto> emailCertificaton
            (@RequestBody @Valid EmailCertificationRequestDto requestBody)
    {ResponseEntity<? super EmailCertificationResponseDto> response = authService.emailCertification(requestBody);
        return response;
    }

    @PostMapping("/check-certification")
    public ResponseEntity<? super CheckCertificationResponseDto> checkCertification
            (@RequestBody @Valid CheckCertificationRequestDto requestBody){
        ResponseEntity<? super CheckCertificationResponseDto> response = authService.checkCertification(requestBody);
        return response;
    }

    //아이디 찾기
    @PostMapping("/findId-email-certification")
    public ResponseEntity<?> sendCertificationEmail(@RequestBody EmailCertificationRequestDto request) {
        return authService.findIdByEmail(request.getEmail());
    }
}

    

