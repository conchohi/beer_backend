package com.zipbeer.beerbackend.service.impl;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.oauth.CustomOAuth2User;
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

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthResponse oAuthResponse = null;

        if(registrationId.equals("naver")){
            oAuthResponse = new NaverResponse(oAuth2User.getAttributes());
        }else if(registrationId.equals("kakao")){
            oAuthResponse = new KakaoResponse(oAuth2User.getAttributes());
        }else {
            return null;
        }

        String username = oAuthResponse.getProvider() + " " + oAuthResponse.getProviderId();
        UserEntity existData = userRepository.findByUserId(username);
        if(existData == null){
            //닉네임을 동의하지 않을 경우 랜덤으로 닉네임 등록
            String nickname = oAuthResponse.getNickname() != null ?
                    oAuthResponse.getNickname() : RandomStringUtils.random(10, true, false);

            UserEntity user = UserEntity.builder()
                    .userId(username)
                    .email(oAuthResponse.getEmail())
                    .sns(oAuthResponse.getProvider())
                    .nickname(nickname)
                    .role("USER")
                    .build();

            userRepository.save(user);

            UserDto userDto = UserDto.builder()
                    .userId(username)
                    .nickname(user.getNickname())
                    .role("USER")
                    .build();

            return new CustomOAuth2User(userDto);
        } else{
            existData.setEmail(oAuthResponse.getEmail());

            UserDto userDto = UserDto.builder()
                    .userId(existData.getUserId())
                    .nickname(existData.getNickname())
                    .role(existData.getRole())
                    .build();

            return new CustomOAuth2User(userDto);
        }

    }
}
