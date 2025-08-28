package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Event 엔티티 Repository
 * 
 * [Spring Data JPA 자동 제공 메서드들]
 * - save(Event entity) : 엔티티 저장/수정
 * - saveAll(Iterable<Event> entities) : 여러 엔티티 일괄 저장
 * - findById(Long id) : ID로 단건 조회 (Optional 반환)
 * - findAll() : 전체 조회
 * - findAll(Pageable pageable) : 페이징 조회
 * - findAll(Sort sort) : 정렬 조회
 * - count() : 전체 개수
 * - existsById(Long id) : ID 존재 여부 확인
 * - delete(Event entity) : 엔티티 삭제
 * - deleteById(Long id) : ID로 삭제
 * - deleteAll() : 전체 삭제
 * 
 * [커스텀 쿼리 메서드 작성법]
 * - 메서드명으로 자동 쿼리 생성: findBy필드명, existsBy필드명 등
 * - 예시: findByTitle(String title) → SELECT * FROM event WHERE title = ?
 * - 복합조건: findByTitleAndCategory(String title, String category)
 * - LIKE 검색: findByTitleContaining(String keyword)
 * - 정렬: findByTitleOrderByCreatedAtDesc(String title)
 * - 페이징: findByTitle(String title, Pageable pageable)
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

  List<Event> findByTitleContaining(String title);
  
  /**
   * RegionRepository의 findRegionsByCondition()으로 찾은 지역들과 일치하는 이벤트 검색
   */
  @Query("SELECT e FROM Event e WHERE e.region IN :regions")
  List<Event> findByRegion(@Param("regions") List<Region> regions);

  List<Event> findByEventType(EventType eventType);

  /**
   * 검색 기간과 겹치는 이벤트 조회 (startDate, endDate 필드 기반)
   * 조건: 이벤트 시작일 <= 검색 종료일 AND 이벤트 종료일 >= 검색 시작일
   */
  @Query("SELECT e FROM Event e WHERE e.startDate <= :endDate AND e.endDate >= :startDate")
  List<Event> findByPeriodBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

  /**
   * 복합 조건으로 이벤트 검색
   * - regions: null이면 지역 조건 무시
   * - startDate: null이면 시작일 조건 무시  
   * - endDate: null이면 종료일 조건 무시
   * - eventType: null이면 타입 조건 무시
   */
  @Query("SELECT e FROM Event e WHERE " +
         "(:regions IS NULL OR e.region IN :regions) AND " +
         "(:startDate IS NULL OR e.endDate >= :startDate) AND " +
         "(:endDate IS NULL OR e.startDate <= :endDate) AND " +
         "(:eventType IS NULL OR e.eventType = :eventType)")
  List<Event> findByFilters(@Param("regions") List<Region> regions,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate,
                           @Param("eventType") EventType eventType);
}
