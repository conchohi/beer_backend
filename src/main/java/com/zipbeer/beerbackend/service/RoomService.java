package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.RoomDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.request.PageRequestDto;
import com.zipbeer.beerbackend.dto.response.PageResponseDto;
import com.zipbeer.beerbackend.entity.ChatEntity;
import com.zipbeer.beerbackend.entity.RoomEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
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

    //참여한 방 참여자, 제목 가져옴
    @Transactional(readOnly = true)
    public RoomDto get(Long roomNo){
        //해당 방이 없으면 에러 처리 -> 컨트롤러에서 처리
        RoomEntity room = roomRepository.findById(roomNo).orElseThrow();
        List<ChatEntity> chatList = room.getParticipantList();
        List<UserDto> userList = new ArrayList<>();
        for (ChatEntity chatEntity : chatList) {
            UserEntity user = chatEntity.getUser();
            //사용자의 닉네임과 프로필만 가져오기
            UserDto userDto = UserDto.builder()
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .build();

            userList.add(userDto);
        }
        return RoomDto.builder()
                .title(room.getTitle())
                .master(room.getMaster())
                .participantList(userList)
                .build();
    }

    //참가자 입장, 퇴장 시 현재 참가자 리스트 가져옴
    @Transactional(readOnly = true)
    public List<UserDto> getParticipantList(Long roomNo){
        //해당 방이 없으면 에러 처리 -> 컨트롤러에서 처리
        RoomEntity room = roomRepository.findById(roomNo).orElseThrow();
        List<ChatEntity> chatList = room.getParticipantList();
        List<UserDto> userList = new ArrayList<>();
        for (ChatEntity chatEntity : chatList) {
            UserEntity user = chatEntity.getUser();
            //사용자의 닉네임과 프로필만 가져오기
            UserDto userDto = UserDto.builder()
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .build();

            userList.add(userDto);
        }

        return userList;
    }

    @Transactional(readOnly = true)
    public PageResponseDto<RoomDto> getList(PageRequestDto pageRequestDto){
        String searchTerm = pageRequestDto.getSearchTerm();
        String searchType = pageRequestDto.getSearchType();
        String category = pageRequestDto.getCategory();
        Sort sort = sortBy(pageRequestDto.getOrderBy());

        Pageable pageable = PageRequest.of(pageRequestDto.getPage()-1, pageRequestDto.getSize(),sort);

        Page<RoomEntity> roomEntityList;

        if(searchType.equals("방제목")){
            roomEntityList = roomRepository.findAllByTitle(pageable,searchTerm,category);
        } else{ //searchType = 닉네임
            roomEntityList = roomRepository.findAllByNickname(pageable,searchTerm,category);
        }

        List<RoomDto> dtoList = new ArrayList<>();

        for (RoomEntity room : roomEntityList.getContent()) {
            //방 번호, 제목, 카테고리,
            RoomDto roomDto = RoomDto.builder()
                    .roomNo(room.getRoomNo())
                    .title(room.getTitle())
                    .category(room.getCategory())
                    .master(room.getMaster())
                    .currentUser(room.getCurrentUserCount())
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
        } while (!roomRepository.existsByRoomNo(generatedNo));
        return generatedNo;
    }

    private Sort sortBy(String orderBy){
        //기본적으론 최신 생성된 순
        Sort sort;
        switch(orderBy){
            case "오래된순" : sort = Sort.by("create_date").ascending(); break;
            case "최다인원순" : sort = Sort.by("participant_count").descending(); break;
            case "최저인원순" : sort = Sort.by("participant_count").ascending(); break;
            default: sort = Sort.by("create_date").descending(); break;
        }
        return sort;
    }
}
