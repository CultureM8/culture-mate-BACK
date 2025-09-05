package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Together;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  // 지역 조건 없는 검색 (기존 쿼리에서 region 조건만 제거)
  @EntityGraph(attributePaths = {"event", "host"})
  @Query("SELECT t FROM Together t " +
    "JOIN t.event e " +
    "WHERE (:keyword IS NULL OR :keyword = '' OR t.title LIKE %:keyword%) AND " +
    "      (:startDate IS NULL OR t.meetingDate >= :startDate) AND " +
    "      (:endDate IS NULL OR t.meetingDate <= :endDate) AND " +
    "      (:eventType IS NULL OR e.eventType = :eventType) AND " +
    "      (:eventId IS NULL OR e.id = :eventId) AND " +
    "      (:isRecruiting IS NULL OR t.isRecruiting = :isRecruiting)")
  List<Together> findBySearchWithoutRegion(@Param("keyword") String keyword,
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

  // 원자적 참여자 수 카운트 업데이트 (단순)
  @Modifying
  @Query("UPDATE Together t SET t.participantCount = t.participantCount + :increment WHERE t.id = :togetherId")
  void updateParticipantCount(@Param("togetherId") Long togetherId, @Param("increment") int increment);

  // 원자적 참여자 수 업데이트 + 모집 상태 자동 변경 (참여 시)
  @Modifying
  @Query("UPDATE Together t SET t.participantCount = t.participantCount + 1, " +
         "t.isRecruiting = CASE WHEN (t.participantCount + 1) >= t.maxParticipants THEN false ELSE t.isRecruiting END " +
         "WHERE t.id = :togetherId")
  void joinParticipantAndUpdateStatus(@Param("togetherId") Long togetherId);

  // 원자적 참여자 수 업데이트 + 모집 상태 자동 변경 (탈퇴 시)
  @Modifying
  @Query("UPDATE Together t SET t.participantCount = t.participantCount - 1, " +
         "t.isRecruiting = CASE WHEN (t.participantCount - 1) < t.maxParticipants THEN true ELSE t.isRecruiting END " +
         "WHERE t.id = :togetherId")
  void leaveParticipantAndUpdateStatus(@Param("togetherId") Long togetherId);

  // 원자적 관심수 카운트 업데이트
  @Modifying
  @Query("UPDATE Together t SET t.interestCount = t.interestCount + :increment WHERE t.id = :togetherId")
  void updateInterestCount(@Param("togetherId") Long togetherId, @Param("increment") int increment);
}
