package com.zipbeer.beerbackend.repository;

import com.zipbeer.beerbackend.entity.RoomEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    boolean existsByRoomNo(Long roomNo);

    //비밀번호 방에 비밀번호 확인
    boolean existsByRoomNoAndRoomPw(Long roomNo, String roomPw);

    //해당 방에 내가 나가면 빈 방인지 확인
    @Query("SELECT CASE WHEN COUNT(p) = 1 THEN true ELSE false END FROM RoomEntity r LEFT JOIN r.participantList p WHERE r.roomNo = :roomNo")
    boolean isEmptyRoomWhenExit(@Param("roomNo") Long roomNo);

    @Query("select r from RoomEntity r where (:category is null or r.category like concat('%',:category,'%')) and (:searchTerm is null or r.title like concat('%',:searchTerm,'%'))")
    Page<RoomEntity> findAllByTitle(Pageable pageable, @Param("searchTerm") String searchTerm, @Param("category") String category);

    @Query("select r from RoomEntity r  where (:category is null or r.category like concat('%',:category,'%')) and (:searchTerm is null or r.master like concat('%',:searchTerm,'%'))")
    Page<RoomEntity> findAllByNickname(Pageable pageable, @Param("searchTerm") String searchTerm, @Param("category") String category);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RoomEntity> findByRoomNo(Long roomNo);

}
