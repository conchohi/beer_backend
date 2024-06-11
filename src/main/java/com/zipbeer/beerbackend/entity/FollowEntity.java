package com.zipbeer.beerbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "follow"})
@Entity
@Table(name="follow_tbl")
public class FollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followNo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "userId")
    private UserEntity user;

    //유저가 팔로우한 사람들 목록
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "follow_id",referencedColumnName = "userId")
    private UserEntity follow;

}
