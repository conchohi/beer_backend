package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByUserId(String userId);

    UserEntity findByUserId(String userId);

    UserEntity findByEmail(String email);

}
