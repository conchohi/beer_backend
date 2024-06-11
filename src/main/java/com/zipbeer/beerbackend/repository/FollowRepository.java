package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<FollowEntity, Long> {
    Optional<FollowEntity> findByUserUserIdAndFollowUserId(String userId, String followId);
    List<FollowEntity> findByFollowUserId(String follow_userId);

    List<FollowEntity> findByUser_UserId(String userId);



}
