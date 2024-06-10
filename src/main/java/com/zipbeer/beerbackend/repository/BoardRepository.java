package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity,Long> {

}
