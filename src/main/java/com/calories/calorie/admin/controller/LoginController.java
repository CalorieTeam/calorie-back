package com.calories.calorie.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String loginP() {
        return "login"; //login.mustache 반환
    }
}
