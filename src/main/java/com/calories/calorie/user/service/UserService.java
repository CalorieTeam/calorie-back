package com.calories.calorie.user.service;

import com.calories.calorie.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    //유저 전체 조회
    List<UserResponseDto> getUsers();
}
