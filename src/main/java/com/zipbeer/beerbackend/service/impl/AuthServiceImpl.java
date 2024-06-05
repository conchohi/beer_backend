package com.zipbeer.beerbackend.service.impl;

import com.beer_back.dto.request.auth.EmailCertificationRequestDto;
import com.beer_back.dto.request.auth.IdCheckRequestDto;
import com.zipbeer.beerbackend.common.CertificationNumber;
import com.zipbeer.beerbackend.dto.UserDto;
import com.zipbeer.beerbackend.dto.request.auth.CheckCertificationRequestDto;
import com.zipbeer.beerbackend.dto.response.ResponseDto;
import com.zipbeer.beerbackend.dto.response.auth.CheckCertificationResponseDto;
import com.zipbeer.beerbackend.dto.response.auth.EmailCertificationResponseDto;
import com.zipbeer.beerbackend.dto.response.auth.IdCheckResponseDto;
import com.zipbeer.beerbackend.entity.CertificationEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.provider.EmailProvider;
import com.zipbeer.beerbackend.repository.CertificationRepository;
import com.zipbeer.beerbackend.repository.UserRepository;
import com.zipbeer.beerbackend.service.AuthService;
import com.zipbeer.beerbackend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final FileUtil fileUtil;
    @Override
    public ResponseEntity<? super ResponseDto> idCheck(IdCheckRequestDto dto) {
        try {
            String userId = dto.getId();
            System.out.println("Checking userId: " + userId); // 로그 추가
            boolean isExistId = userRepository.existsByUserId(userId);
            System.out.println("Is userId exist: " + isExistId); // 로그 추가

            if (isExistId) {
                return ResponseEntity.ok(IdCheckResponseDto.duplicateId());
            } else {
                return ResponseEntity.ok(IdCheckResponseDto.success());
            }
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.databaseError());
        }
    }

    public ResponseEntity<? super ResponseDto> join(UserDto dto) {
        try {
            String userId = dto.getUsername();
            boolean isExistId = userRepository.existsByUserId(userId);
            if (isExistId) return IdCheckResponseDto.duplicateId();

            String profileImagePath = null;
            if (dto.getProfileFile() != null && !dto.getProfileFile().isEmpty()) {
                profileImagePath = fileUtil.saveFile(dto.getProfileFile());
            }

            UserEntity user = UserEntity.builder()
                    .userId(userId)
                    .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                    .email(dto.getEmail())
                    .nickname(dto.getNickname())
                    .profileImage(profileImagePath)
                    .role("USER")
                    .build();
            userRepository.save(user);

        } catch (Exception exception) {
            return ResponseDto.databaseError();
        }
        return ResponseEntity.ok(new ResponseDto());
    }
    @Override
    public ResponseEntity<? super EmailCertificationResponseDto> emailCertification(EmailCertificationRequestDto dto) {
        try {
            String userId = dto.getId();
            String email = dto.getEmail();

            boolean isExistId = userRepository.existsByUserId(userId);
            if (isExistId) return EmailCertificationResponseDto.duplicateId();

            String certificationNumber = CertificationNumber.getCertificationNumber();

            boolean isSuccessed = emailProvider.sendCertificationMail(email, certificationNumber);
            if (!isSuccessed) return EmailCertificationResponseDto.mailSendFail();

            CertificationEntity certificationEntity = new CertificationEntity(userId, email, certificationNumber);
            certificationRepository.save(certificationEntity);

        } catch (Exception exception) {
            return ResponseDto.databaseError();
        }

        return EmailCertificationResponseDto.success();
    }

    @Override
    public ResponseEntity<? super CheckCertificationResponseDto> checkCertification(CheckCertificationRequestDto dto) {
        try {
            String userId = dto.getId();
            String email = dto.getEmail();
            String certificationNumber = dto.getCertificationNumber();

            CertificationEntity certificationEntity = certificationRepository.findByUserId(userId);
            if (certificationEntity == null) return CheckCertificationResponseDto.certificationFail();

            boolean isMatched = certificationEntity.getEmail().equals(email) && certificationEntity.getCertificationNumber().equals(certificationNumber);
            if (!isMatched) return CheckCertificationResponseDto.certificationFail();
        } catch (Exception exception) {
            return ResponseDto.databaseError();
        }
        return CheckCertificationResponseDto.success();
    }

    @Override
    public ResponseEntity<? super ResponseDto> findIdByEmail(String email) {
        try {
            UserEntity user = userRepository.findByEmail(email);
            if (user != null) {
                String userIdPart = user.getUserId().substring(0, 3) + "***";
                emailProvider.sendCertificationMail(email, "회원님의 아이디는 " + userIdPart + "입니다.");
                return ResponseEntity.ok(new ResponseDto());
            } else {
                return ResponseEntity.badRequest().body(new ResponseDto("해당 이메일로 등록된 아이디가 없습니다.", email));
            }
        } catch (Exception e) {
            return ResponseDto.databaseError();
        }
    }
}
