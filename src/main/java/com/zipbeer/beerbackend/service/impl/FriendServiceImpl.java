package com.zipbeer.beerbackend.service.impl;

import com.zipbeer.beerbackend.dto.RoomDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.response.ResponseDto;
import com.zipbeer.beerbackend.entity.FriendEntity;
import com.zipbeer.beerbackend.entity.ParticipantEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.FriendRepository;
import com.zipbeer.beerbackend.repository.ParticipantRepository;
import com.zipbeer.beerbackend.repository.UserRepository;
import com.zipbeer.beerbackend.service.FriendService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> sendFriendRequest(String userId, String friendNickname) {
        UserEntity user = userRepository.findByUserId(userId);
        UserEntity friend;
        try {
            friend = userRepository.findByNickname(friendNickname).orElseThrow(EntityNotFoundException::new);
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 유저가 존재하지 않습니다.");
        }

        if(user.equals(friend)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("자기 자신은 \n 친구 추가할 수 없습니다.");
        }
        // 이미 친구인지 확인
        if (isFriend(userId, friendNickname)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 등록된 친구입니다.");
        }
        //이미 친구를 요청함
        if (friendRepository.existsByUserAndFriend(user, friend)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 친구 요청을 보냈습니다.");
        }

        // 친구 요청 생성
        FriendEntity friendRequest = new FriendEntity();
        friendRequest.setUser(user);
        friendRequest.setFriend(friend);
        friendRequest.setAccepted(false);
        friendRequest.setRequestedAt(LocalDateTime.now());

        // 친구 요청 저장
        friendRepository.save(friendRequest);
        return ResponseDto.success();
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

    // 친구 요청 목록 가져오기 (보낸 요청)
    @Override
    public List<UserDto> getSentFriendRequests(String userId) {
        UserEntity user = userRepository.findByUserId(userId);
        List<FriendEntity> requests = friendRepository.findByUserAndAccepted(user, false);
        return requests.stream().map(f -> new UserDto(f.getFriend())).collect(Collectors.toList());
    }

    // 친구 요청 목록 가져오기 (받은 요청)
    @Override
    public List<UserDto> getReceivedFriendRequests(String userId) {
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


    // 친구의 방 목록 가져오기
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
                .toList());

        List<ParticipantEntity> participantEntities = friendUsers.stream()
                .flatMap(friend -> participantRepository.findByUser(friend).stream())
                .toList();

        return participantEntities.stream()
                .map(participant -> {
                    RoomDto roomDto = new RoomDto(participant.getRoom());
                    UserDto friendDto = new UserDto(participant.getUser());
                    roomDto.setMaster(friendDto.getNickname()); // 친구의 닉네임 설정
                    roomDto.setParticipantList(List.of(friendDto)); // 친구의 이미지 포함 설정
                    roomDto.setCurrentUser(participant.getRoom().getParticipantCount());
                    roomDto.setMaximumUser(participant.getRoom().getMaximumUser());
                    return roomDto;
                })
                .collect(Collectors.toList());
    }

}
