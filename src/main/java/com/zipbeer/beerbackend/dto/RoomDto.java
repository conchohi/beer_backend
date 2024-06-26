package com.zipbeer.beerbackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zipbeer.beerbackend.entity.RoomEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private int currentUser;
    private int maximumUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;
    private List<UserDto> participantList;
    private String master;

    public RoomDto(RoomEntity roomEntity) {
        this.roomNo = roomEntity.getRoomNo();
        this.title = roomEntity.getTitle();
        this.roomPw = roomEntity.getRoomPw();
        this.category = roomEntity.getCategory();
        this.currentUser = roomEntity.getParticipantCount();
        this.maximumUser = roomEntity.getMaximumUser();
        this.createDate = roomEntity.getCreateDate();
        this.participantList = roomEntity.getParticipantList().stream()
                .map(participant -> new UserDto(participant.getUser()))
                .collect(Collectors.toList());
        this.master = roomEntity.getMaster();
    }
}
