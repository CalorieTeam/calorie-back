package com.calories.calorie.user.controller;


import com.calories.calorie.auth.userdetails.CustomUserDetails;
import com.calories.calorie.user.dto.MyPageResponseDto;
import com.calories.calorie.user.dto.MyPageRequestDto;
import com.calories.calorie.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
@Tag(name = "마이페이지", description = "마이페이지 API 목록입니다.")
public class MypageController {
    private final UserService userService;

    @Operation(summary = "마이페이지 조회", description = "마이페이지 조회합니다.")
    @GetMapping
    public ResponseEntity<MyPageResponseDto> getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyPage(userDetails.getUsername()));
    }

    @Operation(summary = "내정보 수정", description = "마이페이지 내정보를 수정합니다.")
    @PostMapping
    public ResponseEntity<?> editMyPage(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MyPageRequestDto dto) {
        String email = userDetails.getUsername();
        userService.updateMyPage(email,dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 이미지 수정", description = "프로필 이미지를 수정합니다.")
    @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("file") MultipartFile file) { //

        userService.updateProfileImage(userDetails.getUsername(), file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 이미지 조회", description = "프로필 이미지 조회")
    @GetMapping("/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        byte[] imageData = userService.getProfileImage(userDetails.getUsername());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 또는 조건에 따라 PNG로
                .body(imageData);
    }
}
