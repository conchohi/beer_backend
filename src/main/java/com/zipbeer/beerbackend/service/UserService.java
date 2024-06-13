package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto getUserById(String id);
    void modify(UserDto userDto);
    UserDto getUserByNickname(String nickname);
    //    public void delete(String userId);
    boolean isIdAvailable(String userid);
    boolean isNicknameAvailable(String nickname);
    List<UserDto> searchUsersByNickname(String nickname);
}
