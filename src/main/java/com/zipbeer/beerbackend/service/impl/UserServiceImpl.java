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

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileUtil fileUtil;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDto getUserById(String id) {
        UserEntity user = userRepository.findByUserId(id);
        UserDto userDto = UserDto.builder()
                .age(user.getAge())
                .mbti(user.getMbti())
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .build();

        return userDto;
    }

    //유저 데이터 가져오기
    @Override
    public Optional<UserEntity> getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

        //닉네임받아서 유저 수정
    @Override
    public Optional<UserEntity> updateUserByNickname(String nickname, UserEntity updatedUser) {
        Optional<UserEntity> existingUser = userRepository.findByNickname(nickname);
        if (existingUser.isPresent()) {
            UserEntity user = existingUser.get();
            user.setMbti(updatedUser.getMbti());
            user.setAge(updatedUser.getAge());
            user.setIntro(updatedUser.getIntro());
            user.setProfileImage(updatedUser.getProfileImage());
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }
    
    @Override
    public void modify(UserDto userDto) {
        UserEntity user = userRepository.findByUserId(userDto.getUserId());
        // 기존 이미지 이름
        String beforeProfileImage = user.getProfileImage();
        String profileImage = null;
        MultipartFile multipartFile = userDto.getProfileFile();
        // 새로운 이미지가 왔으면 저장하고 기존 이미지 삭제
        if(multipartFile != null) {
            profileImage = fileUtil.saveFile(multipartFile);
            fileUtil.deleteFile(beforeProfileImage);
            user.setProfileImage(profileImage);
        }
        if("true".equals(userDto.getIsDelete())) {
            user.setProfileImage(null);
            fileUtil.deleteFile(beforeProfileImage);
        }

        user.setNickname(userDto.getNickname());
        userRepository.save(user);
    }


    public boolean isIdAvailable(String userid) {
        return !userRepository.existsByUserId(userid);
    }

    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    @Override
    public List<String> getUserIdsByEmail(String email) {
        return userRepository.findUserIdsByEmail(email);
    }
    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

//    @Override
//    public void delete(String userId) {
//
//    }
}
