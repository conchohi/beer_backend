package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.RoomDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.service.FriendService;
import com.zipbeer.beerbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendController {
    private final FriendService friendService;
    private final UserService userService;

    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestBody Map<String, String> request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String friendNickname = request.get("nickname");
        return friendService.sendFriendRequest(userId, friendNickname);
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody Map<String, String> request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String friendNickname = request.get("nickname");
        friendService.acceptFriendRequest(userId, friendNickname);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decline")
    public ResponseEntity<?> declineFriendRequest(@RequestBody Map<String, String> request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String friendNickname = request.get("nickname");
        friendService.declineFriendRequest(userId, friendNickname);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFriend(@RequestBody Map<String, String> request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String friendNickname = request.get("nickname");
        friendService.deleteFriend(userId, friendNickname);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> getFriends() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserDto> friends = friendService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<UserDto>> getFriendRequests() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserDto> friendRequests = friendService.getReceivedFriendRequests(userId);
        return ResponseEntity.ok(friendRequests);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDto>> getFriendsRooms() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<RoomDto> rooms = friendService.getFriendsRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam("nickname") String nickname) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 현재 사용자와 이미 친구인 사람들의 목록 가져오기
        List<UserDto> excludedUsers = friendService.getFriends(userId);

        // 현재 사용자 정보 추가
        UserDto currentUser = userService.getUserById(userId);
        excludedUsers.add(currentUser);

        // 친구 요청 보낸 사람들의 목록 가져오기
        List<UserDto> sentRequests = friendService.getSentFriendRequests(userId);
        excludedUsers.addAll(sentRequests);

        // 친구 요청 받은 사람들의 목록 가져오기
        List<UserDto> receivedRequests = friendService.getReceivedFriendRequests(userId);
        excludedUsers.addAll(receivedRequests);

        // 사용자 검색
        List<UserDto> users = userService.searchUsersByNickname(nickname);

        // 현재 사용자와 이미 친구인 사람들을 검색 결과에서 제외
        users = users.stream()
                .filter(user -> !excludedUsers.contains(user))
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

}
