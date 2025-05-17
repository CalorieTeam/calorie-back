package com.calories.calorie.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @PreAuthorize("hasRole('ADMIN')") //ROLE_ prefix는 생략
    @GetMapping("/admin/only")
    public ResponseEntity<?> adminOnly() {
        return ResponseEntity.ok("관리자만 접근 가능한 API입니다.");
    }
}
