package com.calories.calorie.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestDto {
    //아이디
    private String username;
    //비밀번호
    private String password;
}
