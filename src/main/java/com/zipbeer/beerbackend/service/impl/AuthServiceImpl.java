package com.zipbeer.beerbackend.service.impl;

import com.zipbeer.beerbackend.common.CertificationNumber;
import com.zipbeer.beerbackend.dto.EmailCertificationDto;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.response.ResponseDto;
import com.zipbeer.beerbackend.entity.CertificationEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.provider.EmailProvider;
import com.zipbeer.beerbackend.repository.CertificationRepository;
import com.zipbeer.beerbackend.repository.UserRepository;
import com.zipbeer.beerbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final EmailProvider emailProvider;
    private final CertificationRepository certificationRepository;

    @Override
    public boolean idCheck(String id) {
        return userRepository.existsByUserId(id);
    }

    @Override
    public ResponseEntity<?> join(UserDto dto) {
        try {
            String userId = dto.getUserId();
            boolean isExistId = userRepository.existsByUserId(userId);
            if (isExistId) return ResponseDto.duplicateId();

            UserEntity user = UserEntity.builder()
                    .userId(userId)
                    .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                    .email(dto.getEmail())
                    .nickname(dto.getNickname())
                    .mbti(dto.getMbti())
                    .gender(dto.getGender())
                    .age(dto.getAge())
                    .role("USER")
                    .build();

            userRepository.save(user);

        } catch (Exception exception) {
            return ResponseDto.databaseError();
        }
        return ResponseDto.success();
    }

    @Override
    public ResponseEntity<?> emailCertification(EmailCertificationDto dto) {
        try {
            String userId = dto.getId();
            String email = dto.getEmail();

            boolean isExistId = userRepository.existsByUserId(userId);
            if (isExistId) return ResponseDto.duplicateId();

            String certificationNumber = CertificationNumber.getCertificationNumber();

            boolean isSuccessed = emailProvider.sendCertificationMail(email, certificationNumber);
            if (!isSuccessed) return ResponseDto.mailFail();

            CertificationEntity certificationEntity = new CertificationEntity(userId, email, certificationNumber);
            certificationRepository.save(certificationEntity);

        } catch (Exception exception) {
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();
    }

    @Override
    public ResponseEntity<?> checkCertification(EmailCertificationDto dto) {
        try {
            String userId = dto.getId();
            String email = dto.getEmail();
            String certificationNumber = dto.getCertificationNumber();

            CertificationEntity certificationEntity = certificationRepository.findByUserId(userId);
            if (certificationEntity == null) return ResponseDto.certificationFail();

            boolean isMatched = certificationEntity.getEmail().equals(email) && certificationEntity.getCertificationNumber().equals(certificationNumber);
            if (!isMatched) return ResponseDto.certificationFail();
        } catch (Exception exception) {
            return ResponseDto.databaseError();
        }
        return ResponseDto.success();
    }

    @Override
    public ResponseEntity<? super ResponseDto> findIdByEmail(String email) {
        try {
            UserEntity user = userRepository.findByEmail(email);
            if (user != null) {
                String userIdPart = user.getUserId().substring(0, 3) + "***";
                emailProvider.sendCertificationMail(email, "회원님의 아이디는 " + userIdPart + "입니다.");
                return ResponseDto.success();
            } else {
                return ResponseDto.notExistMail();
            }
        } catch (Exception e) {
            return ResponseDto.databaseError();
        }

    }


        @Override
        public ResponseEntity<?> sendPasswordResetCode(String userId, String email) {
            try {
                UserEntity user = userRepository.findByUserIdAndEmail(userId, email).orElse(null);
                if (user == null) {
                    return ResponseDto.notExistUser();
                }

                String certificationNumber = CertificationNumber.getCertificationNumber();
                boolean isSuccessed = emailProvider.sendCertificationMail(email, certificationNumber);
                if (!isSuccessed) return ResponseDto.mailFail();

                CertificationEntity certificationEntity = new CertificationEntity(userId, email, certificationNumber);
                certificationRepository.save(certificationEntity);

                return ResponseDto.success();
            } catch (Exception exception) {
                return ResponseDto.databaseError();
            }
        }

        @Override
        public ResponseEntity<?> updatePassword(String userId, String email, String newPassword) {
            try {
                UserEntity user = userRepository.findByUserIdAndEmail(userId, email).orElse(null);
                if (user == null) {
                    return ResponseDto.notExistUser();
                }

                user.setPassword(bCryptPasswordEncoder.encode(newPassword));
                userRepository.save(user);

                return ResponseDto.success();
            } catch (Exception exception) {
                return ResponseDto.databaseError();
            }
        }
    }

