package com.zipbeer.beerbackend.service.impl;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.oauth.CustomOAuth2User;
import com.zipbeer.beerbackend.dto.oauth.GoogleResponse;
import com.zipbeer.beerbackend.dto.oauth.KakaoResponse;
import com.zipbeer.beerbackend.dto.oauth.NaverResponse;
import com.zipbeer.beerbackend.dto.oauth.OAuthResponse;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2UserRequest를 통해 OAuth2User 객체를 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 클라이언트 등록 ID
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthResponse oAuthResponse = null;

        // 클라이언트 등록 ID에 따라 OAuthResponse 객체를 생성
        if (registrationId.equals("naver")) {
            oAuthResponse = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuthResponse = new KakaoResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuthResponse = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        // 사용자 이름을 생성
        String username = oAuthResponse.getProvider() + " " + oAuthResponse.getProviderId();

        // 사용자가 이미 존재하는지 확인
        UserEntity existingUser = userRepository.findByUserId(username);
        if (existingUser != null) {
            UserDto userDto = new UserDto(existingUser);
            return new CustomOAuth2User(userDto);
        }

        // 고유한 닉네임을 생성
        String nickname = generateUniqueNickname(oAuthResponse.getProvider());

        // 새 사용자 엔티티를 생성
        UserEntity user = UserEntity.builder()
                .userId(username)
                .email(oAuthResponse.getEmail())
                .sns(oAuthResponse.getProvider())
                .nickname(nickname)
                .role("USER")
                .followers(new ArrayList<>())  // 컬렉션을 초기화
                .followings(new ArrayList<>()) // 컬렉션을 초기화
                .build();

        // 사용자 정보 DB에 저장
        userRepository.save(user);

        // UserDto 객체를 생성
        UserDto userDto = UserDto.builder()
                .userId(username)
                .nickname(user.getNickname())
                .role("USER")
                .build();

        // CustomOAuth2User 객체를 반환
        return new CustomOAuth2User(userDto);
    }

    // 고유한 닉네임을 생성
    private String generateUniqueNickname(String provider) {
        String nickname;
        Optional<UserEntity> existingUser;
        do {
            // 닉네임을 생성
            nickname = provider + RandomStringUtils.randomNumeric(6);
            // 닉네임이 이미 존재하는지 확인
            existingUser = userRepository.findByNickname(nickname);
        } while (existingUser.isPresent()); // 중복 닉네임이 안나올때까지 반복
        return nickname;
    }
}
