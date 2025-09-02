package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.dto.EventRequestDto;
import com.culturemate.culturemate_api.dto.EventSearchDto;
import com.culturemate.culturemate_api.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
  private final EventService eventService;

  // 이벤트 전체 데이터 조회하기
  @GetMapping
  public ResponseEntity<List<Event>> get() {
    return ResponseEntity.ok(eventService.findAll());
  }

  // 이벤트 ID로 데이터 조회
  @GetMapping("/{id}")
  public ResponseEntity<Event> getById(@PathVariable Long id) {
    Event event = eventService.findById(id);
    return ResponseEntity.ok(event);
  }

  // 통합 이벤트 검색 (제목, 지역, 날짜, 타입 모두 지원)
  @GetMapping("/search")
  public ResponseEntity<List<Event>> search(EventSearchDto searchDto) {
    // 검색 조건이 비어있으면 전체 조회
    if (searchDto.isEmpty()) {
      List<Event> events = eventService.findAll();
      return ResponseEntity.ok(events);
    }
    
    List<Event> events = eventService.search(searchDto);
    return ResponseEntity.ok(events);
  }

  // 이벤트 등록
  @PostMapping
  public ResponseEntity<Event> add(@Valid @RequestBody EventRequestDto requestDto) {
    Event createdEvent = eventService.create(requestDto);
    return ResponseEntity.status(201).body(createdEvent);
  }

  // 이벤트 정보 수정
  @PutMapping("/{id}")
  public ResponseEntity<Event> update(@PathVariable Long id, @Valid @RequestBody EventRequestDto requestDto) {
    Event updatedEvent = eventService.update(id, requestDto);
    return ResponseEntity.ok(updatedEvent);
  }

  // 관심 설정
  @PostMapping("/{eventId}/interest")
  public ResponseEntity<String> toggleInterest(@PathVariable Long eventId,
                                                  @RequestParam Long memberId) {
    boolean interest = eventService.toggleEventInterest(eventId, memberId);

    if (interest) {
      return ResponseEntity.ok("관심 등록");
    } else {
      return ResponseEntity.ok("관심 취소");
    }
  }

}