package com.zipbeer.beerbackend.dto;

import com.zipbeer.beerbackend.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Long reportNo;
    private String reporterUser;
    private String reportedUser;
    private String reason;
    private String title;
    private String content;
    private LocalDateTime reportDate;
    private boolean isProcess;
}
