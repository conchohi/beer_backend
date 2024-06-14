package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.ReportDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReportServiceTest {

    @Autowired
    ReportService reportService;

    @Test
    void reportUser() {
        ReportDto reportDto = ReportDto.builder()
                        .reportedUser("xyz123")
                        .reporterUser("xyz123")
                                .title("ㅇㅇ")
                                        .content("ㅇㅇ")
                                                .reason("").build();
        reportService.reportUser(reportDto);
    }
}