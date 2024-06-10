package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.entity.UserEntity;

import java.util.Optional;


public interface UserService {

    Optional<UserEntity> getUserByNickname(String nickname);
    Optional<UserEntity> updateUserByNickname(String nickname, UserEntity updatedUser);
    public void modify(UserDto userDto);
//    public void delete(String userId);
}
