package com.zipbeer.beerbackend.service.impl;

import com.zipbeer.beerbackend.dto.RoomDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.entity.FriendEntity;
import com.zipbeer.beerbackend.entity.ParticipantEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.FriendRepository;
import com.zipbeer.beerbackend.repository.ParticipantRepository;
import com.zipbeer.beerbackend.repository.UserRepository;
import com.zipbeer.beerbackend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final ParticipantRepository participantRepository;

    // 친구 요청 보내기
    @Override
    public void sendFriendRequest(String userId, String friendNickname) {
        UserEntity user = userRepository.findByUserId(userId);
        UserEntity friend = userRepository.findByNickname(friendNickname).orElseThrow(() -> new RuntimeException("User not found"));

        // 이미 친구인지 확인
        if (isFriend(userId, friendNickname)) {
            throw new RuntimeException("Already friends");
        }

        // 친구 요청 생성
        FriendEntity friendRequest = new FriendEntity();
        friendRequest.setUser(user);
        friendRequest.setFriend(friend);
        friendRequest.setAccepted(false);
        friendRequest.setRequestedAt(LocalDateTime.now());

        // 친구 요청 저장
        friendRepository.save(friendRequest);
    }

    // 친구 요청 수락하기
    @Override
    public void acceptFriendRequest(String userId, String friendNickname) {
        UserEntity user = userRepository.findByUserId(userId);
        UserEntity friend = userRepository.findByNickname(friendNickname).orElseThrow(() -> new RuntimeException("User not found"));

        // 친구 요청 찾기
        FriendEntity friendRequest = friendRepository.findByUserAndFriend(friend, user)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        // 친구 요청 수락 처리
        friendRequest.setAccepted(true);
        friendRequest.setAcceptedAt(LocalDateTime.now());

        // 업데이트된 친구 요청 저장
        friendRepository.save(friendRequest);
    }

    // 친구 요청 거절하기
    @Override
    public void declineFriendRequest(String userId, String friendNickname) {
        UserEntity user = userRepository.findByUserId(userId);
        UserEntity friend = userRepository.findByNickname(friendNickname).orElseThrow(() -> new RuntimeException("User not found"));

        // 친구 요청 찾기
        FriendEntity friendRequest = friendRepository.findByUserAndFriend(friend, user)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        // 친구 요청 삭제 처리
        friendRepository.delete(friendRequest);
    }

    // 친구 삭제하기
    @Override
    public void deleteFriend(String userId, String friendNickname) {
        UserEntity user = userRepository.findByUserId(userId);
        UserEntity friend = userRepository.findByNickname(friendNickname).orElseThrow(() -> new RuntimeException("User not found"));

        // 친구 관계가 수락되었는지 여부와 상관없이 조회
        Optional<FriendEntity> friendRelationOpt = friendRepository.findByUserAndFriend(user, friend)
                .stream().findFirst()
                .or(() -> friendRepository.findByUserAndFriend(friend, user).stream().findFirst());

        // 친구 관계 찾기
        FriendEntity friendRelation = friendRelationOpt.orElseThrow(() -> new RuntimeException("Friend relation not found"));

        // 친구 관계 삭제
        friendRepository.delete(friendRelation);
    }

    // 친구 목록 가져오기
    @Override
    public List<UserDto> getFriends(String userId) {
        UserEntity user = userRepository.findByUserId(userId);
        List<FriendEntity> friends = friendRepository.findByUserAndAccepted(user, true);
        List<FriendEntity> acceptedFriends = friendRepository.findByFriendAndAccepted(user, true);

        // 친구 목록 변환
        List<UserDto> friendList = friends.stream().map(f -> new UserDto(f.getFriend())).collect(Collectors.toList());
        List<UserDto> acceptedFriendList = acceptedFriends.stream().map(f -> new UserDto(f.getUser())).collect(Collectors.toList());

        // 친구 목록 합치기
        friendList.addAll(acceptedFriendList);
        return friendList;
    }

    // 친구 요청 목록 가져오기
    @Override
    public List<UserDto> getFriendRequests(String userId) {
        UserEntity user = userRepository.findByUserId(userId);
        List<FriendEntity> requests = friendRepository.findByFriendAndAccepted(user, false);
        return requests.stream().map(f -> new UserDto(f.getUser())).collect(Collectors.toList());
    }

    // 친구 여부 확인하기
    @Override
    public boolean isFriend(String userId, String friendNickname) {
        UserEntity user = userRepository.findByUserId(userId);
        UserEntity friend = userRepository.findByNickname(friendNickname).orElseThrow(() -> new RuntimeException("User not found"));

        // 친구 관계 존재 여부 확인
        return friendRepository.existsByUserAndFriendAndAccepted(user, friend, true) ||
                friendRepository.existsByUserAndFriendAndAccepted(friend, user, true);
    }

    @Override
    public List<RoomDto> getFriendsRooms(String userId) {
        UserEntity user = userRepository.findByUserId(userId);
        List<FriendEntity> friends = friendRepository.findByUserAndAccepted(user, true);
        List<FriendEntity> acceptedFriends = friendRepository.findByFriendAndAccepted(user, true);

        List<UserEntity> friendUsers = friends.stream()
                .map(FriendEntity::getFriend)
                .collect(Collectors.toList());
        friendUsers.addAll(acceptedFriends.stream()
                .map(FriendEntity::getUser)
                .collect(Collectors.toList()));

        List<ParticipantEntity> participantEntities = friendUsers.stream()
                .flatMap(friend -> participantRepository.findByUser(friend).stream())
                .collect(Collectors.toList());

        return participantEntities.stream()
                .map(participant -> new RoomDto(participant.getRoom()))
                .collect(Collectors.toList());
    }
}
