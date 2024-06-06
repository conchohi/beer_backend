package com.zipbeer.beerbackend.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@Setter
public class PageRequestDto {
    private int page;
    private int size;
    private String category;
    private String searchType;
    private String searchTerm;
    private String orderBy;
}
