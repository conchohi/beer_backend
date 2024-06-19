package com.zipbeer.beerbackend.dto.oauth;

import java.util.Map;

public class GoogleResponse implements OAuthResponse {
    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getNickname() {
        Object nickname = attribute.get("name");
        return nickname != null ? nickname.toString() : null;
    }
}
