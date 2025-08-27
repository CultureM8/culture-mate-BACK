package com.culturemate.culturemate_api.contoller;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.repository.MemberRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberContoller {
  private final MemberRepository memberRepository;

  public MemberContoller(MemberRepository memberRepository){
    this.memberRepository = memberRepository;
  }
  // 회원 전체 데이터 조회하기
  @GetMapping
  public List<Member> getAllMember(){
    return memberRepository.findAll();
  }

  // ID로 회원 데이터 조회하기
  @GetMapping("/{id}")
  public Member getMemberById(@PathVariable Long id) {
    return memberRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Member not found"));
  }

  // loginId로 회원 데이터 조회하기
  @GetMapping("/login/{loginId}")
  public Member getMemberByLoginId(@PathVariable String loginId) {
    return memberRepository.findByLoginId(loginId)
      .orElseThrow(() -> new RuntimeException("Member not found"));
  }

  // 회원 저장
  @PostMapping
  public Member createMember(@RequestBody Member member) {
    memberRepository.save(member);
    return member;
  }

  // 회원 삭제
  @DeleteMapping("/{id}")
  public String deleteMember(@PathVariable Long id) {
    memberRepository.deleteById(id);
    return "Member deleted: " + id;
  }

  // 상태별(정상인 회원들, 휴면인 회원들, 일시 정지인 회원들, 영구 정지인 회원들) 회원 조회
  @GetMapping("/status/{status}")
  public List<Member> getMembersByStatus(@PathVariable MemberStatus status) {
    return memberRepository.findByStatus(status);
  }

  // 로그인 ID 존재 여부 체크(있으면 로그인으로 넘어갈 수 있음, 혹은 현재 로그인 상태임을 인지시킴)
  @GetMapping("/exists/{loginId}")
  public boolean existsByLoginId(@PathVariable String loginId) {
    return memberRepository.existsByLoginId(loginId);
  }
}
