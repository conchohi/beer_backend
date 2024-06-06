package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.RoomDto;
import com.zipbeer.beerbackend.dto.request.PageRequestDto;
import com.zipbeer.beerbackend.dto.response.PageResponseDto;
import com.zipbeer.beerbackend.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
public class RoomController {
    private final RoomService roomService;
    @GetMapping("/{roomNo}")
    public ResponseEntity<?> getRoom(@PathVariable Long roomNo){
        return roomService.get(roomNo);
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<RoomDto>> getRoomList(PageRequestDto pageRequestDto){
        PageResponseDto<RoomDto> pageResponseDto = roomService.getList(pageRequestDto);
        return ResponseEntity.ok(pageResponseDto);
    }

    @PostMapping("")
    public Long createRoom(@RequestBody RoomDto roomDto){
        return roomService.createRoom(roomDto);
    }

    @DeleteMapping("/{roomNo}")
    public void destroyRoom(@PathVariable Long roomNo){
        roomService.destroyRoom(roomNo);
    }

}
