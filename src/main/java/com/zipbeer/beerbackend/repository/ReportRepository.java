package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.ReportEntity;
import com.zipbeer.beerbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    boolean existsByReporterAndReportedUser(UserEntity reporter, UserEntity reportedUser);
}
