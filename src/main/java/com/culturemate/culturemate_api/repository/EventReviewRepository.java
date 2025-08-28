package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * EventReview 엔티티 Repository
 * 
 * [Spring Data JPA 자동 제공 메서드들]
 * - save(EventReview entity) : 엔티티 저장/수정
 * - saveAll(Iterable<EventReview> entities) : 여러 엔티티 일괄 저장
 * - findById(Long id) : ID로 단건 조회 (Optional 반환)
 * - findAll() : 전체 조회
 * - findAll(Pageable pageable) : 페이징 조회
 * - findAll(Sort sort) : 정렬 조회
 * - count() : 전체 개수
 * - existsById(Long id) : ID 존재 여부 확인
 * - delete(EventReview entity) : 엔티티 삭제
 * - deleteById(Long id) : ID로 삭제
 * - deleteAll() : 전체 삭제
 * - saveAndFlush(EventReview entity) : 저장 후 즉시 DB 반영 (EventReviewService에서 평균별점 계산 시 새 리뷰가 포함되어야 할 때 사용)
 * 
 * [커스텀 쿼리 메서드 작성법]
 * - 메서드명으로 자동 쿼리 생성: findBy필드명, existsBy필드명 등
 * - 예시: findByRating(Integer rating) → SELECT * FROM event_review WHERE rating = ?
 * - 복합조건: findByEventAndRating(Event event, Integer rating)
 * - 정렬: findByEventOrderByCreatedAtDesc(Event event)
 * - 페이징: findByEvent(Event event, Pageable pageable)
 */
@Repository
public interface EventReviewRepository extends JpaRepository<EventReview, Long> {

  List<EventReview> findByEvent(Event event);
  
  List<EventReview> findByEventOrderByCreatedAtDesc(Event event);

}