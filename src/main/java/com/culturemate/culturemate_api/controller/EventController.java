package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.Image;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.dto.EventDto;
import com.culturemate.culturemate_api.dto.EventSearchDto;
import com.culturemate.culturemate_api.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "Event API", description = "문화 이벤트 관리 API")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
  private final EventService eventService;

  @Operation(summary = "전체 이벤트 조회", description = "모든 문화 이벤트를 조회합니다")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공")
  })
  @GetMapping
  public ResponseEntity<List<EventDto.Response>> getAllEvents(
      @AuthenticationPrincipal AuthenticatedUser user) {
    List<Event> events = eventService.findAll();
    
    if (user != null) {
      // 인증된 사용자: 관심 여부 포함
      List<Long> eventIds = events.stream().map(Event::getId).toList();
      Map<Long, Boolean> interestMap = eventService.getInterestStatusBatch(eventIds, user.getMemberId());
      
      List<EventDto.Response> responseDtos = events.stream()
        .map(event -> EventDto.Response.from(event, interestMap.getOrDefault(event.getId(), false)))
        .toList();
      return ResponseEntity.ok(responseDtos);
    } else {
      // 비인증 사용자: 기본값 false
      List<EventDto.Response> responseDtos = events.stream()
        .map(event -> EventDto.Response.from(event, false))
        .toList();
      return ResponseEntity.ok(responseDtos);
    }
  }

  // 이벤트 ID로 데이터 조회 (상세 정보)
  @GetMapping("/{id}")
  public ResponseEntity<EventDto.ResponseDetail> getEventById(
      @PathVariable Long id,
      @AuthenticationPrincipal AuthenticatedUser user) {
    Event event = eventService.findByIdWithDetails(id);
    List<String> contentImages = eventService.getContentImagePaths(id);
    
    boolean isInterested = false;
    if (user != null) {
      isInterested = eventService.isInterested(id, user.getMemberId());
    }
    
    EventDto.ResponseDetail responseDetail = EventDto.ResponseDetail.from(event, contentImages, isInterested);
    return ResponseEntity.ok(responseDetail);
  }

  // 통합 이벤트 검색 (제목, 지역, 날짜, 타입 모두 지원)
  @GetMapping("/search")
  public ResponseEntity<List<EventDto.Response>> searchEvents(
      EventSearchDto searchDto,
      @AuthenticationPrincipal AuthenticatedUser user) {
    // 디버깅용 로그
    System.out.println("=== 검색 파라미터 ===");
    System.out.println("keyword: " + searchDto.getKeyword());
    System.out.println("eventType: " + searchDto.getEventType());
    System.out.println("regionDto: " + searchDto.getRegion());
    System.out.println("isEmpty(): " + searchDto.isEmpty());
    System.out.println("hasKeyword(): " + searchDto.hasKeyword());
    System.out.println("==================");
    
    List<Event> events;
    // 검색 조건이 비어있으면 전체 조회
    if (searchDto.isEmpty()) {
      events = eventService.findAll();
    } else {
      events = eventService.search(searchDto);
    }
    
    if (user != null) {
      // 인증된 사용자: 관심 여부 포함
      List<Long> eventIds = events.stream().map(Event::getId).toList();
      Map<Long, Boolean> interestMap = eventService.getInterestStatusBatch(eventIds, user.getMemberId());
      
      List<EventDto.Response> responseDtos = events.stream()
        .map(event -> EventDto.Response.from(event, interestMap.getOrDefault(event.getId(), false)))
        .toList();
      return ResponseEntity.ok(responseDtos);
    } else {
      // 비인증 사용자: 기본값 false
      List<EventDto.Response> responseDtos = events.stream()
        .map(event -> EventDto.Response.from(event, false))
        .toList();
      return ResponseEntity.ok(responseDtos);
    }
  }

  // 이벤트 등록
  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<EventDto.ResponseDetail> createEvent(
      @RequestPart(value = "eventRequestDto") EventDto.Request eventRequestDto,
      @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
      @RequestParam(value = "imagesToAdd", required = false) List<MultipartFile> imagesToAdd) {
    
    Event createdEvent = eventService.create(eventRequestDto, mainImage, imagesToAdd);
    List<String> contentImages = eventService.getContentImagePaths(createdEvent.getId());
    EventDto.ResponseDetail responseDetail = EventDto.ResponseDetail.from(createdEvent, contentImages, false);
    return ResponseEntity.status(201).body(responseDetail);
  }

  // 이벤트 정보 수정
  @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
  public ResponseEntity<EventDto.Response> updateEvent(
      @PathVariable Long id,
      @RequestPart(value = "eventRequestDto") EventDto.Request eventRequestDto,
      @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
      @RequestParam(value = "imagesToAdd", required = false) List<MultipartFile> imagesToAdd,
      @AuthenticationPrincipal AuthenticatedUser requester) {
    
    Event updatedEvent = eventService.update(id, eventRequestDto, mainImage, imagesToAdd, requester.getMemberId());
    return ResponseEntity.ok(EventDto.Response.from(updatedEvent, false));
  }

  // 관심 설정
  @PostMapping("/{eventId}/interest")
  public ResponseEntity<String> toggleEventInterest(@PathVariable Long eventId,
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
  public ResponseEntity<Void> deleteEvent(@PathVariable Long id,
                                         @AuthenticationPrincipal AuthenticatedUser requester) {
    eventService.delete(id, requester.getMemberId());
    return ResponseEntity.noContent().build();
  }

  // =========================== 이미지 따로 업로드 (대다수 경우 PUT 수정으로 처리 가능) ===========================

  // 이벤트 설명 이미지 목록 조회
  @GetMapping("/{eventId}/content-images")
  public ResponseEntity<List<String>> getEventContentImages(@PathVariable Long eventId) {
    List<Image> images = eventService.getContentImages(eventId);
    List<String> imagePaths = images.stream().map(Image::getPath).toList();
    return ResponseEntity.ok(imagePaths);
  }

  // ℹ️ 나머지 이미지 처리는 PUT /{id} 엔드포인트를 사용하세요
  // - 메인 이미지 수정/삭제: mainImage 파라미터
  // - 내용 이미지 추가: imagesToAdd 파라미터
  // - 이미지 삭제: eventRequestDto.imagesToDelete 필드

}