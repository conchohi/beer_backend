package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.RoomDto;
import com.zipbeer.beerbackend.dto.request.PageRequestDto;
import com.zipbeer.beerbackend.dto.response.PageResponseDto;
import com.zipbeer.beerbackend.dto.response.ResponseDto;
import com.zipbeer.beerbackend.service.ParticipantService;
import com.zipbeer.beerbackend.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
public class RoomController {
    private final RoomService roomService;
    private final ParticipantService participantService;
    @GetMapping("/{roomNo}")
    public ResponseEntity<?> getRoom(@PathVariable(name = "roomNo") Long roomNo){
        return roomService.get(roomNo);
    }

    @GetMapping("/{roomNo}/participant")
    public ResponseEntity<?> getParticipantList(@PathVariable(name = "roomNo") Long roomNo){
        return roomService.getParticipantList(roomNo);
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponseDto<RoomDto>> getRoomList(PageRequestDto pageRequestDto){
        PageResponseDto<RoomDto> pageResponseDto = roomService.getList(pageRequestDto);
        return ResponseEntity.ok(pageResponseDto);
    }

    @PostMapping("/checkPw")
    public ResponseEntity<?> checkPassword(@RequestBody RoomDto roomDto){
        boolean checkPassword = roomService.checkPassword(roomDto);
        if (checkPassword) {
            return ResponseDto.success();
        } else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message","비밀번호가 일치하지 않습니다"));
        }
    }

    @PostMapping
    public Long createRoom(@RequestBody RoomDto roomDto){
        return roomService.createRoom(roomDto);
    }

    @DeleteMapping("/{roomNo}")
    public void destroyRoom(@PathVariable(name = "roomNo") Long roomNo){
        roomService.destroyRoom(roomNo);
    }

    @PostMapping("/join/{roomNo}")
    public ResponseEntity<?> join(@PathVariable(name = "roomNo") Long roomNo){
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return participantService.join(id,roomNo);
//        return participantService.join("abc123", roomNo);
    }

    @DeleteMapping("/exit/{roomNo}")
    public ResponseEntity<?> exit(@PathVariable(name = "roomNo") Long roomNo){
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return participantService.exit(id,roomNo);
//        return participantService.exit("abc123",roomNo);
    }
}
