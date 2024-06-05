package com.zipbeer.beerbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="report_tbl")
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long reportNo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity Reporter;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity ReportedUser;

    @Column
    private String reason;

    @Column
    private String title;

    @Column
    private String content;

    @CreatedDate
    private LocalDateTime reportDate;

    @Column
    private boolean isProcess;
}
