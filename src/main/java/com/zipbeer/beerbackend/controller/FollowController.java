package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.FollowDto;
import com.zipbeer.beerbackend.entity.FollowEntity;
import com.zipbeer.beerbackend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Authenticated");
    }


    @PostMapping("/follow")
    public ResponseEntity<FollowEntity> followUser(@RequestBody FollowDto followDTO) {
        FollowEntity followEntity = followService.followUser(followDTO);
        return ResponseEntity.ok(followEntity);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<Void> unfollowUser(@RequestBody FollowDto followDTO) {
        followService.unfollowUser(followDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers")
    public ResponseEntity<List<FollowEntity>> getFollowers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            // 인증 정보가 없는 경우 기본 응답 처리
            return ResponseEntity.status(403).body(null); // 또는 적절한 오류 응답 처리
        }

        String id = authentication.getName();
        List<FollowEntity> followers = followService.getFollowers(id);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<FollowEntity>> getFollowing(@PathVariable String userId) {
        List<FollowEntity> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @ControllerAdvice
    class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleException(Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}