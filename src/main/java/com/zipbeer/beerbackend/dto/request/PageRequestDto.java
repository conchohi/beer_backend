package com.zipbeer.beerbackend.dto.request;

import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Setter
public class PageRequestDto {
    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int size = 6;
    private String category;
    @Builder.Default
    private String searchType = "방제목";

    private String searchTerm;
    @Builder.Default
    private String orderBy = "최신순";
}
