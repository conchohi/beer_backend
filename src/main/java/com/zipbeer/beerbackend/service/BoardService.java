package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.BoardDto;
import com.zipbeer.beerbackend.dto.CommentDto;
import com.zipbeer.beerbackend.entity.BoardEntity;
import com.zipbeer.beerbackend.entity.CommentEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.BoardRepository;
import com.zipbeer.beerbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    //전체조회
    public List<BoardDto> getAllBoards() {
        List<BoardEntity> boards = boardRepository.findAll(Sort.by("boardNo").descending());

        List<BoardDto> boardDtos= new ArrayList<>();

        for (BoardEntity board : boards) {
            BoardDto boardDto = entityToDto(board);
            boardDtos.add(boardDto);
        }

        return boardDtos;
    }

    //조회
    public BoardDto getBoardById(Long id) {
        BoardEntity board = boardRepository.findById(id).orElseThrow();
        //조회수 증가
        board.setCount(board.getCount()+1);

        return entityToDto(board);
    }

    //등록
    public BoardDto registerBoard(BoardDto board) {
        BoardEntity boardEntity = dtoToEntity(board);
        BoardEntity saveBoard = boardRepository.save(boardEntity);

        return entityToDto(saveBoard);
    }

    //수정
    public BoardDto updateBoard(Long id, BoardDto board) {
        BoardEntity getBoard = boardRepository.findById(id).orElseThrow();

        //제목, 내용, 수정일자
        getBoard.setTitle(board.getTitle());
        getBoard.setContent(board.getContent());
        getBoard.setModifyDate(LocalDate.now());

        BoardEntity updateBoard = boardRepository.save(getBoard);

        return entityToDto(updateBoard);
    }

    //삭제
    public void deleteBoard(Long id) {
        BoardEntity board = boardRepository.findById(id).orElseThrow();
        boardRepository.delete(board);
    }


    //Entity를 Dto로 변환하는 메소드
    private BoardDto entityToDto(BoardEntity boardEntity) {
        List<CommentDto> commentDtos = new ArrayList<>();
        if (boardEntity.getCommentEntityList() != null) {
            for (CommentEntity commentEntity : boardEntity.getCommentEntityList()) {
                CommentDto commentDto = CommentDto.builder()
                        .commentNo(commentEntity.getCommentNo())
                        .nickname(commentEntity.getWriter().getNickname())
                        .profileImage(commentEntity.getWriter().getProfileImage())
                        .content(commentEntity.getContent())
                        .createDate(commentEntity.getCreateDate())
                        .build();
                commentDtos.add(commentDto);
            }
        }

        BoardDto boardDto = BoardDto.builder()
                .boardNo(boardEntity.getBoardNo())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .writer(boardEntity.getWriter().getNickname())
                .regDate(boardEntity.getRegDate())
                .modifyDate(boardEntity.getModifyDate())
                .count(boardEntity.getCount())
                .commentList(commentDtos)
                .build();

        return boardDto;
    }

    // Dto를 Entity로 변환하는 메소드
    private BoardEntity dtoToEntity(BoardDto boardDto) {
        UserEntity writer = userRepository.findByUserId(boardDto.getWriter());

        BoardEntity boardEntity = BoardEntity.builder()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .writer(writer)
                .regDate(boardDto.getRegDate())
                .modifyDate(boardDto.getModifyDate())
                .build();

        return boardEntity;
    }

}
