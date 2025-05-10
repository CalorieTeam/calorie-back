package com.calories.calorie.user.service;

import com.calories.calorie.user.dto.UserResponseDto;
import com.calories.calorie.user.entity.User;
import com.calories.calorie.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

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
}
