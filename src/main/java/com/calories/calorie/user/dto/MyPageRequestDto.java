package com.calories.calorie.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "내정보 수정 요청 형식")
public class MyPageRequestDto {
    @Schema(description = "사용자 닉네임", example = "도라에몽")
    private String nickName;
    @Schema(description = "사용자 키", example = "180")
    private int height;
    @Schema(description = "사용자 몸무게", example = "50")
    private int weight;
}
