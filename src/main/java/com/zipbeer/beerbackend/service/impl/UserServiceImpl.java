package com.zipbeer.beerbackend.service.impl;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.UserRepository;
import com.zipbeer.beerbackend.service.UserService;
import com.zipbeer.beerbackend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileUtil fileUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto getUserById(String id) {
        return wrapUserEntity(userRepository.findByUserId(id))
                .map(UserDto::new)
                .orElse(null);
    }

    public UserDto getUserByNickname(String nickname) {
        UserEntity user = userRepository.findByNickname(nickname).orElseThrow();
        int followerCount = user.getFollowers().size();
        int followingCount = user.getFollowings().size();
        UserDto userDto = UserDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .age(user.getAge())
                .mbti(user.getMbti())
                .gender(user.getGender())
                .profileImage(user.getProfileImage())
                .intro(user.getIntro())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();
        return userDto;
    }

    @Override
    public UserDto modify(UserDto userDto) {
        UserEntity user = wrapUserEntity(userRepository.findByUserId(userDto.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));
        String beforeProfileImage = user.getProfileImage();
        String profileImage = null;
        MultipartFile multipartFile = userDto.getProfileFile();
        if (multipartFile != null && !multipartFile.isEmpty()) {
            profileImage = fileUtil.saveFile(multipartFile);
            fileUtil.deleteFile(beforeProfileImage);
            user.setProfileImage(profileImage);
            userDto.setProfileImage(profileImage);
        } else{
            userDto.setProfileImage(beforeProfileImage);
        }
        if ("true".equals(userDto.getIsDelete())) {
            user.setProfileImage(null);
            fileUtil.deleteFile(beforeProfileImage);
        }
        user.setNickname(userDto.getNickname());
        user.setEmail(userDto.getEmail());
        user.setMbti(userDto.getMbti());
        user.setAge(userDto.getAge());
        user.setGender(userDto.getGender());
        user.setIntro(userDto.getIntro());
        userRepository.save(user);
        return userDto;
    }

    @Override
    public boolean isIdAvailable(String userid) {
        return !userRepository.existsByUserId(userid);
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    // Helper method to wrap UserEntity in Optional
    private Optional<UserEntity> wrapUserEntity(UserEntity userEntity) {
        return Optional.ofNullable(userEntity);
    }




    @Override
    public List<UserDto> searchUsersByNickname(String nickname) {
        List<UserEntity> users = userRepository.findByNicknameContainingIgnoreCase(nickname);
        return users.stream().map(UserDto::new).collect(Collectors.toList());
    }



    @Override
    public boolean changePassword(String userId, String currentPassword, String newPassword) {
        UserEntity user = userRepository.findByUserId(userId);
        if (user == null) {
            return false;
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Transactional
    @Override
    public boolean deleteUser(String userId) {
        try {
            userRepository.deleteById(userId);
            return true;
        } catch (Exception e) {
            // Log the error message
            return false;
        }
    }

}
