package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.RoomDto;  // 추가: 방 정보를 반환하기 위한 DTO
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FriendService {
    ResponseEntity<?> sendFriendRequest(String userId, String friendNickname); // 친구 요청 보내기
    void acceptFriendRequest(String userId, String friendNickname); // 친구 요청 수락하기
    void declineFriendRequest(String userId, String friendNickname); // 친구 요청 거절하기
    void deleteFriend(String userId, String friendNickname); // 친구 삭제하기
    List<UserDto> getFriends(String userId); // 친구 목록 가져오기

    boolean isFriend(String userId, String friendNickname); // 친구 여부 확인하기
    List<RoomDto> getFriendsRooms(String userId); // 친구가 참여한 방 정보를 가져오는 메서드

    // 친구 요청 목록 가져오기 (보낸 요청)
    List<UserDto> getSentFriendRequests(String userId);

    // 친구 요청 목록 가져오기 (받은 요청)
    List<UserDto> getReceivedFriendRequests(String userId);
}
