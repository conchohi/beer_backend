package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.service.UserService;
import com.zipbeer.beerbackend.util.FileUtil;
import com.zipbeer.beerbackend.provider.JWTProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final FileUtil fileUtil;
    private final JWTProvider jwtProvider;

    @GetMapping("/info/{nickname}")
    public ResponseEntity<UserDto> getUserByNickname(@PathVariable("nickname") String nickname) {
        return ResponseEntity.ok(userService.getUserByNickname(nickname));

    }

    @GetMapping("/{profileImage}")
    public ResponseEntity<Resource> getImage(@PathVariable("profileImage") String profileImage) {
        return fileUtil.getFile(profileImage);
    }

    @PatchMapping("")
    public ResponseEntity<?> modify(@RequestBody UserDto userDto) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        userDto.setUserId(id);
        userService.modify(userDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("")
    public ResponseEntity<UserDto> getUserInfo(){
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/id-check")
    public ResponseEntity<Map<String, String>> checkIdAvailability(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        boolean isAvailable = userService.isIdAvailable(id);
        return ResponseEntity.ok(Map.of("message", isAvailable ? "Success." : "ID is already taken."));
    }

    @PostMapping("/nickname-check")
    public ResponseEntity<Map<String, String>> checkNicknameAvailability(@RequestBody Map<String, String> request) {
        String nickname = request.get("nickname");
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(Map.of("message", isAvailable ? "Success." : "Nickname is already taken."));
    }


}
