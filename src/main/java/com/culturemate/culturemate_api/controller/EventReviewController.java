package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventReview;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.service.EventService;
import com.culturemate.culturemate_api.service.EventReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event-reviews")
@RequiredArgsConstructor
public class EventReviewController {
  private final EventReviewService eventReviewService;
  private final EventService eventService;
//  private final MemberService memberService;

  // 특정 이벤트의 리뷰 데이터 조회
  @GetMapping
  public ResponseEntity<List<EventReview>> get(@RequestParam Long eventId) {
    Event event = eventService.findById(eventId);
    List<EventReview> reviews = eventReviewService.findByEvent(event);
    return ResponseEntity.ok(reviews);
  }

  // 이벤트 리뷰 등록
  @PostMapping
  public ResponseEntity<EventReview> add(@RequestParam Long eventId,
                                         @RequestBody EventReview review) {
    Event event = eventService.findById(eventId);
    review.setEvent(event);
    EventReview createdReview = eventReviewService.create(review);
    return ResponseEntity.status(201).body(createdReview);
  }

  // 이벤트 리뷰 ID로 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> remove(@PathVariable Long id) {
    eventReviewService.delete(id);
    return ResponseEntity.noContent().build();
  }
}