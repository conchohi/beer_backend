package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.FriendEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<FriendEntity, Long> {
    List<FriendEntity> findByUserAndFriend(UserEntity user, UserEntity friend);
    List<FriendEntity> findByUserAndAccepted(UserEntity user, boolean accepted);
    List<FriendEntity> findByFriendAndAccepted(UserEntity friend, boolean accepted);
    boolean existsByUserAndFriendAndAccepted(UserEntity user, UserEntity friend, boolean accepted);
    boolean existsByUserAndFriend(UserEntity user, UserEntity friend);
}
