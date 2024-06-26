package com.zipbeer.beerbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "board_tbl")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardNo;

    @Column(length = 100)
    private String title;

    @Column(length = 500)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity writer;

    @Column
    @CreationTimestamp
    private LocalDate regDate;

    @Column
    @UpdateTimestamp
    private LocalDate modifyDate;

    @Column
    private int count;

    @OneToMany(mappedBy = "board", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentEntity> commentEntityList;
}
