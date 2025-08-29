package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.domain.member.Role;
import com.culturemate.culturemate_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
  private final MemberRepository memberRepository;

  // 회원 가입
  public Member create(Member member) {
    if (memberRepository.existsByLoginId(member.getLoginId())) {
      throw new IllegalArgumentException("이미 사용 중인 로그인 아이디입니다.");
    }
    return memberRepository.save(member);
  }

  // 회원 삭제
  public void delete(Long memberId) {
    memberRepository.deleteById(memberId);
  }

  // 전체 회원 조회
  @Transactional(readOnly = true)
  public List<Member> getAllMembers() {
    return memberRepository.findAll();
  }

  // ID로 회원 조회
  @Transactional(readOnly = true)
  public Member getById(Long id) {
    return memberRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
  }

  // 로그인 아이디로 회원 조회
  @Transactional(readOnly = true)
  public Member findByLoginId(String loginId) {
    return memberRepository.findByLoginId(loginId)
      .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 회원이 없습니다."));
  }

  // 상태별 회원 목록 조회
  @Transactional(readOnly = true)
  public List<Member> findByStatus(MemberStatus status) {
    return memberRepository.findByStatus(status);
  }

  // 회원 상태 변경
  public void updateStatus(Long memberId, MemberStatus newStatus) {
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    member.changeStatus(newStatus);
  }

  // 비밀번호 변경
  public void updatePassword(Long memberId, String newPassword) {
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    member.changePassword(newPassword);
  }

  // 권한 변경
  public void updateRole(Long memberId, Role newRole) {
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    member.changeRole(newRole);
  }
}
