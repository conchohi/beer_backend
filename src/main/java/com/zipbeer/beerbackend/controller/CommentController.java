package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.CommentDto;
import com.zipbeer.beerbackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;


    // 댓글 작성
    @PostMapping("")
    public CommentDto registerComment(@RequestBody CommentDto commentDto) {
        return commentService.registerComment(commentDto);
    }

    // 전체 댓글 조회
//    @GetMapping("")
//    public List<CommentDto> getAllComments() {
//        return commentService.getAllComments();
//    }

    // 특정 게시물에 대한 댓글 조회
    @GetMapping("/board/{boardNo}")
    public List<CommentDto> getCommentsByBoardNo(@PathVariable Long boardNo) {
        return commentService.getCommentsByBoardNo(boardNo);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentNo}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentNo) {
        commentService.deleteComment(commentNo);
        return ResponseEntity.noContent().build();
    }


}
