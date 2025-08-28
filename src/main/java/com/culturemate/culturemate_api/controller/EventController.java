package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventReview;
import com.culturemate.culturemate_api.dto.EventSearchFilter;
import com.culturemate.culturemate_api.service.EventService;
import com.culturemate.culturemate_api.service.EventReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
  private final EventService eventService;
  private final EventReviewService eventReviewService;

  // 이벤트 전체 데이터 조회하기
  @GetMapping
  public ResponseEntity<List<Event>> getAllEvents() {
    return ResponseEntity.ok(eventService.readAll());
  }

  // 이벤트 ID로 데이터 조회
  @GetMapping("/{id}")
  public ResponseEntity<?> getEventById(@PathVariable Long id) {
    try {
      Event event = findEventById(id);
      return ResponseEntity.ok().body(event);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // 제목으로 이벤트 검색
  @GetMapping("/search")
  public ResponseEntity<?> searchEventsByTitle(@RequestParam String title) {
    if (title == null || title.trim().isEmpty()) {
      return ResponseEntity.badRequest().body("검색할 제목을 입력해주세요.");
    }
    try {
      List<Event> events = eventService.readByTitle(title);
      return ResponseEntity.ok().body(events);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("제목 검색 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 지역으로 이벤트 검색
  @GetMapping("/region")
  public ResponseEntity<?> getEventsByRegion(@RequestParam String level1,
                                             @RequestParam(required = false) String level2,
                                             @RequestParam(required = false) String level3) {
    if (level1 == null || level1.trim().isEmpty()) {
      return ResponseEntity.badRequest().body("지역 정보(level1)는 필수입니다.");
    }
    try {
      Region region = createRegion(level1, level2, level3);
      List<Event> events = eventService.readByRegion(region);
      return ResponseEntity.ok().body(events);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("지역 검색 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 복합 필터로 이벤트 검색
  @GetMapping("/filter")
  public ResponseEntity<?> searchEventsByFilter(
      @RequestParam(required = false) String level1,
      @RequestParam(required = false) String level2,
      @RequestParam(required = false) String level3,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate,
      @RequestParam(required = false) String eventType
  ) {
    // 날짜 문자열을 LocalDate로 변환
    LocalDate parsedStartDate = null;
    LocalDate parsedEndDate = null;
    
    try {
      if (startDate != null && !startDate.trim().isEmpty()) {
        parsedStartDate = LocalDate.parse(startDate);
      }
      if (endDate != null && !endDate.trim().isEmpty()) {
        parsedEndDate = LocalDate.parse(endDate);
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("날짜 형식이 올바르지 않습니다. (YYYY-MM-DD 형식을 사용해주세요)");
    }
    
    // EventSearchFilter 객체 생성
    EventSearchFilter filter = EventSearchFilter.builder()
        .level1(level1)
        .level2(level2)
        .level3(level3)
        .startDate(parsedStartDate)
        .endDate(parsedEndDate)
        .eventType(eventType)
        .build();
    
    try {
      List<Event> events = eventService.readByFilters(filter);
      return ResponseEntity.ok().body(events);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("필터 검색 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 이벤트 등록
  @PostMapping
  public ResponseEntity<?> createEvent(@RequestBody Event event) {
    if (event == null) {
      return ResponseEntity.badRequest().body("이벤트 정보가 필요합니다.");
    }
    if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
      return ResponseEntity.badRequest().body("이벤트 제목은 필수입니다.");
    }
    try {
      Event createdEvent = eventService.create(event);
      return ResponseEntity.ok().body(createdEvent);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("이벤트 생성 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 이벤트 리뷰 데이터 조회
  @GetMapping("/{id}/reviews")
  public ResponseEntity<?> getEventReviews(@PathVariable Long id) {
    try {
      Event event = findEventById(id);
      List<EventReview> reviews = eventReviewService.readByEvent(event);
      return ResponseEntity.ok().body(reviews);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("리뷰 조회 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 이벤트 리뷰 등록
  @PostMapping("/{id}/reviews")
  public ResponseEntity<?> addEventReview(@PathVariable Long id, @RequestBody EventReview review) {
    if (review == null) {
      return ResponseEntity.badRequest().body("리뷰 정보가 필요합니다.");
    }
    if (review.getContent() == null || review.getContent().trim().isEmpty()) {
      return ResponseEntity.badRequest().body("리뷰 내용은 필수입니다.");
    }
    try {
      Event event = findEventById(id);
      review.setEvent(event);
      EventReview createdReview = eventReviewService.create(review);
      return ResponseEntity.ok().body(createdReview);
    } catch (RuntimeException e) {
      if (e.getMessage().contains("not found")) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.badRequest().body("리뷰 생성 중 오류가 발생했습니다: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("리뷰 생성 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 이벤트 리뷰 ID로 삭제
  @DeleteMapping("/reviews/{id}")
  public ResponseEntity<?> deleteReview(@PathVariable Long id) {
    try {
      eventReviewService.delete(id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  // Private helper methods
  private Event findEventById(Long id) {
    Event event = eventService.read(id);
    if (event == null) {
      throw new RuntimeException("Event not found: " + id);
    }
    return event;
  }

  private Region createRegion(String level1, String level2, String level3) {
    Region region = new Region();
    region.setLevel1(level1);
    region.setLevel2(level2 != null ? level2 : "");
    region.setLevel3(level3 != null ? level3 : "");
    return region;
  }
}