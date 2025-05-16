package com.calories.calorie.auth.controller;

import com.calories.calorie.auth.dto.SignupRequestDto;
import com.calories.calorie.auth.jwt.JwtUtil;
import com.calories.calorie.auth.userdetails.CustomUserDetails;
import com.calories.calorie.user.entity.User;
import com.calories.calorie.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Auth API", description = "회원가입, 로그아웃 등의 인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름, 성별 등으로 신규 회원가입을 처리합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .pw(passwordEncoder.encode(request.getPw()))
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .state(1)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @Operation(summary = "로그아웃", description = "AccessToken을 헤더에 포함하여 로그아웃 요청 시, Redis에 저장된 RefreshToken을 제거합니다.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("AccessToken이 없습니다.");
        }

        String accessToken = authHeader.substring(7);

        if (!jwtUtil.validateToken(accessToken)) {
            return ResponseEntity.status(401).body("유효하지 않은 AccessToken입니다.");
        }

        String email = jwtUtil.getEmailFromToken(accessToken);
        redisTemplate.delete("RT:" + email);

        return ResponseEntity.ok("로그아웃되었습니다.");
    }

    @Operation(summary = "로그인 사용자 정보 조회", description = "AccessToken을 통해 현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<?> getLoginUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return ResponseEntity.status(401).body("인증 정보가 없습니다.");
        }

        // 필요한 정보만 선택해서 응답 (예: 이메일, 이름 등)
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", customUserDetails.getUsername());
        userInfo.put("name", customUserDetails.getUser().getName());
        userInfo.put("phoneNumber", customUserDetails.getUser().getPhoneNumber());

        return ResponseEntity.ok(userInfo);
    }
}