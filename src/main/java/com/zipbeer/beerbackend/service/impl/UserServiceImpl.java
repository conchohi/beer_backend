package com.zipbeer.beerbackend.service.impl;

import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.UserRepository;
import com.zipbeer.beerbackend.service.UserService;
import com.zipbeer.beerbackend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileUtil fileUtil;

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

    @Override
    public UserDto getUserById(String userId) {
        UserEntity user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserDto(user);
    }
}

 class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
