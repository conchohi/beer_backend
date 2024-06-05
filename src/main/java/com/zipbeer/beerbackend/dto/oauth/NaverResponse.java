package com.zipbeer.beerbackend.dto.oauth;

import java.util.Map;

public class NaverResponse implements OAuthResponse{
    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getNickname() {
        Object nickname = attribute.get("nickname");
        return nickname != null ? nickname.toString() : null;
    }
}
