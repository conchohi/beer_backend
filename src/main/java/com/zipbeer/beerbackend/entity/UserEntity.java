package com.zipbeer.beerbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    private LocalDate createDate;

    //양방향
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL
            , orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<FollowEntity> followList;

    @OneToOne(mappedBy = "user")
    private ChatEntity room;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDate.now();
    }
}
