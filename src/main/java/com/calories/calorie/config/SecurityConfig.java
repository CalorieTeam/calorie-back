package com.calories.calorie.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    // Spring 3.1 ~ (시큐리티 내부 구현 방법 - 필수적으로 람다형식으로 표현해야함)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                //인가설정
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/login","loginProc","/join","/joinProc").permitAll() // 해당 URL은 인증 없이 누구나 접근 가능
                        .requestMatchers("/admin").hasRole("ADMIN") // 해당 URL은 ADMIN 역할을 가진 사용자만 접근 가능
                        .requestMatchers("/my/**").hasAnyRole("ADMIN","USER") // 해당 경로는 ADMIN 또는 USER 역할을 가진 사용자만 접근 가능
                        .anyRequest().authenticated() // 위에서 명시되지 않은 모든 요청은 인증된 사용자만 접근 가능
                )
                //로그인 페이지 설정
                .formLogin((auth) -> auth.loginPage("/login") //로그인 페이지 설정
                        .loginProcessingUrl("/loginProc") //해당 url로 로그인 요청 처리 POST요청이여야 하며 , username,password 파라미터 추출
                        .permitAll())  //로그인 관련 요청은 누구나 접근 가능
                //csrf설정
                .csrf((auth) -> auth.disable()); //csrf 비활성 (임시)
        return http.build();
    }

    // BCrypt 암호화 사용을 위한 빈 등록
    // 단방향 해시(복호화를 할 수 없는 알고리즘)
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
