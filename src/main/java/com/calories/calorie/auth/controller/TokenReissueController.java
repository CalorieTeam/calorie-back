package com.calories.calorie.auth.controller;

import com.calories.calorie.auth.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenReissueController {
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("리프레시 토큰이 없습니다.");
        }

        String refreshToken = authHeader.substring(7); // "Bearer " 제거

        // 토큰 유효성 검사
        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        String redisKey = "RT:" + email;

        // Redis에 저장된 토큰과 비교
        String savedRefreshToken = redisTemplate.opsForValue().get(redisKey);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            return ResponseEntity.status(403).body("리프레시 토큰이 일치하지 않습니다.");
        }

        // Access Token 재발급
        String newAccessToken = jwtUtil.generateAccessToken(email);

        // 필요시 Refresh Token도 갱신
        String newRefreshToken = jwtUtil.generateRefreshToken(email);
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, Duration.ofDays(7));

        // 응답 반환
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", newAccessToken);
        tokenMap.put("refreshToken", newRefreshToken);

        return ResponseEntity.ok(tokenMap);
    }
}
