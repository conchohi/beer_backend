package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.RoomDto;
import com.zipbeer.beerbackend.dto.request.PageRequestDto;
import com.zipbeer.beerbackend.dto.response.PageResponseDto;
import com.zipbeer.beerbackend.entity.RoomEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoomServiceTest {
    @Autowired
    RoomService roomService;

    @Test
    void createRoom() {
        RoomDto roomDto1 = RoomDto.builder()
                .title("캔맥들고 모여")
                .roomPw("1234")
                .category("고민상담")
                .maximumUser(6)
                .build();

        System.out.println(roomService.createRoom(roomDto1));

        RoomDto roomDto2 = RoomDto.builder()
                .title("외로운 밤..")
                .category("회사생활")
                .maximumUser(6)
                .build();
        System.out.println(roomService.createRoom(roomDto2));
        RoomDto roomDto3 = RoomDto.builder()
                .title("같이 돌 잡으실분")
                .category("피트니스")
                .maximumUser(4)
                .build();
        System.out.println(roomService.createRoom(roomDto3));

        RoomDto roomDto4 = RoomDto.builder()
                .title("게임 할 사람")
                .category("게임")
                .maximumUser(6)
                .build();
        System.out.println(roomService.createRoom(roomDto4));

        RoomDto roomDto5 = RoomDto.builder()
                .title("배고파요")
                .category("친목")
                .maximumUser(4)
                .build();
        System.out.println(roomService.createRoom(roomDto5));

        RoomDto roomDto6 = RoomDto.builder()
                .title("제주도 같이 갈사람")
                .category("여행")
                .maximumUser(4)
                .build();
        System.out.println(roomService.createRoom(roomDto6));

    }

    @Test
    void modifyRoom() {
        RoomDto roomDto = RoomDto.builder()
                .roomNo(13209L)
                .title("제주도 갈사람?")
                .category("여행")
                .maximumUser(2)
                .build();
        roomService.modifyRoom(roomDto);
    }

    @Test
    void destroyRoom() {
        roomService.destroyRoom(15938L);
    }

    @Test
    void get() {
        System.out.println(roomService.get(11657L));
    }

    @Test
    void getParticipantList() {

    }

    @Test
    void getList() {
        PageRequestDto pageRequestDto = new PageRequestDto();
        pageRequestDto.setPage(1);
        pageRequestDto.setSize(6);
        pageRequestDto.setOrderBy("최다인원순");

        PageResponseDto<RoomDto> pageResponseDto = roomService.getList(pageRequestDto);
        System.out.println(pageResponseDto);
    }
}