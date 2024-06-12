package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.RoomDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.request.PageRequestDto;
import com.zipbeer.beerbackend.dto.response.PageResponseDto;
import com.zipbeer.beerbackend.dto.response.ResponseDto;
import com.zipbeer.beerbackend.entity.RoomEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {
    private final RoomRepository roomRepository;

    //방 번호 최대, 최소
    private static final long MIN_ID = 10000;
    private static final long MAX_ID = 19999;

    //채팅방 생성
    public Long createRoom(RoomDto roomDto){
        //10000 ~ 19999 사이 랜덤 숫자 생성
        Long roomNo = generateRoomNo();
        String roomPw = roomDto.getRoomPw();
        RoomEntity room = RoomEntity.builder()
                .roomNo(roomNo)
                .title(roomDto.getTitle())
                .category(roomDto.getCategory())
                .maximumUser(roomDto.getMaximumUser())
                //비밀번호가 있을 경우에만 넣기
                .roomPw(StringUtils.hasText(roomPw) ? roomPw : null)
                .build();
        roomRepository.save(room);

        return roomNo;
    }

    //채팅방 수정
    public void modifyRoom(RoomDto roomDto){
        //제목, 카테고리, 최대 인원 수정 가능
        RoomEntity room = roomRepository.findById(roomDto.getRoomNo()).orElseThrow();
        room.setTitle(roomDto.getTitle());
        room.setCategory(roomDto.getCategory());
        room.setMaximumUser(roomDto.getMaximumUser());
        roomRepository.save(room);
    }

    //채팅방 폭파
    public void destroyRoom(Long roomNo){
        roomRepository.deleteById(roomNo);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> checkPassword(RoomDto roomDto){
        try {
            roomRepository.findById(roomDto.getRoomNo()).orElseThrow(EntityNotFoundException::new);
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","방이 존재하지 않습니다."));
        } catch (Exception e){
            return ResponseDto.databaseError();
        }

        boolean checkPassword =  roomRepository.existsByRoomNoAndRoomPw(roomDto.getRoomNo(), roomDto.getRoomPw());
        if (checkPassword) {
            return ResponseDto.success();
        } else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","비밀번호가 일치하지 않습니다"));
        }
    }

    //참여한 방 참여자, 제목 가져옴
    @Transactional(readOnly = true)
    public ResponseEntity<?> get(Long roomNo){
        RoomEntity room;
        try {
            room = roomRepository.findById(roomNo).orElseThrow(EntityNotFoundException::new);
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","방이 존재하지 않습니다."));
        } catch (Exception e){
            return ResponseDto.databaseError();
        }

        List<UserDto> userList = new ArrayList<>();
        for (UserEntity user : room.getUsers()) {
            //사용자의 닉네임과 프로필만 가져오기
            UserDto userDto = UserDto.builder()
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .build();

            userList.add(userDto);
        }
        RoomDto dto =  RoomDto.builder()
                .title(room.getTitle())
                .master(room.getMaster())
                .maximumUser(room.getMaximumUser())
                .participantList(userList)
                .build();

        return ResponseEntity.ok(dto);
    }

    //참가자 입장, 퇴장 시 현재 참가자 리스트 가져옴
    @Transactional(readOnly = true)
    public ResponseEntity<?> getParticipantList(Long roomNo){
        RoomEntity room;
        //해당 방이 없으면 에러 처리 -> 컨트롤러에서 처리
        try {
            room = roomRepository.findById(roomNo).orElseThrow(EntityNotFoundException::new);
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","방이 존재하지 않습니다."));
        } catch (Exception e){
            return ResponseDto.databaseError();
        }
        List<UserDto> userList = new ArrayList<>();
        for (UserEntity user : room.getUsers()) {
            //사용자의 닉네임과 프로필만 가져오기
            UserDto userDto = UserDto.builder()
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .build();

            userList.add(userDto);
        }

        return ResponseEntity.ok(userList);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<RoomDto> getList(PageRequestDto pageRequestDto){
        System.out.println(pageRequestDto);
        String searchTerm = pageRequestDto.getSearchTerm();
        String searchType = pageRequestDto.getSearchType();
        String category = pageRequestDto.getCategory();
        Sort sort = sortBy(pageRequestDto.getOrderBy());

        Pageable pageable = PageRequest.of(pageRequestDto.getPage()-1, pageRequestDto.getSize(),sort);

        Page<RoomEntity> roomEntityList;

        if(StringUtils.hasText(searchType) && searchType.equals("닉네임")){
            roomEntityList = roomRepository.findAllByNickname(pageable,searchTerm,category);
        } else{ //searchType = 방제목
            roomEntityList = roomRepository.findAllByTitle(pageable,searchTerm,category);
        }

        List<RoomDto> dtoList = new ArrayList<>();

        for (RoomEntity room : roomEntityList.getContent()) {
            //방 번호, 제목, 카테고리,
            RoomDto roomDto = RoomDto.builder()
                    .roomNo(room.getRoomNo())
                    .title(room.getTitle())
                    .category(room.getCategory())
                    .master(room.getMaster())
                    .currentUser(room.getParticipantCount())
                    .maximumUser(room.getMaximumUser())
                    .createDate(room.getCreateDate())
                    .roomPw(room.getRoomPw())
                    .build();

            dtoList.add(roomDto);
        }

        return PageResponseDto.<RoomDto>builder()
                .totalCount(roomEntityList.getTotalElements())
                .pageRequestDTO(pageRequestDto)
                .dtoList(dtoList)
                .build();
    }

    private Long generateRoomNo(){        //10000 ~ 19999 사이 랜덤 숫자 생성
        Random random = new Random();
        Long generatedNo;
        do {
            generatedNo = MIN_ID + random.nextLong((MAX_ID - MIN_ID + 1));
        } while (roomRepository.existsByRoomNo(generatedNo));
        return generatedNo;
    }

    private Sort sortBy(String orderBy){
        //기본적으론 최신 생성된 순
        Sort sort;
        if(orderBy.equals("오래된순")) {
            sort = Sort.by("createDate").ascending();
        } else if(orderBy.equals("최다인원순")) {
            sort = Sort.by("participantCount").descending().and(Sort.by("createDate").descending());
        } else if(orderBy.equals("최저인원순")){
            sort = Sort.by("participantCount").ascending().and(Sort.by("createDate").descending());
        } else {
            sort = Sort.by("createDate").descending();
        }
        return sort;
    }

}
