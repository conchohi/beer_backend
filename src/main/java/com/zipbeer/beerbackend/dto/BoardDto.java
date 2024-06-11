package com.zipbeer.beerbackend.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

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

    private List<CommentDto> commentList;

}
