package com.culturemate.culturemate_api.contoller;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventReview;
import com.culturemate.culturemate_api.service.EventReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/EventReview")
public class EventReviewController {
  private final EventReviewService eventReviewService;

  public EventReviewController(EventReviewService eventReviewService) {
    this.eventReviewService = eventReviewService;
  }

  //이벤트 리뷰

  // 이벤트 리뷰 데이터 조회(이벤트 먼저 찾고 그 이벤트의 리뷰를 따로 찾아야 한다)
//  @GetMapping("/{id}/reviews")
//  public List<EventReview> getEventReviews(@PathVariable Long id) {
//    Event event = eventReviewService.findById(id)
//      .orElseThrow(() -> new RuntimeException("Event not found: " + id));
//    return eventReviewRepository.findByEvent(event);
//  }

  // 이벤트 리뷰 등록
//  @PostMapping("/{id}/reviews")
//  public EventReview addEventReview(@PathVariable Long id, @RequestBody EventReview review) {
//    Event event = eventRepository.findById(id)
//      .orElseThrow(() -> new RuntimeException("Event not found: " + id));
//    review.setEvent(event);
//    eventReviewRepository.save(review);
//    return review;
//  }

  // 이벤트 리뷰 ID로 삭제
  @DeleteMapping("/reviews/{id}")
  public String deleteReview(@PathVariable Long id) {
    eventReviewService.delete(id);
    return "EventReview deleted: " + id;
  }

//  // 엔티티 직접 전달해서 삭제 (필요시)
//  @DeleteMapping("/reviews")
//  public String deleteReviewEntity(@RequestBody EventReview review) {
//    eventReviewRepository.delete(review);
//    return "EventReview deleted";
//  }
}
