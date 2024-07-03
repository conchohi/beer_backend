package com.zipbeer.beerbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "participantList")
@Entity
@Table(name="room_tbl")
public class RoomEntity {
    @Id
    @Column
    private Long roomNo;

    private String title;

    @Column(name = "room_pw")
    private String roomPw;

    @CreationTimestamp
    private LocalDateTime createDate;

    private String category;

    private int maximumUser;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL
            , orphanRemoval = true,
            fetch = FetchType.LAZY)
    @OrderBy("partNo asc")
    private List<ParticipantEntity> participantList;

    private int participantCount;

    //방장의 닉네임
    private String master;

    //참여중인 사용자의 리스트 얻는 법
    public List<UserEntity> getUsers() {
        return participantList.stream()
                .map(ParticipantEntity::getUser)
                .collect(Collectors.toList());
    }
}
