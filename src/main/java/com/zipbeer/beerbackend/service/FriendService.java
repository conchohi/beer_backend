package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.RoomDto;  // 추가: 방 정보를 반환하기 위한 DTO

import java.util.List;

public interface FriendService {
    void sendFriendRequest(String userId, String friendNickname);
    void acceptFriendRequest(String userId, String friendNickname);
    void declineFriendRequest(String userId, String friendNickname);
    void deleteFriend(String userId, String friendNickname);
    List<UserDto> getFriends(String userId);
    List<UserDto> getFriendRequests(String userId);
    boolean isFriend(String userId, String friendNickname);
    List<RoomDto> getFriendsRooms(String userId); // 추가: 친구가 참여한 방 정보를 가져오는 메서드
}
