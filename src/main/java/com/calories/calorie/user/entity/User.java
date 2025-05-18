package com.calories.calorie.user.entity;

import com.calories.calorie.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //고유 아이디

    @Column(unique = true)
    private String email; //이메일

    private String pw; //비밀번호

    private String name; //이름

    @Column(length = 20)
    private String phoneNumber; //휴대폰번호

    @Column(length = 2)
    private String gender; //성별

    @Column(length = 30)
    private String nickName; //닉네임

    @Column(length = 20)
    private String role; //예 : ROLE_ADMIN, ROLE_USER

    private Integer height; //키

    private Integer weight; //몸무게

    @Column(length = 1)
    private int state; //상태

    @Lob
    @Column(name = "profile_image" , columnDefinition = "LONGBLOB")
    private byte[] profileImage;

    //유저 정보 업데이트

    public void updateProfileImage(byte[] imageData) {
        this.profileImage = imageData;
    }
    public void updateMyPage(String nickName,int height,int weight) {
        this.nickName = nickName;
        this.height = height;
        this.weight = weight;
    }

}
