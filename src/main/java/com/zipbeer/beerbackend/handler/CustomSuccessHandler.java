package com.zipbeer.beerbackend.handler;

import com.zipbeer.beerbackend.dto.oauth.CustomOAuth2User;
import com.zipbeer.beerbackend.provider.JWTProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTProvider jwtProvider;

    @Value("${server_ip}")
    private String serverip;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String username = customOAuth2User.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String nickname = customOAuth2User.getName();

        String token = jwtProvider.createJwt("refresh", username, role, nickname, 24*60*60*1000L);

        //쿠키로 전달한 것 프론트에서 "Bearer token"으로 처리해서 요청 헤더에 저장해서 넘겨야 할듯
        response.addCookie(createCookie("refresh", token));
        response.sendRedirect(serverip + "/getAccess");

    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setSecure(true);
        //보일 위치 - 전역
        cookie.setPath("/");
        //HttpOnly 를 해두면 프론트에서 js로 쿠키를 사용할 수 없음
        cookie.setHttpOnly(true);

        return cookie;
    }
}
