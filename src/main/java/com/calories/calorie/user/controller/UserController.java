package com.calories.calorie.user.controller;


import com.calories.calorie.user.dto.UserResponseDto;
import com.calories.calorie.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "유저 관련 API 목록입니다.")
public class UserController {
    private final UserService userService;

    @Operation(summary = "테스트 API", description = "단순 테스트용 API입니다.")
    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.ok("테스트 완료");
    }

    @Operation(summary = "유저 전체 조회", description = "모든 유저 데이터를 조회합니다.")
    @GetMapping("/db")
    public ResponseEntity<List<UserResponseDto>> getDb() {
        List<UserResponseDto> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }
}
