package com.zipbeer.beerbackend.service;

import com.zipbeer.beerbackend.dto.ReportDto;
import com.zipbeer.beerbackend.dto.response.ResponseDto;
import com.zipbeer.beerbackend.entity.ReportEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import com.zipbeer.beerbackend.repository.ReportRepository;
import com.zipbeer.beerbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    public ResponseEntity<?> reportUser(ReportDto reportDto){
        try{
            //신고자
            UserEntity reporter = userRepository.findById(reportDto.getReporterUser()).orElseThrow();
            //신고받은자
            UserEntity reported = userRepository.findById(reportDto.getReportedUser()).orElseThrow();

            if(reportRepository.existsByReporterAndReportedUser(reporter,reported)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("동일한 회원을 중복 신고할 수 없습니다.");
            }
            ReportEntity report = ReportEntity.builder()
                    .reporter(reporter)
                    .reportedUser(reported)
                    .title(reportDto.getTitle())
                    .reason(reportDto.getReason())
                    .content(reportDto.getContent())
                    .isProcess(false)
                    .build();
            reportRepository.save(report);
        } catch (Exception e){
            return ResponseDto.databaseError();
        }
        return ResponseDto.success();
    }
}
