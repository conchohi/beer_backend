package com.zipbeer.beerbackend.dto.response;

import com.zipbeer.beerbackend.dto.request.PageRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
@ToString

public class PageResponseDto<E> {
    private List<E> dtoList;

    private List<Integer> pageNumList;

    private PageRequestDto pageRequestDTO;

    private boolean prev, next;

    private int totalCount, prevPage, nextPage, totalPage, current;
    @Builder
    public PageResponseDto(List<E> dtoList, PageRequestDto pageRequestDTO, long totalCount) {
        //5개의 페이지만
        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = (int)totalCount;

        int end =   (int)(Math.ceil( pageRequestDTO.getPage() / 5.0 )) *  5;

        int start = end - 4;

        int last =  (int)(Math.ceil((totalCount/(double)pageRequestDTO.getSize())));

        end = (end > last) ? last : end;

        this.prev = start > 1;


        this.next = totalCount > (end * pageRequestDTO.getSize());

        //페이지 처리를 위해 필요한 번호
        this.pageNumList = IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList());

        if(prev) {
            this.prevPage = start -1;
        }

        if(next) {
            this.nextPage = end + 1;
        }

        this.totalPage = this.pageNumList.size();

        this.current = pageRequestDTO.getPage();

    }
}
