package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.UserDto;


public interface UserService {
    public void modify(UserDto userDto);
    UserDto getUserById(String userId);
//    public void delete(String userId);
}
