package com.zipbeer.beerbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zipbeer.beerbackend.entity.UserEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @JsonProperty("id")
    private String userId;
    private String password;
    private String nickname;
    private String email;
    private String profileImage;
    private String sns;
    private String role;
    private String mbti;
    private int age;
    private String gender;

    @JsonIgnore
    private MultipartFile profileFile;

    private String isDelete;

    public UserDto(UserEntity user) {
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
        this.email = user.getEmail();
        this.sns = user.getSns();
        this.role = user.getRole();
        this.mbti = user.getMbti();
        this.age = user.getAge();
        this.gender = user.getGender();
        this.isDelete = null; // 초기화 필요시 설정
    }
}
