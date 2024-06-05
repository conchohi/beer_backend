package com.zipbeer.beerbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="user")
@Table(name="user_tbl")
@ToString
public class UserEntity {
    @Id
    private String userId;
    private String password;
    private String email;
    private String role;
    private String nickname;
    private String profileImage;
    private String sns;
    private String mbti;
    private int age;
    private String gender;

}
