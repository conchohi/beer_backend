package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.ChatEntity;
import com.zipbeer.beerbackend.entity.RoomEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    void deleteByUserUserIdAndRoomRoomNo(String userId, Long roomId);
}
