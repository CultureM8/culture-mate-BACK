package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.event.EventReview;
import com.culturemate.culturemate_api.dto.EventReviewRequestDto;
import com.culturemate.culturemate_api.dto.EventReviewResponseDto;
import com.culturemate.culturemate_api.service.EventReviewService;
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
  @GetMapping
  public ResponseEntity<List<EventReviewResponseDto>> get(@RequestParam Long eventId) {
    return ResponseEntity.ok(
      eventReviewService.findByEventId(eventId).stream()
        .map(EventReviewResponseDto::from)
        .collect(Collectors.toList())
    );
  }

  // 이벤트 리뷰 등록
  @PostMapping
  public ResponseEntity<EventReviewResponseDto> add(@Valid @RequestBody EventReviewRequestDto reviewDto) {
    EventReview createdReview = eventReviewService.create(reviewDto);
    return ResponseEntity.status(201).body(EventReviewResponseDto.from(createdReview));
  }

  // 이벤트 리뷰 수정
  @PutMapping("/{id}")
  public ResponseEntity<EventReviewResponseDto> update(@PathVariable Long id, @Valid @RequestBody EventReviewRequestDto reviewDto) {
    EventReview updatedEventReview = eventReviewService.update(id, reviewDto);
    return ResponseEntity.ok(EventReviewResponseDto.from(updatedEventReview));
  }

  // 이벤트 리뷰 ID로 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> remove(@PathVariable Long id) {
    eventReviewService.delete(id);
    return ResponseEntity.noContent().build();
  }
}