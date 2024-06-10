package com.zipbeer.beerbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RoomMessage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomId;
    private String senderName;
    private String receiverName;
    private String message;
    private String date;

    @Enumerated(EnumType.STRING)
    private Status status;
}
