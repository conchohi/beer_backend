package com.zipbeer.beerbackend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoomRepositoryTest {
    @Autowired
    RoomRepository roomRepository;

    @Test
    void isEmptyRoom() {
        System.out.println(roomRepository.isEmptyRoomWhenExit(11790L));
        System.out.println(roomRepository.isEmptyRoomWhenExit(11118L));
    }
}