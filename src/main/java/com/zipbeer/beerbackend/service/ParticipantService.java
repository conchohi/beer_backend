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
            room = roomRepository.findByRoomNo(roomNo).orElseThrow(EntityNotFoundException::new);
        }catch (EntityNotFoundException e){
            //방이 존재하지 않음
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","방이 존재하지 않습니다."));
        }catch (Exception e){
            //데이터베이스 에러
            return ResponseDto.databaseError();
        }
        //방이 가득참
        if(room.getParticipantCount() >= room.getMaximumUser()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","인원이 가득찼습니다."));
        }
        //DB에 EXIT 적용 안됐을 경우 기존 방 삭제
        if(participantRepository.existsByUserUserId(userId)){
            participantRepository.deleteByUser(user);
        }

        ParticipantEntity participant = ParticipantEntity.builder()
                .user(user)
                .room(room)
                .build();

        participantRepository.save(participant);
        room.setParticipantCount(room.getParticipantCount() + 1);
        if(room.getMaster() == null){
            room.setMaster(user.getNickname());
        }
        return ResponseDto.success();
    }

    //채팅방 퇴장
    public ResponseEntity<?> exit(String userId, Long roomNo){
        RoomEntity room;
        try {
            room = roomRepository.findByRoomNo(roomNo).orElseThrow(EntityNotFoundException::new);
        }catch (EntityNotFoundException e){
            //방이 존재하지 않음
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","Not exist room."));
        }catch (Exception e){
            //데이터베이스 에러
            return ResponseDto.databaseError();
        }

        //만약 내가 나간 후 방이 비면 해당 방 삭제
        if(room.getParticipantCount() == 1){
            roomRepository.delete(room);
        //방이 비지 않으면 방 나가기
        } else{
            participantRepository.deleteByUserUserIdAndRoomRoomNo(userId,roomNo);
            room.setParticipantCount(room.getParticipantCount()-1);
        }

        return ResponseDto.success();
    }
}
