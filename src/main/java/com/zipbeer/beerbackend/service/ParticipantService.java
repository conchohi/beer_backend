package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.response.ResponseDto;
import com.zipbeer.beerbackend.entity.ParticipantEntity;
import com.zipbeer.beerbackend.entity.RoomEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.ParticipantRepository;
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
public class ParticipantService {
    private final ParticipantRepository participantRepository;
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

        ParticipantEntity participant = ParticipantEntity.builder()
                .user(user)
                .room(room)
                .build();

        participantRepository.save(participant);
        return ResponseDto.success();
    }
    //채팅방 퇴장
    public ResponseEntity<?> exit(String userId, Long roomNo){
        try {
            roomRepository.findById(roomNo).orElseThrow(EntityNotFoundException::new);
        }catch (EntityNotFoundException e){
            //방이 존재하지 않음
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Not exist room."));
        }catch (Exception e){
            //데이터베이스 에러
            return ResponseDto.databaseError();
        }
        participantRepository.deleteByUserUserIdAndRoomRoomNo(userId,roomNo);
        return ResponseDto.success();
    }
}
