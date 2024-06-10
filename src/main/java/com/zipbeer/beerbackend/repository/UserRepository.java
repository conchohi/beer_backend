package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByUserId(String userId);

    UserEntity findByUserId(String userId);

    UserEntity findByEmail(String email);

    Optional<UserEntity> findByNickname(String nickname);

}
