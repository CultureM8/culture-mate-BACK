package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.dto.MemberDto;
import com.culturemate.culturemate_api.dto.TogetherDto;
import com.culturemate.culturemate_api.dto.TogetherSearchDto;
import com.culturemate.culturemate_api.service.ChatRoomService;
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
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Together API", description = "그룹 모임 관리 API")
@RestController
@RequestMapping("/api/v1/together")
@RequiredArgsConstructor
public class TogetherController {
  private final TogetherService togetherService;
  private final MemberService memberService;
  private final ChatRoomService chatRoomService;

  @Operation(summary = "전체 모임 조회", description = "모든 모임 목록을 조회합니다")
  @GetMapping
  public ResponseEntity<List<TogetherDto.Response>> getAllTogethers(@AuthenticationPrincipal AuthenticatedUser user) {
    List<Together> togethers = togetherService.findAll();
    
    if (user != null) {
      // 인증된 사용자: 관심 여부 포함
      List<Long> togetherIds = togethers.stream().map(Together::getId).toList();
      Map<Long, Boolean> interestMap = togetherService.getInterestStatusBatch(togetherIds, user.getMemberId());
      
      List<TogetherDto.Response> responseDtos = togethers.stream()
        .map(together -> togetherService.toResponseDto(together, interestMap.getOrDefault(together.getId(), false)))
        .collect(Collectors.toList());
      return ResponseEntity.ok(responseDtos);
    } else {
      // 비인증 사용자: 관심 여부 미포함
      return ResponseEntity.ok().body(
        togethers.stream()
          .map(togetherService::toResponseDto)
          .collect(Collectors.toList())
      );
    }
  }

  @Operation(summary = "특정 모임 조회", description = "ID로 특정 모임을 조회합니다")
  @GetMapping("/{id}")
  public ResponseEntity<TogetherDto.Response> getTogetherById(@Parameter(description = "모임 ID", required = true) @PathVariable Long id,
                                                              @AuthenticationPrincipal AuthenticatedUser user) {
    Together together = togetherService.findById(id);
    
    boolean isInterested = false;
    if (user != null) {
      isInterested = togetherService.isInterested(id, user.getMemberId());
    }
    
    return ResponseEntity.ok().body(togetherService.toResponseDto(together, isInterested));
  }

  @Operation(summary = "호스트별 모임 조회", description = "특정 회원이 호스트인 모임을 조회합니다")
  @GetMapping("/hosted-by/{hostId}")
  public ResponseEntity<List<TogetherDto.Response>> getTogethersByHostId(@Parameter(description = "호스트 회원 ID", required = true)
                                                                           @PathVariable Long hostId) {
    Member host = memberService.findById(hostId);
    List<Together> togethers = togetherService.findByHost(host);
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(togetherService::toResponseDto)
      .collect(Collectors.toList()));
  }

  // 특정 회원이 실제 참여 중인 모집글 조회 (승인된 것만)
  @GetMapping("/with/{memberId}")
  public ResponseEntity<List<TogetherDto.Response>> getTogethersByMemberId(@PathVariable Long memberId) {
    Member member = memberService.findById(memberId);
    List<Together> togethers = togetherService.findByMemberAndStatus(member, "APPROVED");
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(togetherService::toResponseDto)
      .collect(Collectors.toList()));
  }

  // 모집글 통합 검색
  @GetMapping("/search")
  public ResponseEntity<List<TogetherDto.Response>> searchTogethers(TogetherSearchDto searchDto) {
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
  public ResponseEntity<TogetherDto.Response> createTogether(@Valid @RequestBody TogetherDto.Request togetherRequestDto) {
    Together together = togetherService.create(togetherRequestDto);
    return ResponseEntity.ok().body(togetherService.toResponseDto(together));
  }

  // 모집글 수정
  @PutMapping("/{togetherId}")
  public ResponseEntity<TogetherDto.Response> updateTogether(@PathVariable Long togetherId,
                                                             @Valid @RequestBody TogetherDto.Request togetherRequestDto,
                                                             @AuthenticationPrincipal AuthenticatedUser requester) {
    Together updatedTogether = togetherService.update(togetherId, togetherRequestDto, requester.getMemberId());
    return ResponseEntity.ok().body(togetherService.toResponseDto(updatedTogether));
  }

  // 모집글 삭제
  @DeleteMapping("/{togetherId}")
  public ResponseEntity<Void> deleteTogether(@PathVariable Long togetherId,
                                            @AuthenticationPrincipal AuthenticatedUser requester) {
    togetherService.delete(togetherId, requester.getMemberId());
    return ResponseEntity.noContent().build();
  }

  // 동행 신청 (승인 대기)
  @PostMapping("/{togetherId}/apply")
  public ResponseEntity<Void> applyTogether(@PathVariable Long togetherId,
                                            @RequestParam String message,
                                            @AuthenticationPrincipal AuthenticatedUser requester) {
    // 동행 신청
    togetherService.applyTogether(togetherId, requester.getMemberId());

    // 신청 채팅방 생성
    ChatRoom newChatRoom =  chatRoomService.createChatRoom();
    chatRoomService.addMemberToRoom(newChatRoom.getId(), requester.getMemberId());
    
    Together together = togetherService.findById(togetherId);
    chatRoomService.addMemberToRoom(newChatRoom.getId(), together.getHost().getId());

    // 신청 메시지 보내기
    chatRoomService.sendMessage(newChatRoom.getId(), requester.getMemberId(), message);

    return ResponseEntity.ok().build();
  }

  // 동행 참여 승인
  @PostMapping("/{togetherId}/participants/{participantId}/approve")
  public ResponseEntity<Void> approveParticipation(@PathVariable Long togetherId,
                                                   @PathVariable Long participantId,
                                                   @AuthenticationPrincipal AuthenticatedUser requester) {
    togetherService.approveParticipation(togetherId, participantId, requester.getMemberId());
    return ResponseEntity.ok().build();
  }

  // 동행 참여 거절
  @PostMapping("/{togetherId}/participants/{participantId}/reject")
  public ResponseEntity<Void> rejectParticipation(@PathVariable Long togetherId,
                                                  @PathVariable Long participantId,
                                                  @AuthenticationPrincipal AuthenticatedUser requester) {
    togetherService.rejectParticipation(togetherId, participantId, requester.getMemberId());
    return ResponseEntity.ok().build();
  }

  // 참여자 목록 조회 (상태별 필터링 가능)
  @GetMapping("/{togetherId}/participants")
  public ResponseEntity<List<MemberDto.Response>> getTogetherParticipants(@PathVariable Long togetherId,
                                                                          @RequestParam(required = false) String status) {
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
  public ResponseEntity<Void> cancelParticipation(@PathVariable Long togetherId,
                                                  @AuthenticationPrincipal AuthenticatedUser requester) {
    togetherService.leaveTogether(togetherId, requester.getMemberId());
    return ResponseEntity.ok().build();
  }

  // 호스트의 참여자 강제 퇴출
  @DeleteMapping("/{togetherId}/participants/{participantId}")
  public ResponseEntity<Void> removeTogetherMember(@PathVariable Long togetherId,
                                                   @PathVariable Long participantId,
                                                   @AuthenticationPrincipal AuthenticatedUser requester) {
    togetherService.removeMember(togetherId, participantId, requester.getMemberId());
    return ResponseEntity.ok().build();
  }

  // 호스트 모집상태 변경
  @PatchMapping("/{togetherId}/recruiting/{status}")
  public ResponseEntity<Void> changeRecruitingStatus(@PathVariable Long togetherId,
                                                     @PathVariable String status,
                                                     @AuthenticationPrincipal AuthenticatedUser requester) {
    if ("close".equals(status)) {
      togetherService.closeTogether(togetherId, requester.getMemberId());
    } else if ("reopen".equals(status)) {
      togetherService.reopenTogether(togetherId, requester.getMemberId());
    } else {
      throw new IllegalArgumentException("상태는 'close' 또는 'reopen'이어야 합니다.");
    }
    return ResponseEntity.ok().build();
  }

  // 내 신청 목록 조회 (상태별 필터링 가능)
  @GetMapping("/my-applications")
  public ResponseEntity<List<TogetherDto.Response>> getMyApplications(@RequestParam(required = false) String status,
                                                                      @AuthenticationPrincipal AuthenticatedUser requester) {
    Member member = memberService.findById(requester.getMemberId());
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

  // 관심 등록/해제
  @Operation(summary = "동행 관심 등록/해제", description = "특정 동행에 대한 관심을 등록하거나 해제합니다")
  @PostMapping("/{togetherId}/interest")
  public ResponseEntity<String> toggleTogetherInterest(@Parameter(description = "동행 ID", required = true) @PathVariable Long togetherId,
                                                       @AuthenticationPrincipal AuthenticatedUser user) {
    if (user == null) {
      return ResponseEntity.status(401).body("인증이 필요합니다");
    }

    boolean interest = togetherService.toggleInterest(togetherId, user.getMemberId());

    if (interest) {
      return ResponseEntity.ok("관심 등록");
    } else {
      return ResponseEntity.ok("관심 취소");
    }
  }

  // 내가 관심 등록한 동행 목록 조회
  @Operation(summary = "관심 동행 목록 조회", description = "사용자가 관심 등록한 동행 목록을 조회합니다")
  @GetMapping("/my-interests")
  public ResponseEntity<List<TogetherDto.Response>> getMyInterests(@AuthenticationPrincipal AuthenticatedUser user) {
    if (user == null) {
      return ResponseEntity.status(401).build();
    }

    List<Together> interestTogethers = togetherService.getUserInterestTogethers(user.getMemberId());
    List<TogetherDto.Response> responseDtos = interestTogethers.stream()
      .map(togetherService::toResponseDto)
      .collect(Collectors.toList());

    return ResponseEntity.ok(responseDtos);
  }

}
