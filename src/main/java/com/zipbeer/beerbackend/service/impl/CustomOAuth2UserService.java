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
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthResponse oAuthResponse = null;

        if (registrationId.equals("naver")) {
            oAuthResponse = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuthResponse = new KakaoResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuthResponse = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String username = oAuthResponse.getProvider() + " " + oAuthResponse.getProviderId();

        // Check if user already exists
        UserEntity existingUser = userRepository.findByUserId(username);
        if (existingUser != null) {
            UserDto userDto = new UserDto(existingUser);
            return new CustomOAuth2User(userDto);
        }

        String nickname = generateUniqueNickname(oAuthResponse.getProvider());

        UserEntity user = UserEntity.builder()
                .userId(username)
                .email(oAuthResponse.getEmail())
                .sns(oAuthResponse.getProvider())
                .nickname(nickname)
                .role("USER")
                .followers(new ArrayList<>())  // Initialize the collections
                .followings(new ArrayList<>()) // Initialize the collections
                .build();

        userRepository.save(user);

        UserDto userDto = UserDto.builder()
                .userId(username)
                .nickname(user.getNickname())
                .role("USER")
                .build();

        return new CustomOAuth2User(userDto);
    }

    private String generateUniqueNickname(String provider) {
        String nickname;
        Optional<UserEntity> existingUser;
        do {
            nickname = provider + RandomStringUtils.randomNumeric(6);
            existingUser = userRepository.findByNickname(nickname);
        } while (existingUser.isPresent());
        return nickname;
    }
}
