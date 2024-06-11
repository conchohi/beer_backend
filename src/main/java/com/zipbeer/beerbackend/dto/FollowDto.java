package com.zipbeer.beerbackend.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDto {
    private Long followNo; // 팔로우 번호
    private String userId; // 팔로우를 하는 사용자의 아이디
    private String followId; // 팔로우 대상 사용자의 아이디


}

