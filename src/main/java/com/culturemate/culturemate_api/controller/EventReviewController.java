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
@RequestMapping("/event-reviews")
@RequiredArgsConstructor
public class EventReviewController {
  private final EventReviewService eventReviewService;
  private final EventService eventService;
//  private final MemberService memberService;

  // 특정 이벤트의 리뷰 데이터 조회
  @GetMapping
  public ResponseEntity<?> get(@RequestParam Long eventId) {
    try {
      Event event = eventService.read(eventId);
      if (event == null) {
        return ResponseEntity.notFound().build();
      }
      List<EventReview> reviews = eventReviewService.readByEvent(event);
      return ResponseEntity.ok().body(reviews);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("리뷰 조회 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 특정 회원이 작성한 리뷰 데이터 조회
//  @GetMapping("/member")
//  public ResponseEntity<?> getByMember(@RequestParam Long memberId) {
//    try {
//      Member member = memberService.read(memberId);
//      if (member == null) {
//        return ResponseEntity.notFound().build();
//      }
//      List<EventReview> reviews = eventReviewService.readByMember(member);
//      return ResponseEntity.ok().body(reviews);
//    } catch (Exception e) {
//      return ResponseEntity.badRequest().body("회원 리뷰 조회 중 오류가 발생했습니다: " + e.getMessage());
//    }
//  }

  // 이벤트 리뷰 등록
  @PostMapping
  public ResponseEntity<?> create(@RequestParam Long eventId, @RequestBody EventReview review) {
    if (review == null) {
      return ResponseEntity.badRequest().body("리뷰 정보가 필요합니다.");
    }
    if (review.getContent() == null || review.getContent().trim().isEmpty()) {
      return ResponseEntity.badRequest().body("리뷰 내용은 필수입니다.");
    }
    try {
      Event event = eventService.read(eventId);
      if (event == null) {
        return ResponseEntity.notFound().build();
      }
      review.setEvent(event);
      EventReview createdReview = eventReviewService.create(review);
      return ResponseEntity.ok().body(createdReview);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("리뷰 생성 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 이벤트 리뷰 ID로 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    try {
      eventReviewService.delete(id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}