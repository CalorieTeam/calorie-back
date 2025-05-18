package com.calories.calorie.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class JoinRequestDto {
    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;
    @Schema(description = "비밀번호", example = "1234")
    private String pw;
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;
    @Schema(description = "닉네임", example = "도라에몽")
    private String nickName; // 닉네임
    @Schema(description = "휴대폰 번호", example = "01012345678")
    private String phoneNumber;
    @Schema(description = "성별 (M 또는 F)", example = "M")
    private String gender; // "M", "F" 등
}
