package com.calories.calorie.user.repository;

import com.calories.calorie.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //이메일 중복검사
    boolean existsByEmail(String email);

    //이메일 찾기
    Optional<User> findByEmail(String email);
}
