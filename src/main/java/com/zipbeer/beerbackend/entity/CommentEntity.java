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
@Table(name = "comment_tbl")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long commentNo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity writer;

    @ManyToOne
    @JoinColumn(name = "board_no")
    private BoardEntity board;

    @Column(name = "content", length = 200)
    private String content;

    @Column
    @CreationTimestamp
    private LocalDateTime createDate;
}
