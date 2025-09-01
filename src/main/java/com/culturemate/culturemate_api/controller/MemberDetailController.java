package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.dto.MemberDetailDto;
import com.culturemate.culturemate_api.service.MemberDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member-detail")
@RequiredArgsConstructor
public class MemberDetailController {

  private final MemberDetailService memberDetailService;

  // 상세 조회
  @GetMapping("/{memberId}")
  public ResponseEntity<MemberDetailDto> searchMemberDetail(@PathVariable Long memberId) {
    MemberDetailDto dto = memberDetailService.searchMemberDetail(memberId);
    return ResponseEntity.ok(dto);  // HTTP 200 + MemberDetailDto 반환
  }

  // 생성
  @PostMapping
  public ResponseEntity<MemberDetailDto> createMemberDetail(@RequestBody MemberDetailDto dto) {
    MemberDetailDto created = memberDetailService.createMemberDetail(dto);
    return ResponseEntity.status(201).body(created);  // HTTP 201 Created + 데이터 반환
  }

  // 수정
  @PutMapping("/{memberId}")
  public ResponseEntity<MemberDetailDto> updateMemberDetail(@PathVariable Long memberId,
                                                            @RequestBody MemberDetailDto dto) {
    MemberDetailDto updated = memberDetailService.updateMemberDetail(memberId, dto);
    return ResponseEntity.ok(updated);  // HTTP 200 OK + 데이터 반환
  }

  // 삭제
  @DeleteMapping("/{memberId}")
  public ResponseEntity<Void> deleteMemberDetail(@PathVariable Long memberId) {
    memberDetailService.deleteMemberDetail(memberId);
    return ResponseEntity.noContent().build();  // HTTP 204 No Content
  }
}
