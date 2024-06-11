package com.zipbeer.beerbackend.service.impl;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.UserRepository;
import com.zipbeer.beerbackend.service.UserService;
import com.zipbeer.beerbackend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

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
                .followerCount(followingCount)
                .build();
        return userDto;
    }

    @Override
    public Optional<UserDto> updateUserByNickname(String nickname, UserDto userDto) {
        Optional<UserEntity> optionalUser = userRepository.findByNickname(nickname);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            user.setMbti(userDto.getMbti());
            user.setAge(userDto.getAge());
            user.setIntro(userDto.getIntro());

            MultipartFile multipartFile = userDto.getProfileFile();
            if (multipartFile != null && !multipartFile.isEmpty()) {
                String profileImage = fileUtil.saveFile(multipartFile);
                fileUtil.deleteFile(user.getProfileImage());
                user.setProfileImage(profileImage);
            }

            userRepository.save(user);
            return Optional.of(new UserDto(user));
        }
        return Optional.empty();
    }

    @Override
    public void modify(UserDto userDto) {
        UserEntity user = wrapUserEntity(userRepository.findByUserId(userDto.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));
        String beforeProfileImage = user.getProfileImage();
        String profileImage = null;
        MultipartFile multipartFile = userDto.getProfileFile();
        if (multipartFile != null && !multipartFile.isEmpty()) {
            profileImage = fileUtil.saveFile(multipartFile);
            fileUtil.deleteFile(beforeProfileImage);
            user.setProfileImage(profileImage);
        }
        if ("true".equals(userDto.getIsDelete())) {
            user.setProfileImage(null);
            fileUtil.deleteFile(beforeProfileImage);
        }
        user.setNickname(userDto.getNickname());
        userRepository.save(user);
    }

    @Override
    public boolean isIdAvailable(String userid) {
        return !userRepository.existsByUserId(userid);
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    @Override
    public Optional<UserDto> updateNickname(String userId, String newNickname) {
        Optional<UserEntity> optionalUser = wrapUserEntity(userRepository.findByUserId(userId));
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            if (!userRepository.existsByNickname(newNickname)) {
                user.setNickname(newNickname);
                userRepository.save(user);
                return Optional.of(new UserDto(user));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<String> getUserIdsByEmail(String email) {
        return userRepository.findUserIdsByEmail(email);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<UserDto> updateUserProfile(String userId, MultipartFile profileFile) {
        Optional<UserEntity> optionalUser = wrapUserEntity(userRepository.findByUserId(userId));
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            String beforeProfileImage = user.getProfileImage();
            String profileImage = fileUtil.saveFile(profileFile);
            user.setProfileImage(profileImage);
            fileUtil.deleteFile(beforeProfileImage);
            userRepository.save(user);
            return Optional.of(new UserDto(user));
        }
        return Optional.empty();
    }

    // Helper method to wrap UserEntity in Optional
    private Optional<UserEntity> wrapUserEntity(UserEntity userEntity) {
        return Optional.ofNullable(userEntity);
    }
}
