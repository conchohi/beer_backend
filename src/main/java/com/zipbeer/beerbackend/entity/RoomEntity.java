package com.zipbeer.beerbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="room_tbl")
public class RoomEntity {
    @Id
    @Column
    private Long roomNo;

    private String title;

    @Column(name = "room_pw")
    private String roomPw;

    @CreatedDate
    private LocalDateTime createDate;

    private String category;

    private int maximumUser;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL
            , orphanRemoval = true,
            fetch = FetchType.LAZY)
    @OrderBy("chatNo asc")
    private List<ChatEntity> participantList;

    private int currentUserCount;

    //방장의 닉네임
    private String master;

    @PostLoad
    @PostPersist
    @PostUpdate
    private void getParticipantCountAndMaster() {
        if (participantList != null) {
            this.currentUserCount = participantList.size();
            //List의 가장 첫번쨰 사람이 방장 => 입장한 지 오래된 순
            this.master = participantList.get(0).getUser().getNickname();
        } else {
            this.currentUserCount = 0;
            this.master = null;
        }
    }

}
