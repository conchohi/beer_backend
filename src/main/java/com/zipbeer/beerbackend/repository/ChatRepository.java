package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.ChatEntity;
import com.zipbeer.beerbackend.entity.RoomEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    void deleteByUserAndRoom(UserEntity user, RoomEntity room);
}
