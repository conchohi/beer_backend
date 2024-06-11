package com.zipbeer.beerbackend.controller;

import com.zipbeer.beerbackend.dto.ReportDto;
import com.zipbeer.beerbackend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {
    private final ReportService reportService;
    @PostMapping("")
    public ResponseEntity<?> report(@RequestBody ReportDto reportDto){
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        reportDto.setReporterUser(id);
        return reportService.reportUser(reportDto);
    }
}
