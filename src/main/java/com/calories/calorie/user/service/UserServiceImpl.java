package com.calories.calorie.user.service;

import com.calories.calorie.admin.dto.JoinRequestDto;
import com.calories.calorie.user.dto.UserResponseDto;
import com.calories.calorie.user.entity.User;
import com.calories.calorie.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    //회원가입
    @Override
    public void joinProcess(JoinRequestDto joinRequestDto) {
        User user = User.builder()
                .email(joinRequestDto.getUsername())
                .pw(bCryptPasswordEncoder.encode(joinRequestDto.getPassword()))
                .phoneNumber("01012341234")
                .state(1)
                .name("곽루카")
                .gender("M")
                .build();
        userRepository.save(user);
        boolean matches = bCryptPasswordEncoder.matches("1234", "$2a$10$/p.GdhgWISRWg0yUSFDGceYdhXfXEkBUAtO8cBdpVbPu6RHtC/Vg2");
        log.info("매치 결과: {}", matches);
    }
}
