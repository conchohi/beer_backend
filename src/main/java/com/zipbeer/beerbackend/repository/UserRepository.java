package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByUserId(String userId);
    UserEntity findByUserId(String userId);
    UserEntity findByEmail(String email);

    Optional<UserEntity> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email); // Add this method

    @Query("SELECT u.userId FROM UserEntity u WHERE u.email = :email")
    List<String> findUserIdsByEmail(String email);

    Optional<UserEntity> findByUserIdAndEmail(String userId, String email);

    @Query("SELECT u FROM UserEntity u WHERE lower(u.nickname) LIKE lower(concat('%', :nickname, '%'))")
    List<UserEntity> findByNicknameContainingIgnoreCase(String nickname);
}
