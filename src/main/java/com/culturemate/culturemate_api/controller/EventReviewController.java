package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.event.EventReview;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.dto.ReviewDto;
import com.culturemate.culturemate_api.service.EventReviewService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/event-reviews")
@RequiredArgsConstructor
public class EventReviewController {
  private final EventReviewService eventReviewService;

  // 특정 이벤트의 리뷰 데이터 조회
  @GetMapping("/{eventId}")
  public ResponseEntity<List<ReviewDto.Response>> getEventReviews(@RequestParam Long eventId) {
    return ResponseEntity.ok(
      eventReviewService.findByEventId(eventId).stream()
        .map(ReviewDto.Response::from)
        .collect(Collectors.toList())
    );
  }

  // 이벤트 리뷰 등록
  @PostMapping
  public ResponseEntity<ReviewDto.Response> createEventReview(
      @Valid @RequestBody ReviewDto.Request reviewDto,
      @AuthenticationPrincipal AuthenticatedUser requester) {
    EventReview createdReview = eventReviewService.create(reviewDto, requester.getMemberId());
    return ResponseEntity.status(201).body(ReviewDto.Response.from(createdReview));
  }

  // 이벤트 리뷰 수정
  @PutMapping("/{id}")
  public ResponseEntity<ReviewDto.Response> updateEventReview(
      @PathVariable Long id, 
      @Valid @RequestBody ReviewDto.Request reviewDto,
      @AuthenticationPrincipal AuthenticatedUser requester) {
    EventReview updatedEventReview = eventReviewService.update(id, reviewDto, requester.getMemberId());
    return ResponseEntity.ok(ReviewDto.Response.from(updatedEventReview));
  }

  // 이벤트 리뷰 ID로 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEventReview(
      @PathVariable Long id,
      @AuthenticationPrincipal AuthenticatedUser requester) {
    eventReviewService.delete(id, requester.getMemberId());
    return ResponseEntity.noContent().build();
  }
}