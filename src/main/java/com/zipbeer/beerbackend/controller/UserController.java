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

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<UserDto> getUserByNickname(@PathVariable String nickname) {
        Optional<UserDto> userDto = userService.getUserByNickname(nickname);
        return userDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/token/nickname")
    public ResponseEntity<Map<String, String>> getNicknameByToken(@RequestHeader("Authorization") String token) {
        String nickname = jwtProvider.getNickname(token.replace("Bearer ", ""));
        return ResponseEntity.ok(Map.of("nickname", nickname));
    }

    @GetMapping("/token/user")
    public ResponseEntity<UserDto> getUserByToken(@RequestHeader("Authorization") String token) {
        String nickname = jwtProvider.getNickname(token.replace("Bearer ", ""));
        UserDto userDto = userService.getUserByNickname(nickname).orElse(null);
        if (userDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDto);
    }

    @PutMapping(value = "/update/{nickname}", consumes = "multipart/form-data")
    public ResponseEntity<UserDto> updateUserByNickname(
            @PathVariable String nickname,
            @RequestPart("userDto") UserDto userDto,
            @RequestPart(value = "profileFile", required = false) MultipartFile profileFile) {
        userDto.setProfileFile(profileFile);
        Optional<UserDto> updatedUser = userService.updateUserByNickname(nickname, userDto);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update-nickname")
    public ResponseEntity<UserDto> updateNickname(@RequestBody Map<String, String> request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String newNickname = request.get("nickname");
        Optional<UserDto> updatedUser = userService.updateNickname(userId, newNickname);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/upload-profile")
    public ResponseEntity<UserDto> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserDto> updatedUser = userService.updateUserProfile(userId, file);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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

    @GetMapping("/info/{userId}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserById(userId);
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
