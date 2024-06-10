package com.zipbeer.beerbackend.entity;

import com.zipbeer.beerbackend.entity.composite.ChatId;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="chat_tbl")
public class ChatEntity {
    @EmbeddedId
    private ChatId chatId;

    @Column
    private int ord;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomNo")
    @JoinColumn(name = "room_no")
    private RoomEntity room;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
