package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(String id);
    UserDto modify(UserDto userDto);
    UserDto getUserByNickname(String nickname);
    //    public void delete(String userId);
    boolean isIdAvailable(String userid);
    boolean isNicknameAvailable(String nickname);
    List<UserDto> searchUsersByNickname(String nickname);

    boolean changePassword(String userId, String currentPassword, String newPassword);

    boolean deleteUser(String userId);
}
