package com.zipbeer.beerbackend.dto;

import com.zipbeer.beerbackend.entity.UserEntity;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardDto {
    private Long boardNo;
    private String title;
    private String content;
    private String writer;
    private LocalDate regDate;
    private LocalDate modifyDate;
    private int count;

}
