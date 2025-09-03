package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberDetail;
import com.culturemate.culturemate_api.dto.MemberDetailRequestDto;
import com.culturemate.culturemate_api.dto.MemberDetailResponseDto;
import com.culturemate.culturemate_api.service.MemberDetailService;
import com.culturemate.culturemate_api.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member-detail")
@RequiredArgsConstructor
public class MemberDetailController {

  private final MemberDetailService memberDetailService;
  private final MemberService memberService;

  // 상세 조회
  @GetMapping("/{memberId}")
  public ResponseEntity<MemberDetailResponseDto> getByMemberId(@PathVariable Long memberId) {
    MemberDetail memberDetail = memberDetailService.findByMemberId(memberId);
    return ResponseEntity.ok(MemberDetailResponseDto.from(memberDetail));  // HTTP 200 + MemberDetailResponseDto 반환
  }

  // 생성
  @PostMapping("/{memberId}")
  public ResponseEntity<MemberDetailResponseDto> add(@PathVariable Long memberId, @Valid @RequestBody MemberDetailRequestDto dto) {
    Member member = memberService.findById(memberId);
    MemberDetail created = memberDetailService.create(member, dto);
    return ResponseEntity.status(201).body(MemberDetailResponseDto.from(created));  // HTTP 201 Created + 데이터 반환
  }

  // 수정
  @PutMapping("/{memberId}")
  public ResponseEntity<MemberDetailResponseDto> modify(@PathVariable Long memberId,
                                                       @Valid @RequestBody MemberDetailRequestDto dto) {
    MemberDetail updated = memberDetailService.update(memberId, dto);
    return ResponseEntity.ok(MemberDetailResponseDto.from(updated));  // HTTP 200 OK + 데이터 반환
  }

  // 삭제
  @DeleteMapping("/{memberId}")
  public ResponseEntity<Void> remove(@PathVariable Long memberId) {
    memberDetailService.delete(memberId);
    return ResponseEntity.noContent().build();  // HTTP 204 No Content
  }
}
