package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.dto.EventSearchFilter;
import com.culturemate.culturemate_api.service.EventService;
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

  // 이벤트 전체 데이터 조회하기
  @GetMapping
  public ResponseEntity<List<Event>> getAll() {
    return ResponseEntity.ok(eventService.readAll());
  }

  // 이벤트 ID로 데이터 조회
  @GetMapping("/{id}")
  public ResponseEntity<?> get(@PathVariable Long id) {
    try {
      Event event = eventService.read(id);
      if (event == null) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok().body(event);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("이벤트 조회 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 제목으로 이벤트 검색
  @GetMapping("/search")
  public ResponseEntity<?> getByTitle(@RequestParam String title) {
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
  public ResponseEntity<?> getByRegion(@RequestParam String level1,
                                             @RequestParam(required = false) String level2,
                                             @RequestParam(required = false) String level3) {
    if (level1 == null || level1.trim().isEmpty()) {
      return ResponseEntity.badRequest().body("지역 정보(level1)는 필수입니다.");
    }
    try {
      List<Event> events = eventService.readByRegion(level1, level2, level3);
      return ResponseEntity.ok().body(events);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("지역 검색 중 오류가 발생했습니다: " + e.getMessage());
    }
  }

  // 복합 필터로 이벤트 검색
  @GetMapping("/filter")
  public ResponseEntity<?> getByFilter(
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
  public ResponseEntity<?> create(@RequestBody Event event) {
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



}