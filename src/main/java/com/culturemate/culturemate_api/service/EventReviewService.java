package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventReview;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.repository.EventRepository;
import com.culturemate.culturemate_api.repository.EventReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventReviewService {

  private final EventReviewRepository reviewRepository;
  private final EventRepository eventRepository;

  @Transactional
  public EventReview create(EventReview eventReview) {
    // 리뷰 저장 후 즉시 DB 반영 (평균별점 계산 시 새 리뷰가 포함되어야 함)
    EventReview savedReview = reviewRepository.saveAndFlush(eventReview);
    updateEventAverageRating(eventReview.getEvent().getId());
    return savedReview;
  }

  public EventReview read(Long reviewId) {
    return reviewRepository.findById(reviewId).orElse(null);
  }

  @Transactional
  public List<EventReview> readAll() {
    return reviewRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<EventReview> readByEvent(Event event) {
    return reviewRepository.findByEvent(event);
  }

  public List<EventReview> readByMember(Member member) {
    return reviewRepository.findByMember(member);
  }

  public void update(Long reviewId, EventReview updatedReview) {
    EventReview existingReview = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
    
    existingReview.setRating(updatedReview.getRating());
    existingReview.setContent(updatedReview.getContent());
    
    updateEventAverageRating(existingReview.getEvent().getId());
  }

  @Transactional
  public void delete(Long reviewId) {
    EventReview eventReview = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
    Long eventId = eventReview.getEvent().getId();

    reviewRepository.delete(eventReview);
    updateEventAverageRating(eventId);
  }

  // 리뷰 등록/삭제시 해당 이벤트 별점 업데이트
  private void updateEventAverageRating(Long eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다."));
    
    event.recalculateAvgRating();
  }
}