package com.zipbeer.beerbackend.service.impl;

import com.zipbeer.beerbackend.dto.FollowDto;
import com.zipbeer.beerbackend.entity.FollowEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.FollowRepository;
import com.zipbeer.beerbackend.repository.UserRepository;
import com.zipbeer.beerbackend.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public FollowEntity followUser(FollowDto followDTO) {
        try {
            String userId = followDTO.getUserId();
            String followId = followDTO.getFollowId();

            UserEntity user = userRepository.findByUserId(userId);
            UserEntity follow = userRepository.findByUserId(followId);

            FollowEntity followEntity = FollowEntity.builder()
                    .user(user)
                    .follow(follow)
                    .build();

            return followRepository.save(followEntity);
        } catch (Exception e) {
            // 예외 처리 로직을 추가합니다.
            System.err.println("Error during followUser: " + e.getMessage());
            throw new RuntimeException("Error during followUser", e);
        }
    }

    @Override
    public void unfollowUser(FollowDto followDTO) {
        try {
            String userId = followDTO.getUserId();
            String followId = followDTO.getFollowId();

            Optional<FollowEntity> followEntityOptional = followRepository.findByUserUserIdAndFollowUserId(userId, followId);
            if (followEntityOptional.isPresent()) {
                followRepository.delete(followEntityOptional.get());
            } else {
                System.out.println("Follow relationship not found for userId: " + userId + " and followId: " + followId);
                throw new RuntimeException("Follow relationship not found for userId: " + userId + " and followId: " + followId);
            }
        } catch (Exception e) {
            // 예외 처리 로직을 추가합니다.
            System.err.println("Error during unfollowUser: " + e.getMessage());
            throw new RuntimeException("Error during unfollowUser", e);
        }
    }

    @Override
    public List<FollowEntity> getFollowers(String userId) {
        try {
            return followRepository.findByFollowUserId(userId);
        } catch (Exception e) {
            // 예외 처리 로직을 추가합니다.
            System.err.println("Error during getFollowers: " + e.getMessage());
            throw new RuntimeException("Error during getFollowers", e);
        }
    }

    @Override
    public List<FollowEntity> getFollowing(String userId) {
        try {
            return followRepository.findByUser_UserId(String.valueOf(userId));
        } catch (Exception e) {
            // 예외 처리 로직을 추가합니다.
            System.err.println("Error during getFollowing: " + e.getMessage());
            throw new RuntimeException("Error during getFollowing", e);
        }
    }
}
