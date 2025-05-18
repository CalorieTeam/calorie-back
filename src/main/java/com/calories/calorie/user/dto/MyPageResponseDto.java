package com.calories.calorie.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "마이페이지 응답 형식")
public class MyPageResponseDto {
    @Schema(description = "사용자 닉네임", example = "도라에몽")
    private String nickName;

    @Schema(description = "가입일", example = "YYYY-MM-DD")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdDt;

    @Schema(description = "사용자 키", example = "180")
    private int height;

    @Schema(description = "사용자 몸무게", example = "50")
    private int weight;
}
