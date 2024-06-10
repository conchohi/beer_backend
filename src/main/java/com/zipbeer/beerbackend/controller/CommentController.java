package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.CommentDto;
import com.zipbeer.beerbackend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class CommentController {

    @Autowired
    private CommentService commentService;


    // 댓글 작성
    @PostMapping("")
    public CommentDto registerComment(@RequestBody CommentDto commentDto) {
        return commentService.registerComment(commentDto);
    }

    // 전체 댓글 조회
//    @GetMapping
//    public List<CommentDto> getAllComments() {
//        return commentService.getAllComments();
//    }

    // 특정 게시물에 대한 댓글 조회
    @GetMapping("/board/{boardNo}")
    public List<CommentDto> getCommentsByBoardNo(@PathVariable Long boardNo) {
        return commentService.getCommentsByBoardNo(boardNo);
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }


}
