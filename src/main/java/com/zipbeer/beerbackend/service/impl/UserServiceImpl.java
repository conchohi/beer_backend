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
        UserEntity user = userRepository.findByUserId(userDto.getUsername());
        //기존 이미지 이름
        String beforeProfileImage = userDto.getProfileImage();
        String profileImage = null;
        MultipartFile multipartFile = userDto.getProfileFile();
        //새로운 이미지가 왔으면 저장하고 기존 이미지 삭제
        if(multipartFile != null) {
            profileImage = fileUtil.saveFile(multipartFile);
            fileUtil.deleteFile(beforeProfileImage);
            user.setProfileImage(profileImage);
        }
        if(userDto.getIsDelete().equals("true")){
            user.setProfileImage(null);
            fileUtil.deleteFile(beforeProfileImage);
        }

        user.setNickname(userDto.getNickname());
        userRepository.save(user);
    }

//    @Override
//    public void delete(String userId) {
//
//    }
}
