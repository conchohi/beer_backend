package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.service.UserService;
import com.zipbeer.beerbackend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final FileUtil fileUtil;

    @GetMapping("/{profileImage}")
    public ResponseEntity<Resource> getImage(@PathVariable("profileImage") String profileImage){
        return fileUtil.getFile(profileImage);
    }

    @PatchMapping("")
    public ResponseEntity<?> modify(@RequestBody UserDto userDto){
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        userDto.setUserId(id);
        userService.modify(userDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable("userId") String userId){
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }
}

