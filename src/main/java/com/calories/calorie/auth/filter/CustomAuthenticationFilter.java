package com.calories.calorie.auth.filter;

import com.calories.calorie.auth.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 로그인 요청을 처리하는 커스텀 필터
 * - 기본 필터인 UsernamePasswordAuthenticationFilter를 상속
 * - POST /login 요청을 가로채서 JWT를 발급
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // 인증 처리의 핵심 객체: email, password로 인증을 실행
    private final AuthenticationManager authenticationManager;
    // JWT 생성 및 파싱을 위한 유틸리티 클래스
    private final JwtUtil jwtUtil;
    // Redis에 refresh token을 저장하기 위한 템플릿
    private final StringRedisTemplate redisTemplate;


    /**
     * 로그인 요청이 오면 실행되는 메서드
     * - JSON으로 들어온 email, password를 파싱
     * - 인증 토큰(UsernamePasswordAuthenticationToken)을 생성해 AuthenticationManager에 전달
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            // JSON 요청 본문에서 email, pw 꺼내기
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> loginData = objectMapper.readValue(request.getInputStream(), Map.class);

            String email = loginData.get("email");
            String pw = loginData.get("pw");

            // 인증용 토큰 객체 생성 → 실제 검증은 UserDetailsService에서 수행
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, pw);

            // AuthenticationManager에게 인증 요청
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 로그인 성공 시 실행되는 메서드
     * - AccessToken, RefreshToken 발급
     * - RefreshToken은 Redis에 저장
     * - 클라이언트에 두 토큰을 JSON 형태로 응답
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication)
            throws IOException, ServletException {

        String email = authentication.getName(); // 인증된 사용자 email

        // JWT 생성
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        // Redis에 RefreshToken 저장 (만료 시간 설정)
        redisTemplate.opsForValue().set("RT:" + email, refreshToken, Duration.ofDays(7));

        // 응답 반환
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 토큰 응답 구조
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        // JSON 직렬화 후 응답 전송
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(tokens));
    }

    /**
     * 로그인 실패 시 실행되는 메서드
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("인증 실패 authentication fail: " + failed.getMessage());
    }
}
