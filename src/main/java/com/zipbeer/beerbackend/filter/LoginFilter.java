    package com.zipbeer.beerbackend.filter;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.zipbeer.beerbackend.dto.jwt.CustomUserDetails;
    import com.zipbeer.beerbackend.provider.JWTProvider;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.AuthenticationException;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import java.io.IOException;
    import java.io.PrintWriter;
    import java.util.Collection;
    import java.util.HashMap;
    import java.util.Iterator;
    import java.util.Map;

    public class LoginFilter extends UsernamePasswordAuthenticationFilter {
        private final AuthenticationManager authenticationManager;
        private final JWTProvider jwtProvider;

        public LoginFilter(AuthenticationManager authenticationManager, JWTProvider jwtProvider) {
            this.authenticationManager = authenticationManager;
            this.jwtProvider = jwtProvider;
            setFilterProcessesUrl("/api/login");
        }

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
            String username = obtainUsername(request);
            String password = obtainPassword(request);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,password);

            return authenticationManager.authenticate(token);
        }
        //요청에서 받을 때 설정할 파라미터 이름
        @Override
        protected String obtainPassword(HttpServletRequest request) {
            return request.getParameter("password");
        }

        @Override
        protected String obtainUsername(HttpServletRequest request) {
            return request.getParameter("username");
        }
        //로그인 성공 시 수행할 작업
        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            String userId = authentication.getName();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority auth = iterator.next();
            String role = auth.getAuthority();
            String nickname = customUserDetails.getNickname();

            String access = jwtProvider.createJwt("access", userId, role, nickname, 10*60*1000L);
            String refresh = jwtProvider.createJwt("refresh", userId, role, nickname, 24*60*60*1000L);

            response.addCookie(createCookie("refresh", refresh));
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.setHeader("access", access);

            // 닉네임을 바디에 포함
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("nickname", nickname);

            PrintWriter out = response.getWriter();
            out.print(new ObjectMapper().writeValueAsString(responseBody));
            out.flush();

        }


        //로그인 실패 시
        @Override
        protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
