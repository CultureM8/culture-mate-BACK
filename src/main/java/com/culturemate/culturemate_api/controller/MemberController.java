package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.domain.member.Role;
import com.culturemate.culturemate_api.dto.MemberResponseDto;
import com.culturemate.culturemate_api.dto.RegisterDto;
import com.culturemate.culturemate_api.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
  private final MemberService memberService;

  // 회원 가입
  @PostMapping
  public ResponseEntity<MemberResponseDto> add(@Valid @RequestBody RegisterDto registerDto) {
    Member savedMember = memberService.create(registerDto);
    return ResponseEntity.status(201).body(MemberResponseDto.from(savedMember));
  }

  // 회원 삭제
  @DeleteMapping("/{memberId}")
  public ResponseEntity<Void> remove(@PathVariable Long memberId) {
    memberService.delete(memberId);
    return ResponseEntity.noContent().build();
  }

  // 전체 회원 조회
  @GetMapping
  public ResponseEntity<List<MemberResponseDto>> get() {
    return ResponseEntity.ok(
      memberService.findAll()
        .stream()
        .map(MemberResponseDto::from)
        .collect(Collectors.toList())
    );
  }

  // ID로 회원 조회
  @GetMapping("/id/{id}")
  public ResponseEntity<MemberResponseDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(MemberResponseDto.from(memberService.findById(id)));
  }

  // 로그인 아이디로 회원 조회
  @GetMapping("/login/{loginId}")
  public ResponseEntity<MemberResponseDto> findByLoginId(@PathVariable String loginId) {
    return ResponseEntity.ok(MemberResponseDto.from(memberService.findByLoginId(loginId)));
  }

  // 상태별 회원 목록 조회 (관리자용)
  @GetMapping("/status/{status}")
  public ResponseEntity<List<MemberResponseDto>> findByStatus(@PathVariable MemberStatus status) {
    return ResponseEntity.ok(
      memberService.findByStatus(status)
        .stream()
        .map(MemberResponseDto::from)
        .collect(Collectors.toList())
    );
  }

  // 회원 상태 변경 (관리자용)
  @PatchMapping("/{id}/status")
  public ResponseEntity<MemberResponseDto> modifyStatus(
    @PathVariable Long id,
    @RequestParam MemberStatus status
  ) {
    Member updatedMember = memberService.updateStatus(id, status);
    return ResponseEntity.ok(MemberResponseDto.from(updatedMember));
  }

  // 비밀번호 변경
  @PatchMapping("/{id}/password")
  public ResponseEntity<MemberResponseDto> modifyPassword(
    @PathVariable Long id,
    @RequestParam String newPassword
  ) {
    Member updatedMember = memberService.updatePassword(id, newPassword);
    return ResponseEntity.ok(MemberResponseDto.from(updatedMember));
  }

  // 권한 변경 (관리자용)
  @PatchMapping("/{id}/role")
  public ResponseEntity<MemberResponseDto> modifyRole(
    @PathVariable Long id,
    @RequestParam Role role
  ) {
    Member updatedMember = memberService.updateRole(id, role);
    return ResponseEntity.ok(MemberResponseDto.from(updatedMember));
  }

}
