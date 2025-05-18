package com.calories.calorie.user.service;

import com.calories.calorie.user.dto.MyPageRequestDto;
import com.calories.calorie.user.dto.MyPageResponseDto;
import com.calories.calorie.user.dto.UserResponseDto;
import com.calories.calorie.user.entity.User;
import com.calories.calorie.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; //암호화 BCrypt 해싱 적용을 위한 DI

    //유저 전체 조회
    @Override
    public List<UserResponseDto> getUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> UserResponseDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .pw(user.getPw())
                        .name(user.getName())
                        .phoneNumber(user.getPhoneNumber())
                        .gender(user.getGender())
                        .build()).toList();
    }

    //마이페이지 조회
    @Override
    public MyPageResponseDto getMyPage(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("해당 회원을 찾을 수 없습니다."));

        return MyPageResponseDto.builder()
                .nickName(user.getNickName())
                .createdDt(convertToDate(user.getCreatedDt()))
                .height(user.getHeight())
                .weight(user.getWeight())
                .build();

    }

    @Override
    public void updateMyPage(String email, MyPageRequestDto dto) {
        //유저 정보 조회
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        //유저 정보 업데이트
        user.updateMyPage(dto.getNickName(), dto.getHeight(),dto.getWeight());
        // 저장
        userRepository.save(user);
    }

    @Override
    public void updateProfileImage(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        try {
            byte[] imageData = file.getBytes();
            user.updateProfileImage(imageData);
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }

    @Override
    public byte[] getProfileImage(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

        return user.getProfileImage();
    }

    // LocalDateTime -> LocalDate 타입 변환 메소드
    private Date convertToDate(LocalDateTime localDateTime) {
        return java.sql.Timestamp.valueOf(localDateTime);
    }
}
