package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Together;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TogetherRepository extends JpaRepository<Together, Long> {

  @EntityGraph(attributePaths = {"event", "host", "region"})
  List<Together> findByHost(Member host);
  
  @EntityGraph(attributePaths = {"event", "host", "region"})
  List<Together> findByEvent(Event event);
  
  @EntityGraph(attributePaths = {"event", "host", "region"})
  @Query("SELECT t FROM Together t " +
         "JOIN t.participants p " +
         "WHERE p.participant = :member " +
         "ORDER BY p.id DESC")
  List<Together> findByParticipant(@Param("member") Member participant);

//  통합 검색
  @EntityGraph(attributePaths = {"event", "host", "region"})
  @Query("SELECT t FROM Together t " +
         "JOIN t.event e " +
         "WHERE (:keyword IS NULL OR :keyword = '' OR t.title LIKE %:keyword%) AND " +
         "      (:regions IS NULL OR t.region IN :regions) AND " +
         "      (:startDate IS NULL OR t.meetingDate >= :startDate) AND " +
         "      (:endDate IS NULL OR t.meetingDate <= :endDate) AND " +
         "      (:eventType IS NULL OR e.eventType = :eventType) AND " +
         "      (:eventId IS NULL OR e.id = :eventId) AND " +
         "      (:isRecruiting IS NULL OR t.isRecruiting = :isRecruiting)")
  List<Together> findBySearch(@Param("keyword") String keyword,
                              @Param("regions") List<Region> regions,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("eventType") EventType eventType,
                              @Param("eventId") Long eventId,
                              @Param("isRecruiting") Boolean isRecruiting);

  @EntityGraph(attributePaths = {"event", "host", "region"})
  List<Together> findByIsRecruiting(boolean isRecruiting);

  // 이미지 경로만 조회 (삭제 시 사용)
  @Query("SELECT t.thumbnailImagePath, t.mainImagePath FROM Together t WHERE t.id = :id")
  Object[] findImagePathsById(@Param("id") Long id);
}
