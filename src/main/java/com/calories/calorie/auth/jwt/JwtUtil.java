package com.calories.calorie.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    //시크릿 키: JWT 서명에 사용될 비밀키 (32자 이상, HMAC-SHA256 암호화에서 최소 길이 요구됨)
    @Value("${jwt.secret}")
    private String secret ; // 최소 256비트 (32자 이상)


    private Key key;

    //토큰 유효시간 설정
    //엑세스토큰 유효시간
    private final long accessTokenValidity = 1000L * 60 * 1; // 15분
    //리프레시 토큰 유효시간
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7; // 7일

    //의존성 주입 완료 후 자동 호출됨
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes()); // HMAC-SHA 방식 키 생성
    }

    /**
     * AccessToken 생성 메서드
     * @param email 토큰 주제(subject)에 들어갈 사용자 이메일
     * @return JWT AccessToken 문자열
     */
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)         // 토큰 주제(일반적으로 사용자 ID 또는 이메일)
                .setIssuedAt(new Date())   // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)  // 서명 알고리즘과 키 설정
                .compact();                               // 문자열 형태로 직렬화
    }

    /**
     * RefreshToken 생성 메서드
     * @param email 사용자 이메일
     * @return JWT RefreshToken 문자열
     */
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "refresh") // Access와 구분되는 클레임 추가
                .claim("token_id", UUID.randomUUID().toString()) // 고유 ID
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검사
     * @param token 검사 대상 JWT 문자열
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            // 파서에 키를 설정한 후 토큰을 파싱하여 유효성 검사 (예외 발생 시 false)
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 서명 오류, 만료, 포맷 오류 등 다양한 JWT 예외 처리
            return false;
        }
    }

    /**
     * 토큰에서 이메일(= subject) 추출
     * @param token JWT 문자열
     * @return 이메일 문자열
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token) // 토큰 파싱 후 payload(Claims) 추출
                .getBody();

        return claims.getSubject(); // subject(email)를 꺼냄
    }
    public enum TokenValidationResult {
        VALID,
        EXPIRED,
        INVALID
    }

    public TokenValidationResult validateTokenDetail(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return TokenValidationResult.VALID;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return TokenValidationResult.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            return TokenValidationResult.INVALID;
        }
    }
}
