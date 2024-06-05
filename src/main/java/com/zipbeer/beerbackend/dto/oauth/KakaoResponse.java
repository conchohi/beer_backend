package com.zipbeer.beerbackend.dto.oauth;

import java.util.Map;

public class KakaoResponse implements OAuthResponse{
    private final Map<String ,Object> attribute;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.kakaoAccount = (Map<String,Object>)attribute.get("kakao_account");
        this.profile = (Map<String, Object>)kakaoAccount.get("profile");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getNickname() {
        Object nickname = profile.get("nickname");
        return nickname != null ? nickname.toString() : null;
    }
}
