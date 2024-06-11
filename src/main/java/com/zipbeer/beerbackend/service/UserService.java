package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto getUserById(String id);
    Optional<UserDto> updateUserByNickname(String nickname, UserDto userDto);
    void modify(UserDto userDto);
    UserDto getUserByNickname(String nickname);
    //    public void delete(String userId);
    boolean isIdAvailable(String userid);
    boolean isNicknameAvailable(String nickname);
    Optional<UserDto> updateNickname(String userId, String newNickname); // New method to update nickname
    List<String> getUserIdsByEmail(String email);
    boolean emailExists(String email);
    Optional<UserDto> updateUserProfile(String userId, MultipartFile profileFile);
}
