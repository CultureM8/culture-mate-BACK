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
  public ResponseEntity<List<TogetherResponseDto>> get() {
    return ResponseEntity.ok().body(
      togetherService.findAll().stream()
        .map(TogetherResponseDto::from)
        .collect(Collectors.toList())
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<TogetherResponseDto> getById(@PathVariable Long id) {
    Together together = togetherService.findById(id);
    return ResponseEntity.ok().body(TogetherResponseDto.from(together));
  }

  // 특정 회원이 호스트인 모집글 조회
  @GetMapping("/hosted-by/{hostId}")
  public ResponseEntity<List<TogetherResponseDto>> getByHostId(@PathVariable Long hostId) {
    Member host = memberService.findById(hostId);
    List<Together> togethers = togetherService.findByHost(host);
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(TogetherResponseDto::from)
      .collect(Collectors.toList()));
  }
  // 특정 회원이 참여하는 모집글 조회
  @GetMapping("/with/{memberId}")
  public ResponseEntity<List<TogetherResponseDto>> getByMemberId(@PathVariable Long memberId) {
    Member member = memberService.findById(memberId);
    List<Together> togethers = togetherService.findByMember(member);
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(TogetherResponseDto::from)
      .collect(Collectors.toList()));
  }
  // 모집글 통합 검색
  @GetMapping("/search")
  public ResponseEntity<List<TogetherResponseDto>> search(@RequestParam TogetherSearchDto searchDto) {
    if (searchDto.isEmpty()) {
      List<Together> togethers = togetherService.findAll();
      return ResponseEntity.ok().body(
        togethers.stream()
        .map(TogetherResponseDto::from)
        .collect(Collectors.toList()));
    }

    List<Together> togethers = togetherService.search(searchDto);
    return ResponseEntity.ok().body(
      togethers.stream()
      .map(TogetherResponseDto::from)
      .collect(Collectors.toList()));
  }

  // 모집글 생성
  @PostMapping
  public ResponseEntity<TogetherResponseDto> add(@Valid @RequestBody TogetherRequestDto togetherRequestDto) {
    Together together = togetherService.create(togetherRequestDto);
    return ResponseEntity.ok().body(TogetherResponseDto.from(together));
  }

  // 모집글 수정
  @PutMapping("/{id}")
  public ResponseEntity<TogetherResponseDto> modify(@PathVariable Long id, @Valid @RequestBody TogetherRequestDto togetherRequestDto) {
    Together updatedTogether = togetherService.update(id, togetherRequestDto);
    return ResponseEntity.ok().body(TogetherResponseDto.from(updatedTogether));
  }

  // 모집글 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> remove(@PathVariable Long id) {
    togetherService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // 동행 참여 요청
  @PostMapping("/{id}/join")
  public ResponseEntity<Void> joinTogether(@PathVariable Long id, @AuthenticationPrincipal CustomUser customUser) {
    togetherService.joinTogether(id, customUser.getMemberId());
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

}