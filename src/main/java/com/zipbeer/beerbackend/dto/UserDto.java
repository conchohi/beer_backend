package com.zipbeer.beerbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zipbeer.beerbackend.entity.UserEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Builder.Default
    private String intro = "안녕하세요";
    private int age;
    private String gender;

    @Builder.Default
    private List<UserDto> followerList = new ArrayList<>();
    private int followerCount;

    @Builder.Default
    private List<UserDto> followingList = new ArrayList<>();
    private int followingCount;

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
        this.intro = user.getIntro();
        this.isDelete = null; // 초기화 필요시 설정
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(userId, userDto.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
