package com.zipbeer.beerbackend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long commentNo;
    private String writerId;
    private Long boardNo;
    private String content;
    private LocalDateTime createDate;

}
