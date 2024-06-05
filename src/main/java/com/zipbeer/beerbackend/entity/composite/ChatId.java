package com.zipbeer.beerbackend.entity.composite;

import com.zipbeer.beerbackend.entity.RoomEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Embeddable
public class ChatId implements Serializable {
    private String userId;
    private Long roomNo;
}
