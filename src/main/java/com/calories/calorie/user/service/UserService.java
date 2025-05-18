package com.calories.calorie.user.service;

import com.calories.calorie.user.dto.MyPageRequestDto;
import com.calories.calorie.user.dto.MyPageResponseDto;
import com.calories.calorie.user.dto.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    //유저 전체 조회
    List<UserResponseDto> getUsers();
    
    //마이페이지 조회
    MyPageResponseDto getMyPage(String email);

    //유저 정보 수정
    void updateMyPage(String email, MyPageRequestDto dto);

    //프로필 이미지 업데이트
    void updateProfileImage(String email, MultipartFile file);

    //프로필 이미지 가져오기
    byte[] getProfileImage(String email);
}
