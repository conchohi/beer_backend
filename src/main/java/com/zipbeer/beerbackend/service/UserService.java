package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto getUserById(String id);
    Optional<UserEntity> getUserByNickname(String nickname);
    Optional<UserEntity> updateUserByNickname(String nickname, UserEntity updatedUser);
    public void modify(UserDto userDto);
    //    public void delete(String userId);
    boolean isIdAvailable(String userid);
    boolean isNicknameAvailable(String nickname);
    List<String> getUserIdsByEmail(String email);
    boolean emailExists(String email); // Add this method
}
