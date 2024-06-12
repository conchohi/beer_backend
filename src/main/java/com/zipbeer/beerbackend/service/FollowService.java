package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.FollowDto;
import com.zipbeer.beerbackend.entity.FollowEntity;

import java.util.List;

public interface FollowService {
    FollowEntity followUser(FollowDto followDTO);
    void unfollowUser(FollowDto followDTO);
    List<FollowDto> getFollowers(String userId);
    List<FollowDto> getFollowing(String userId);
}
