package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventReview;
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

  private final EventReviewRepository eventReviewRepository;
  private final EventRepository eventRepository;

  @Transactional
  public void create(EventReview eventReview) {
    // 리뷰 저장 후 즉시 DB 반영 (평균별점 계산 시 새 리뷰가 포함되어야 함)
    eventReviewRepository.saveAndFlush(eventReview);
    updateEventAverageRating(eventReview.getEvent().getId());
  }

  @Transactional
  public List<EventReview> readAll() {
    return eventReviewRepository.findAll();
  }

  @Transactional
  public void update(Long reviewId, EventReview updatedReview) {
    EventReview existingReview = eventReviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
    
    existingReview.setRating(updatedReview.getRating());
    existingReview.setContent(updatedReview.getContent());
    
    updateEventAverageRating(existingReview.getEvent().getId());
  }

  @Transactional
  public void delete(Long reviewId) {
    EventReview eventReview = eventReviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
    
    Long eventId = eventReview.getEvent().getId();
    eventReviewRepository.delete(eventReview);
    updateEventAverageRating(eventId);
  }


  private void updateEventAverageRating(Long eventId) {
    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다."));
    
    event.recalculateAvgRating();
  }
}