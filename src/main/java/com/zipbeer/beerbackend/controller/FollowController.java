package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.FollowDto;
import com.zipbeer.beerbackend.entity.FollowEntity;
import com.zipbeer.beerbackend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<FollowEntity> followUser(@RequestBody FollowDto followDto) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        followDto.setUserId(id);
        FollowEntity followEntity = followService.followUser(followDto);
        return ResponseEntity.ok(followEntity);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<Void> unfollowUser(@RequestBody FollowDto followDto) {
        followService.unfollowUser(followDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<FollowDto>> getFollowers(@PathVariable String userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
//            // 인증 정보가 없는 경우 기본 응답 처리
//            return ResponseEntity.status(403).body(null); // 또는 적절한 오류 응답 처리
//        }
//
//        String id = authentication.getName();

        List<FollowDto> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<FollowDto>> getFollowing(@PathVariable String userId) {
        List<FollowDto> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }
}
