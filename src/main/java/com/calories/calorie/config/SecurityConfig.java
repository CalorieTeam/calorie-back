package com.calories.calorie.config;


import com.calories.calorie.auth.filter.CustomAuthenticationFilter;
import com.calories.calorie.auth.filter.JwtAuthenticationFilter;
import com.calories.calorie.auth.jwt.JwtUtil;
import com.calories.calorie.auth.userdetails.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //@PreAuthorize 사용 가능하게 해줌
public class SecurityConfig {


    /**
     * Spring Security의 핵심 필터 설정
     * - /login 요청 시 CustomAuthenticationFilter가 처리
     * - 그 외 요청은 인증된 사용자만 접근 허용
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http ,
                                           AuthenticationManager authManager,
                                           JwtUtil jwtUtil,
                                           StringRedisTemplate redisTemplate,
                                           CustomUserDetailsService customUserDetailsService) throws Exception {

        // 커스텀 로그인 필터 생성
        CustomAuthenticationFilter customFilter = new CustomAuthenticationFilter(authManager, jwtUtil, redisTemplate);
        // 기본 로그인 경로(/login) 설정
        customFilter.setFilterProcessesUrl("/login"); // 로그인 경로 설정

        // 요청마다 JWT 인증을 수행하는 필터
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, customUserDetailsService);

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf((auth) -> auth.disable()) // CSRF 비활성화 (JWT 기반 인증에 필요 없음)
                //인가설정
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/login","/auth/reissue","/swagger-ui/**","/v3/api-docs/**","/auth/logout").permitAll() // 해당 URL은 인증 없이 누구나 접근 가능
                        .anyRequest().authenticated() // 위에서 명시되지 않은 모든 요청은 인증된 사용자만 접근 가능
                )
                .addFilter(customFilter) // 커스텀 로그인 필터 등록
                .addFilterBefore(jwtAuthenticationFilter, CustomAuthenticationFilter.class); // 모든 요청 앞단에 JWT 필터 실행
                //csrf설정

        return http.build();
    }

    // BCrypt 암호화 사용을 위한 빈 등록
    // 단방향 해시(복호화를 할 수 없는 알고리즘)
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 수동 등록
     * - Spring Boot 3 이상에서는 자동 등록 안되므로 명시 필요
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 프론트 주소
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true); // credentials: 'include' 쓸 경우 true

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
