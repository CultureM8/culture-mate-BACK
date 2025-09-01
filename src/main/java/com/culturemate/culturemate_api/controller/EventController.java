package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.dto.EventSearchDto;
import com.culturemate.culturemate_api.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  // 통합 이벤트 검색 (제목, 지역, 날짜, 타입 모두 지원)
  @GetMapping("/search")
  public ResponseEntity<?> search(EventSearchDto searchDto) {
    try {
      // 검색 조건이 비어있으면 전체 조회
      if (searchDto.isEmpty()) {
        List<Event> events = eventService.readAll();
        return ResponseEntity.ok().body(events);
      }
      
      List<Event> events = eventService.search(searchDto);
      return ResponseEntity.ok().body(events);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body("이벤트 검색 중 오류가 발생했습니다: " + e.getMessage());
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

  // TODO: 이벤트 정보 수정

}