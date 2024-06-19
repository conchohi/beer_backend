package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.service.AuthService;
import com.zipbeer.beerbackend.service.UserService;
import com.zipbeer.beerbackend.util.FileUtil;
import com.zipbeer.beerbackend.provider.JWTProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final AuthService authService;
    private final UserService userService;
    private final FileUtil fileUtil;
    private final JWTProvider jwtProvider;

    @GetMapping("/info/{nickname}")
    public ResponseEntity<UserDto> getUserByNickname(@PathVariable("nickname") String nickname) {
        return ResponseEntity.ok(userService.getUserByNickname(nickname));
    }

    // 파일 이미지 가져오기
    @GetMapping("/{profileImage}")
    public ResponseEntity<Resource> getImage(@PathVariable("profileImage") String profileImage) {
        return fileUtil.getFile(profileImage);
    }

    // 수정
    @PatchMapping("")
    public ResponseEntity<?> modify(@ModelAttribute UserDto userDto) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        userDto.setUserId(id);
        UserDto result = userService.modify(userDto);
        return ResponseEntity.ok(result);
    }

    // 유저 정보 가져오기
    @GetMapping("")
    public ResponseEntity<UserDto> getUserInfo(){
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserDto userDto) {
        return authService.join(userDto);
    }

    // 아이디 중복 체크
    @PostMapping("/id-check")
    public ResponseEntity<Map<String, String>> checkIdAvailability(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        boolean isAvailable = userService.isIdAvailable(id);
        return ResponseEntity.ok(Map.of("message", isAvailable ? "Success." : "ID is already taken."));
    }

    // 닉네임 중복 체크
    @PostMapping("/nickname-check")
    public ResponseEntity<Map<String, String>> checkNicknameAvailability(@RequestBody Map<String, String> request) {
        String nickname = request.get("nickname");
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(Map.of("message", isAvailable ? "Success." : "Nickname is already taken."));
    }


    // 유저 닉네임 검색
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String nickname) {
        List<UserDto> users = userService.searchUsersByNickname(nickname);
        return ResponseEntity.ok(users);
    }


    // 패스워드 변경
    @PostMapping("/pwchange")
    public ResponseEntity<?> passwordChange(@RequestBody Map<String, String> request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        boolean isChanged = userService.changePassword(userId, currentPassword, newPassword);

        if (isChanged) {
            return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "Password change failed."));
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser() {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isDeleted = userService.deleteUser(currentUserId);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted successfully."));
        } else {
            return ResponseEntity.status(500).body(Map.of("message", "User deletion failed."));
        }
    }
}
