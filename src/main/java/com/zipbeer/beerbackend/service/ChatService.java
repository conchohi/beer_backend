package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.response.ResponseDto;
import com.zipbeer.beerbackend.entity.ChatEntity;
import com.zipbeer.beerbackend.entity.RoomEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.ChatRepository;
import com.zipbeer.beerbackend.repository.RoomRepository;
import com.zipbeer.beerbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    //채팅방 참가
    public ResponseEntity<?> join(String userId, Long roomNo){
        UserEntity user = userRepository.findByUserId(userId);
        RoomEntity room;
        try {
            room = roomRepository.findById(roomNo).orElseThrow(EntityNotFoundException::new);
        }catch (EntityNotFoundException e){
            //방이 존재하지 않음
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Not exist room."));
        }catch (Exception e){
            //데이터베이스 에러
            return ResponseDto.databaseError();
        }
        //방이 가득참
        if(room.getParticipantCount() >= room.getMaximumUser()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Room is full."));
        }

        ChatEntity participant = ChatEntity.builder()
                .user(user)
                .room(room)
                .build();

        chatRepository.save(participant);
        return ResponseDto.success();
    }
    //채팅방 퇴장
    public ResponseEntity<?> exit(String userId, Long roomNo){
        UserEntity user = userRepository.findByUserId(userId);
        RoomEntity room;
        try {
            room = roomRepository.findById(roomNo).orElseThrow(EntityNotFoundException::new);
        }catch (EntityNotFoundException e){
            //방이 존재하지 않음
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Not exist room."));
        }catch (Exception e){
            //데이터베이스 에러
            return ResponseDto.databaseError();
        }
        chatRepository.deleteByUserAndRoom(user,room);
        return ResponseDto.success();
    }
}
