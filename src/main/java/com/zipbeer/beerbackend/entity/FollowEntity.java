package com.zipbeer.beerbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="follow_tbl")
public class FollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followNo;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "userId")
    private UserEntity user;

    //유저가 팔로우한 사람들 목록
    @ManyToOne
    @JoinColumn(name = "follow_id",referencedColumnName = "userId")
    private UserEntity follow;

}
