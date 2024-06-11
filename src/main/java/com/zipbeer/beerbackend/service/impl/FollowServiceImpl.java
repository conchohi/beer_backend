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
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public FollowEntity followUser(FollowDto followDTO) {
        String userId = followDTO.getUserId();
        String followId = followDTO.getFollowId();

        if (userId.equals(followId)) {
            throw new RuntimeException("User cannot follow themselves");
        }

        UserEntity user = userRepository.findByUserId(userId);
        UserEntity follow = userRepository.findByUserId(followId);

        if (user == null) {
            throw new RuntimeException("User entity not found for userId: " + userId);
        }
        if (follow == null) {
            throw new RuntimeException("Follow entity not found for followId: " + followId);
        }

        Optional<FollowEntity> existingFollow = followRepository.findByUserUserIdAndFollowUserId(userId, followId);
        if (existingFollow.isPresent()) {
            throw new RuntimeException("User " + userId + " is already following " + followId);
        }

        FollowEntity followEntity = FollowEntity.builder()
                .user(user)
                .follow(follow)
                .build();

        return followRepository.save(followEntity);
    }

    @Override
    @Transactional
    public void unfollowUser(FollowDto followDTO) {
        String userId = followDTO.getUserId();
        String followId = followDTO.getFollowId();

        Optional<FollowEntity> followEntityOptional = followRepository.findByUserUserIdAndFollowUserId(userId, followId);
        if (followEntityOptional.isPresent()) {
            followRepository.delete(followEntityOptional.get());
        } else {
            throw new RuntimeException("Follow relationship not found for userId: " + userId + " and followId: " + followId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowDto> getFollowers(String userId) {
        List<FollowEntity> followEntities = followRepository.findByFollowUserId(userId);
        return followEntities.stream()
                .map(follow -> new FollowDto(follow.getFollowNo(), follow.getUser().getUserId(), follow.getFollow().getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowDto> getFollowing(String userId) {
        List<FollowEntity> followEntities = followRepository.findByUser_UserId(userId);
        return followEntities.stream()
                .map(follow -> new FollowDto(follow.getFollowNo(), follow.getUser().getUserId(), follow.getFollow().getUserId()))
                .collect(Collectors.toList());
    }
}
