package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.CommentDto;
import com.zipbeer.beerbackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        commentDto.setWriterId(id);
        return commentService.registerComment(commentDto);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentNo}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentNo) {
        commentService.deleteComment(commentNo);
        return ResponseEntity.noContent().build();
    }


}
