package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {
    void deleteByUserUserIdAndRoomRoomNo(String userId, Long roomId);
}
