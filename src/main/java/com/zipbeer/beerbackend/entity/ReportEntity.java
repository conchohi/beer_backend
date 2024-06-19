package com.zipbeer.beerbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    @JoinColumn(name = "reporter_id", referencedColumnName = "userId")
    private UserEntity reporter;

    @ManyToOne
    @JoinColumn(name = "reported_user_id", referencedColumnName = "userId")
    private UserEntity reportedUser;

    @Column
    private String reason;

    @Column
    private String title;

    @Column
    private String content;

    @CreationTimestamp
    private LocalDateTime reportDate;

    @Column
    private boolean isProcess;
}
