package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Together;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Together 엔티티 Repository
 * 
 * [Spring Data JPA 자동 제공 메서드들]
 * - save(Together entity) : 엔티티 저장/수정
 * - saveAll(Iterable<Together> entities) : 여러 엔티티 일괄 저장
 * - findById(Long id) : ID로 단건 조회 (Optional 반환)
 * - findAll() : 전체 조회
 * - findAll(Pageable pageable) : 페이징 조회
 * - findAll(Sort sort) : 정렬 조회
 * - count() : 전체 개수
 * - existsById(Long id) : ID 존재 여부 확인
 * - delete(Together entity) : 엔티티 삭제
 * - deleteById(Long id) : ID로 삭제
 * - deleteAll() : 전체 삭제
 * 
 * [커스텀 쿼리 메서드 작성법]
 * - 메서드명으로 자동 쿼리 생성: findBy필드명, existsBy필드명 등
 * - 예시: findByTitle(String title) → SELECT * FROM together WHERE title = ?
 * - 복합조건: findByTitleAndHost(String title, Member host)
 * - LIKE 검색: findByTitleContaining(String keyword)
 * - 정렬: findByTitleOrderByCreatedAtDesc(String title)
 * - 페이징: findByTitle(String title, Pageable pageable)
 */
@Repository
public interface TogetherRepository extends JpaRepository<Together, Long> {

  List<Together> findByTitleContaining(String title);
  
  /**
   * RegionRepository의 findRegionsByCondition()으로 찾은 지역들과 일치하는 Together 검색
   */
  @Query("SELECT t FROM Together t WHERE t.region IN :regions")
  List<Together> findByRegion(@Param("regions") List<Region> regions);
  
  List<Together> findByHost(Member host);
  
  List<Together> findByEvent(Event event);
  
  @Query("SELECT t FROM Together t " +
         "JOIN t.participants p " +
         "WHERE p.participant = :member " +
         "ORDER BY p.id DESC")
  List<Together> findByParticipant(@Param("member") Member participant);

  /**
   * 통합 검색: 제목 + 복합 조건으로 이벤트 검색
   * - keyword: null이면 제목 조건 무시, 값이 있으면 LIKE 검색
   * - regions: null이면 지역 조건 무시
   * - startDate: null이면 시작일 조건 무시
   * - endDate: null이면 종료일 조건 무시
   * - eventType: null이면 이벤트 타입 조건 무시
   * - eventId: null이면 특정 이벤트 조건 무시
   */
  @Query("SELECT t FROM Together t " +
         "JOIN t.event e " +
         "WHERE (:keyword IS NULL OR :keyword = '' OR t.title LIKE %:keyword%) AND " +
         "      (:regions IS NULL OR t.region IN :regions) AND " +
         "      (:startDate IS NULL OR t.meetingDate >= :startDate) AND " +
         "      (:endDate IS NULL OR t.meetingDate <= :endDate) AND " +
         "      (:eventType IS NULL OR e.eventType = :eventType) AND " +
         "      (:eventId IS NULL OR e.id = :eventId)")
  List<Together> findBySearch(@Param("keyword") String keyword,
                              @Param("regions") List<Region> regions,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("eventType") EventType eventType,
                              @Param("eventId") Long eventId);

  List<Together> findByIsRecruiting(boolean isRecruiting);
}
