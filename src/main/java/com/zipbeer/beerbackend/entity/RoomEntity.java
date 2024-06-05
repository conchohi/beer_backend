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

    private Integer maximumUser;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL
            , orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<ChatEntity> participantList;
}
