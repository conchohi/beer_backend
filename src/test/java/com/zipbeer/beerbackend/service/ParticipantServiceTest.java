package com.zipbeer.beerbackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ParticipantServiceTest {
    @Autowired
    ParticipantService participantService;
    String[] users = {"kakao 3519701954","kakao 3519926444","kakao 3520577190","kakao 3521344739",
            "kakao 3521728823","kakao 3530338180","kakao 3568267758","kakao 3586822276","kakao 3587063272","kakao 3587750404"};
    @Test
    void join() throws InterruptedException {

        int numberOfThreads = 10;
        // 쓰레드 생성
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        // 주어진 수 만큼 이벤트를 기다림
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i <= numberOfThreads; i++) {

            // 각 쓰레드에서 사용할 요청 생성
            int finalI = i;
            executorService.submit(() -> {
                try {
                    System.out.println(finalI + "번째 쓰레드 접근 시작");
                    participantService.join(users[finalI],15840L);

                } catch (Exception e){
                    System.out.println(finalI + "번째 쓰레드 에러 " + e.getMessage());
                } finally {
                    latch.countDown();
                    System.out.println(finalI + "번째 쓰레드 접근 종료");
                }
            });
        }

        latch.await(); // 모든 쓰레드의 작업이 완료될 때까지 대기
        executorService.shutdown();
    }

    @Test
    void exit() {
        participantService.exit("kakao 3520577190",15840L);
    }
}