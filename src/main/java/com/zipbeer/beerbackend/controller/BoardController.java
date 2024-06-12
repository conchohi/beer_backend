package com.zipbeer.beerbackend.controller;


import com.zipbeer.beerbackend.dto.BoardDto;
import com.zipbeer.beerbackend.entity.BoardEntity;
import com.zipbeer.beerbackend.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping
    public List<BoardDto> getAllBoards() {
        return boardService.getAllBoards();
    }

    @GetMapping("/{id}")
    public BoardDto getBoardById(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }

    @PostMapping
    public BoardDto registerBoard(@RequestBody BoardDto board) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        board.setWriter(id);
        return boardService.registerBoard(board);
    }

    @PutMapping("/{id}")
    public BoardDto updateBoard(@PathVariable Long id, @RequestBody BoardDto board) {
        return boardService.updateBoard(id, board);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }


}
