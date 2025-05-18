package com.calories.calorie.auth.filter;

import com.calories.calorie.auth.jwt.JwtUtil;
import com.calories.calorie.auth.userdetails.CustomUserDetails;
import com.calories.calorie.auth.userdetails.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT 유틸 클래스: 토큰 생성, 검증, 파싱 담당
    private final JwtUtil jwtUtil;
    //사용자 정보 로드 서비스 : email을 기반으로 UserDetails 조회
    private final CustomUserDetailsService customUserDetailsService;


    /**
     * 모든 요청마다 한 번 실행됨 (OncePerRequestFilter)
     * - AccessToken이 Authorization 헤더에 존재하는 경우
     * - 해당 토큰을 검증하고 인증 정보를 SecurityContextHolder에 저장
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 꺼냄
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // "Bearer " 이후 토큰 추출

            //유효성검사
            JwtUtil.TokenValidationResult result = jwtUtil.validateTokenDetail(token);

            // 2.유효한 토큰인지 검사
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token); // subject == email

                // 3. DB에서 사용자 조회 (토큰만으로는 인증 불완전하므로 재확인)
                CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

                // 4. SecurityContext에 인증 정보 저장
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                if (result == JwtUtil.TokenValidationResult.EXPIRED) {
                    response.getWriter().write("{\"message\": \"AccessTokenExpired\"}");
                } else {
                    response.getWriter().write("{\"message\": \"InvalidAccessToken\"}");
                }
                return;
            }
        }

        // 5. 다음 필터로 계속 진행
        filterChain.doFilter(request, response);
    }
}
