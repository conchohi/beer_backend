package com.zipbeer.beerbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"room","followList"})
@Entity(name="user")
@Table(name="user_tbl")
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

    @CreationTimestamp
    private LocalDate createDate;

    //양방향
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FollowEntity> followers;

    @OneToMany(mappedBy = "follow", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FollowEntity> followings;

    //chat 삭제 시 해당 속성이 null로 변경
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private ParticipantEntity room;

}
