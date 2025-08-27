package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventReview;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventReviewRepository {

  private final EntityManager em;

  public void save(EventReview eventReview) {
    em.persist(eventReview);
    // JPQL 쿼리 실행 전 즉시 DB 반영 필요 
    // (EventReviewService에서 평균별점 계산 시 새 리뷰가 포함되어야 함)
    em.flush();
  }

  public Optional<EventReview> findById(Long id) {
    return Optional.ofNullable(em.find(EventReview.class, id));
  }

  public List<EventReview> findAll() {
    return em.createQuery("select er from EventReview er", EventReview.class).getResultList();
  }

  public List<EventReview> findByEvent(Event event) {
    return em.createQuery(
        "select er from EventReview er where er.event = :event", EventReview.class)
        .setParameter("event", event)
        .getResultList();
  }


  public void delete(EventReview eventReview) {
    em.remove(eventReview);
  }

  public void deleteById(Long id) {
    Optional<EventReview> eventReview = findById(id);
    if (eventReview.isPresent()) {
      em.remove(eventReview.get());
    }
  }

}