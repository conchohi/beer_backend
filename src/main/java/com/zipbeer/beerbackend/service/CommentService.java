package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.CommentDto;
import com.zipbeer.beerbackend.entity.BoardEntity;
import com.zipbeer.beerbackend.entity.CommentEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.BoardRepository;
import com.zipbeer.beerbackend.repository.CommentRepository;
import com.zipbeer.beerbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    //전체조회
//    public List<CommentDto> getAllComments() {
//        List<CommentEntity> comments = commentRepository.findAll();
//        List<CommentDto> commentDtos = new ArrayList<>();
//
//        for (CommentEntity comment : comments) {
//            CommentDto commentDto = entityToDto(comment);
//            commentDtos.add(commentDto);
//        }
//
//        return commentDtos;
//    }

    // 특정 게시물에 대한 댓글 조회
    public List<CommentDto> getCommentsByBoardNo(Long boardNo) {
        List<CommentEntity> comments = commentRepository.findByBoardBoardNo(boardNo);
        List<CommentDto> commentDtos = new ArrayList<>();

        for (CommentEntity comment : comments) {
            CommentDto commentDto = entityToDto(comment);
            commentDtos.add(commentDto);
        }

        return commentDtos;
    }

    //등록
    public CommentDto registerComment(CommentDto commentDto) {
        UserEntity user = userRepository.findById(commentDto.getWriterId()).orElseThrow();
        BoardEntity board = boardRepository.findById(commentDto.getBoardNo()).orElseThrow();

        CommentEntity commentEntity = CommentEntity.builder()
                .writer(user)
                .board(board)
                .content(commentDto.getContent())
                .build();

        CommentEntity saveComment = commentRepository.save(commentEntity);

        return entityToDto(saveComment);
    }

    //삭제
    public void deleteComment(Long commentNo) {
        commentRepository.deleteById(commentNo);
    }



    private CommentDto entityToDto(CommentEntity commentEntity) {
        return CommentDto.builder()
                .commentNo(commentEntity.getCommentNo())
                .nickname(commentEntity.getWriter().getNickname())
                .profileImage(commentEntity.getWriter().getProfileImage())
                .boardNo(commentEntity.getBoard().getBoardNo())
                .content(commentEntity.getContent())
                .createDate(commentEntity.getCreateDate())
                .build();
    }


}