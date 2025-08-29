package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.domain.member.Role;
import com.culturemate.culturemate_api.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {
  private final MemberService memberService;

  public MemberController(MemberService memberService){
    this.memberService = memberService;
  }

  // 회원 가입
  @PostMapping
  public Member create(@RequestBody Member member) {
    return memberService.create(member);
  }

  // 회원 삭제
  @DeleteMapping
  public void delete(@PathVariable Long memberId) {
    memberService.delete(memberId);
  }

  // 전체 회원 조회
  @GetMapping
  public List<Member> getAllMembers() {
    return memberService.getAllMembers();
  }

  // ID로 회원 조회
  @GetMapping("/{id}")
  public Member getById(@PathVariable Long id) {
    return memberService.getById(id);
  }

  // 로그인 아이디로 회원 조회
  @GetMapping("/{loginId}")
  public Member findByLoginId(@PathVariable String loginId) {
    return memberService.findByLoginId(loginId);
  }

  // 상태별 회원 목록 조회
  @GetMapping("/status/{status}")
  public List<Member> findByStatus(@PathVariable MemberStatus status) {
    return memberService.findByStatus(status);
  }

  // 회원 상태 변경
  @PatchMapping("/{id}/status")
  public ResponseEntity<?> updateStatus(
    @PathVariable Long id,
    @RequestParam MemberStatus status
  ) {
    memberService.updateStatus(id, status);
    return ResponseEntity.ok("회원 상태가 변경되었습니다.");
  }

  // 비밀번호 변경
  @PatchMapping("/{id}/password")
  public ResponseEntity<String> updatePassword(
    @PathVariable Long id,
    @RequestParam String newPassword
  ) {
    memberService.updatePassword(id, newPassword);
    return ResponseEntity.ok("비밀번호가 변경되었습니다.");
  }

  // 권한 변경
  @PatchMapping("/{id}/role")
  public ResponseEntity<String> updateRole(
    @PathVariable Long id,
    @RequestParam Role role
  ) {
    memberService.updateRole(id, role);
    return ResponseEntity.ok("회원 권한이 변경되었습니다.");
  }

}
