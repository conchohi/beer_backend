package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.UserDto;

public interface UserService {
    void modify(UserDto userDto);
    UserDto getUserById(String userId);
    void encodeExistingPasswords();
}
