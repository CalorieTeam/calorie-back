package com.calories.calorie.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "유저 응답 형식")
public class UserResponseDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long id; //고유 아이디

    @Schema(description = "이메일", example = "test@example.com")
    private String email; //이메일

    @Schema(description = "비밀번호", example = "password123")
    private String pw; //비밀번호

    @Schema(description = "이름", example = "홍길동")
    private String name; //이름

    @Schema(description = "휴대폰 번호", example = "01012345678")
    private String phoneNumber; //휴대폰번호

    @Schema(description = "성별", example = "M")
    private String gender; //성별
}