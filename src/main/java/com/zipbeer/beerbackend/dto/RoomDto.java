package com.zipbeer.beerbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {
    private Long roomNo;
    private String title;
    private String roomPw;
    private String category;
    //참가자 수
    private int currentUser;
    //최대 인원 수
    private int maximumUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private List<UserDto> participantList;

    //방장의 닉네임
    private String master;
}
