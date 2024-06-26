package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByBoardBoardNo(Long boardNo);


}
