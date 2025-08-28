package com.culturemate.culturemate_api.contoller;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberContoller {
  private final MemberService memberService;

  public MemberContoller(MemberService memberService){
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

  // id로 회원 조회
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

}
