package com.zipbeer.beerbackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatServiceTest {
    @Autowired
    ParticipantService participantService;

    @Test
    void join() {
        participantService.join("abc123",12609L);
        participantService.join("xyz234",12609L);
    }

    @Test
    void exit() {
        participantService.exit("abc123",11790L);
    }
}