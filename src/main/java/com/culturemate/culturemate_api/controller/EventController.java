package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.Image;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.dto.EventDto;
import com.culturemate.culturemate_api.dto.EventSearchDto;
import com.culturemate.culturemate_api.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
  private final EventService eventService;

  // 이벤트 전체 데이터 조회하기
  @GetMapping
  public ResponseEntity<List<EventDto.Response>> get() {
    List<Event> events = eventService.findAll();
    List<EventDto.Response> responseDtos = events.stream()
      .map(EventDto.Response::from)
      .toList();
    return ResponseEntity.ok(responseDtos);
  }

  // 이벤트 ID로 데이터 조회 (상세 정보)
  @GetMapping("/{id}")
  public ResponseEntity<EventDto.ResponseDetail> getById(@PathVariable Long id) {
    EventDto.ResponseDetail event = eventService.findDetailById(id);
    return ResponseEntity.ok(event);
  }

  // 통합 이벤트 검색 (제목, 지역, 날짜, 타입 모두 지원)
  @GetMapping("/search")
  public ResponseEntity<List<EventDto.Response>> search(EventSearchDto searchDto) {
    // 디버깅용 로그
    System.out.println("=== 검색 파라미터 ===");
    System.out.println("keyword: " + searchDto.getKeyword());
    System.out.println("eventType: " + searchDto.getEventType());
    System.out.println("regionDto: " + searchDto.getRegionDto());
    System.out.println("isEmpty(): " + searchDto.isEmpty());
    System.out.println("hasKeyword(): " + searchDto.hasKeyword());
    System.out.println("==================");
    
    // 검색 조건이 비어있으면 전체 조회
    if (searchDto.isEmpty()) {
      List<Event> events = eventService.findAll();
      List<EventDto.Response> responseDtos = events.stream()
        .map(EventDto.Response::from)
        .toList();
      return ResponseEntity.ok(responseDtos);
    }
    
    List<Event> events = eventService.search(searchDto);
    List<EventDto.Response> responseDtos = events.stream()
      .map(EventDto.Response::from)
      .toList();
    return ResponseEntity.ok(responseDtos);
  }

  // 이벤트 등록
  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<EventDto.ResponseDetail> add(
      @RequestPart(value = "eventRequestDto") EventDto.Request eventRequestDto,
      @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
      @RequestParam(value = "imagesToAdd", required = false) List<MultipartFile> imagesToAdd) {
    
    Event createdEvent = eventService.create(eventRequestDto, mainImage, imagesToAdd);
    EventDto.ResponseDetail responseDetail = eventService.findDetailById(createdEvent.getId());
    return ResponseEntity.status(201).body(responseDetail);
  }

  // 이벤트 정보 수정
  @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
  public ResponseEntity<EventDto.Response> update(
      @PathVariable Long id,
      @RequestPart(value = "eventRequestDto") EventDto.Request eventRequestDto,
      @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
      @RequestParam(value = "imagesToAdd", required = false) List<MultipartFile> imagesToAdd) {
    
    Event updatedEvent = eventService.update(id, eventRequestDto, mainImage, imagesToAdd);
    return ResponseEntity.ok(EventDto.Response.from(updatedEvent));
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

  // 이벤트 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    eventService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // =========================== 이미지 따로 업로드 (대다수 경우 PUT 수정으로 처리 가능) ===========================

  // 이벤트 설명 이미지 목록 조회
  @GetMapping("/{eventId}/content-images")
  public ResponseEntity<List<String>> getContentImages(@PathVariable Long eventId) {
    List<Image> images = eventService.getContentImages(eventId);
    List<String> imagePaths = images.stream().map(Image::getPath).toList();
    return ResponseEntity.ok(imagePaths);
  }

  // ℹ️ 나머지 이미지 처리는 PUT /{id} 엔드포인트를 사용하세요
  // - 메인 이미지 수정/삭제: mainImage 파라미터
  // - 내용 이미지 추가: imagesToAdd 파라미터
  // - 이미지 삭제: eventRequestDto.imagesToDelete 필드

}