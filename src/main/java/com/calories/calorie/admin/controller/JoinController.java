package com.calories.calorie.admin.controller;

import com.calories.calorie.admin.dto.JoinRequestDto;
import com.calories.calorie.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class JoinController {
    //의존성주입
    private final UserService userService;
    //회원가입 페이지 이동
    @GetMapping("/join")
    public String joinP() {
        return "join";
    }

    //회원가입 처리
    @PostMapping("/joinProc")
    public String joinProcess(JoinRequestDto joinRequestDto) {

        //테스트 아이디 출력
        System.out.println(joinRequestDto.getUsername());

        //회원가입 처리
        userService.joinProcess(joinRequestDto);
        //로그인 페이지로 리다이렉트
        return "redirect:/login";
    }
}
