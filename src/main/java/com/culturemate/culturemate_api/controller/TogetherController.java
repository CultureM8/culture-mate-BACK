package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.dto.CustomUser;
import com.culturemate.culturemate_api.dto.MemberDto;
import com.culturemate.culturemate_api.dto.TogetherDto;
import com.culturemate.culturemate_api.dto.TogetherSearchDto;
import com.culturemate.culturemate_api.service.MemberService;
import com.culturemate.culturemate_api.service.TogetherService;
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

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Together API", description = "그룹 모임 관리 API")
@RestController
@RequestMapping("/api/v1/together")
@RequiredArgsConstructor
public class TogetherController {
  private final TogetherService togetherService;
  private final MemberService memberService;

  @Operation(summary = "전체 모임 조회", description = "모든 모임 목록을 조회합니다")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공")
  })
  @GetMapping
  public ResponseEntity<List<TogetherDto.Response>> getAll() {
    return ResponseEntity.ok().body(
      togetherService.findAll().stream()
        .map(togetherService::toResponseDto)
        .collect(Collectors.toList())
    );
  }

  @Operation(summary = "특정 모임 조회", description = "ID로 특정 모임을 조회합니다")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공"),
    @ApiResponse(responseCode = "404", description = "모임을 찾을 수 없음")
  })
  @GetMapping("/{id}")
  public ResponseEntity<TogetherDto.Response> getById(
    @Parameter(description = "모임 ID", required = true) @PathVariable Long id) {
    Together together = togetherService.findById(id);
    return ResponseEntity.ok().body(togetherService.toResponseDto(together));
  }

  @Operation(summary = "호스트별 모임 조회", description = "특정 회원이 호스트인 모임을 조회합니다")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "조회 성공"),
    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
  })
  @GetMapping("/hosted-by/{hostId}")
  public ResponseEntity<List<TogetherDto.Response>> getByHostId(
    @Parameter(description = "호스트 회원 ID", required = true) @PathVariable Long hostId) {
    Member host = memberService.findById(hostId);
    List<Together> togethers = togetherService.findByHost(host);
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(togetherService::toResponseDto)
      .collect(Collectors.toList()));
  }
  // 특정 회원이 실제 참여 중인 모집글 조회 (승인된 것만)
  @GetMapping("/with/{memberId}")
  public ResponseEntity<List<TogetherDto.Response>> getByMemberId(@PathVariable Long memberId) {
    Member member = memberService.findById(memberId);
    List<Together> togethers = togetherService.findByMemberAndStatus(member, "APPROVED");
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(togetherService::toResponseDto)
      .collect(Collectors.toList()));
  }
  // 모집글 통합 검색
  @GetMapping("/search")
  public ResponseEntity<List<TogetherDto.Response>> search(@RequestParam TogetherSearchDto searchDto) {
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
  public ResponseEntity<TogetherDto.Response> add(@Valid @RequestBody TogetherDto.Request togetherRequestDto) {
    Together together = togetherService.create(togetherRequestDto);
    return ResponseEntity.ok().body(togetherService.toResponseDto(together));
  }

  // 모집글 수정
  @PutMapping("/{id}")
  public ResponseEntity<TogetherDto.Response> modify(@PathVariable Long id, @Valid @RequestBody TogetherDto.Request togetherRequestDto) {
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
  public ResponseEntity<List<MemberDto.Response>> getParticipants(@PathVariable Long togetherId, @RequestParam(required = false) String status) {
    List<Member> participants;
    if (status == null) {
      participants = togetherService.getAllParticipants(togetherId);
    } else {
      participants = togetherService.getParticipantsByStatus(togetherId, status);
    }
    
    List<MemberDto.Response> participantDtos = participants.stream()
      .map(MemberDto.Response::from)
      .collect(Collectors.toList());
    
    return ResponseEntity.ok().body(participantDtos);
  }

  // 참여 취소 (본인)
  @DeleteMapping("/{togetherId}/participants/cancel")
  public ResponseEntity<Void> cancelParticipation(@PathVariable Long togetherId, @AuthenticationPrincipal CustomUser customUser) {
    togetherService.leaveTogether(togetherId, customUser.getMemberId());
    return ResponseEntity.ok().build();
  }

  // 호스트의 참여자 강제 퇴출
  @DeleteMapping("/{togetherId}/participants/{participantId}")
  public ResponseEntity<Void> removeMember(@PathVariable Long togetherId, @PathVariable Long participantId, @AuthenticationPrincipal CustomUser customUser) {
    togetherService.removeMember(togetherId, participantId, customUser.getMemberId());
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
  public ResponseEntity<List<TogetherDto.Response>> getMyApplications(@RequestParam(required = false) String status, @AuthenticationPrincipal CustomUser customUser) {
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