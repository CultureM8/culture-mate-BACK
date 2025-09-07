package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.dto.CustomUser;
import com.culturemate.culturemate_api.dto.TogetherRequestDto;
import com.culturemate.culturemate_api.dto.TogetherResponseDto;
import com.culturemate.culturemate_api.dto.TogetherSearchDto;
import com.culturemate.culturemate_api.service.MemberService;
import com.culturemate.culturemate_api.service.TogetherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/together")
@RequiredArgsConstructor
public class TogetherController {
  private final TogetherService togetherService;
  private final MemberService memberService;

  @GetMapping
  public ResponseEntity<List<TogetherResponseDto>> getAll() {
    return ResponseEntity.ok().body(
      togetherService.findAll().stream()
        .map(togetherService::toResponseDto)
        .collect(Collectors.toList())
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<TogetherResponseDto> getById(@PathVariable Long id) {
    Together together = togetherService.findById(id);
    return ResponseEntity.ok().body(togetherService.toResponseDto(together));
  }

  // 특정 회원이 호스트인 모집글 조회
  @GetMapping("/hosted-by/{hostId}")
  public ResponseEntity<List<TogetherResponseDto>> getByHostId(@PathVariable Long hostId) {
    Member host = memberService.findById(hostId);
    List<Together> togethers = togetherService.findByHost(host);
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(togetherService::toResponseDto)
      .collect(Collectors.toList()));
  }
  // 특정 회원이 실제 참여 중인 모집글 조회 (승인된 것만)
  @GetMapping("/with/{memberId}")
  public ResponseEntity<List<TogetherResponseDto>> getByMemberId(@PathVariable Long memberId) {
    Member member = memberService.findById(memberId);
    List<Together> togethers = togetherService.findByMemberAndStatus(member, "APPROVED");
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(togetherService::toResponseDto)
      .collect(Collectors.toList()));
  }
  // 모집글 통합 검색
  @GetMapping("/search")
  public ResponseEntity<List<TogetherResponseDto>> search(@RequestParam TogetherSearchDto searchDto) {
    if (searchDto.isEmpty()) {
      List<Together> togethers = togetherService.findAll();
      return ResponseEntity.ok().body(
        togethers.stream()
        .map(togetherService::toResponseDto)
        .collect(Collectors.toList()));
    }

    List<Together> togethers = togetherService.search(searchDto);
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(togetherService::toResponseDto)
      .collect(Collectors.toList()));
  }

  // 모집글 생성
  @PostMapping
  public ResponseEntity<TogetherResponseDto> add(@Valid @RequestBody TogetherRequestDto togetherRequestDto) {
    Together together = togetherService.create(togetherRequestDto);
    return ResponseEntity.ok().body(togetherService.toResponseDto(together));
  }

  // 모집글 수정
  @PutMapping("/{id}")
  public ResponseEntity<TogetherResponseDto> modify(@PathVariable Long id, @Valid @RequestBody TogetherRequestDto togetherRequestDto) {
    Together updatedTogether = togetherService.update(id, togetherRequestDto);
    return ResponseEntity.ok().body(togetherService.toResponseDto(updatedTogether));
  }

  // 모집글 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> remove(@PathVariable Long id) {
    togetherService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // 동행 신청 (승인 대기)
  @PostMapping("/{id}/apply")
  public ResponseEntity<Void> applyTogether(@PathVariable Long id, @AuthenticationPrincipal CustomUser customUser) {
    togetherService.applyTogether(id, customUser.getMemberId());
    return ResponseEntity.ok().build();
  }

  // 동행 참여 승인
  @PostMapping("/{togetherId}/participants/{participantId}/approve")
  public ResponseEntity<Void> approveParticipation(@PathVariable Long togetherId, @PathVariable Long participantId, @AuthenticationPrincipal CustomUser customUser) {
    togetherService.approveParticipation(togetherId, participantId, customUser.getMemberId());
    return ResponseEntity.ok().build();
  }

  // 동행 참여 거절
  @PostMapping("/{togetherId}/participants/{participantId}/reject")
  public ResponseEntity<Void> rejectParticipation(@PathVariable Long togetherId, @PathVariable Long participantId, @AuthenticationPrincipal CustomUser customUser) {
    togetherService.rejectParticipation(togetherId, participantId, customUser.getMemberId());
    return ResponseEntity.ok().build();
  }

  // 참여자 목록 조회 (상태별 필터링 가능)
  @GetMapping("/{togetherId}/participants")
  public ResponseEntity<List<Member>> getParticipants(@PathVariable Long togetherId, @RequestParam(required = false) String status) {
    List<Member> participants;
    if (status == null) {
      participants = togetherService.getAllParticipants(togetherId);
    } else {
      participants = togetherService.getParticipantsByStatus(togetherId, status);
    }
    return ResponseEntity.ok().body(participants);
  }

  // 참여 취소 (본인)
  @DeleteMapping("/{togetherId}/participants/cancel")
  public ResponseEntity<Void> cancelParticipation(@PathVariable Long togetherId, @AuthenticationPrincipal CustomUser customUser) {
    togetherService.leaveTogether(togetherId, customUser.getMemberId());
    return ResponseEntity.ok().build();
  }

  // 호스트 모집상태 변경
  @PatchMapping("/{togetherId}/recruiting/{status}")
  public ResponseEntity<Void> changeRecruitingStatus(@PathVariable Long togetherId, @PathVariable String status, @AuthenticationPrincipal CustomUser customUser) {
    if ("close".equals(status)) {
      togetherService.closeTogether(togetherId, customUser.getMemberId());
    } else if ("reopen".equals(status)) {
      togetherService.reopenTogether(togetherId, customUser.getMemberId());
    } else {
      throw new IllegalArgumentException("상태는 'close' 또는 'reopen'이어야 합니다.");
    }
    return ResponseEntity.ok().build();
  }

  // 내 신청 목록 조회 (상태별 필터링 가능)
  @GetMapping("/my-applications")
  public ResponseEntity<List<TogetherResponseDto>> getMyApplications(@RequestParam(required = false) String status, @AuthenticationPrincipal CustomUser customUser) {
    Member member = memberService.findById(customUser.getMemberId());
    List<Together> togethers;
    
    if (status == null) {
      togethers = togetherService.findByMemberAll(member);
    } else {
      togethers = togetherService.findByMemberAndStatus(member, status);
    }
    
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(togetherService::toResponseDto)
      .collect(Collectors.toList()));
  }

}