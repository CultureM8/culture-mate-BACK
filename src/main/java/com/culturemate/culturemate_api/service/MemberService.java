package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.domain.member.Role;
import com.culturemate.culturemate_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
  private final MemberRepository memberRepository;


  // 회원 가입
  @Transactional
  public Member create(String loginId, String password) {
    if (memberRepository.existsByLoginId(loginId)) {
      throw new IllegalArgumentException("이미 사용 중인 로그인 아이디입니다.");
    }

    Member member = Member.builder()
      .loginId(loginId)
      .password(password)
      .role(Role.MEMBER)
      .status(MemberStatus.ACTIVE)
      .joinedAt(Instant.now())
      .build();

    return memberRepository.save(member);
  }

  // 회원 삭제
  @Transactional
  public void delete(Long memberId) {
    memberRepository.deleteById(memberId);
  }

  // 전체 회원 조회
  public List<Member> findAll() {
    return memberRepository.findAll();
  }

  // ID로 회원 조회
  public Member findById(Long id) {
    return memberRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
  }

  // 로그인 아이디로 회원 조회
  public Member findByLoginId(String loginId) {
    return memberRepository.findByLoginId(loginId)
      .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 회원이 없습니다."));
  }

  // 상태별 회원 조회
  public List<Member> findByStatus(MemberStatus status) {
    return memberRepository.findByStatus(status);
  }

  // 회원 상태 변경
  @Transactional
  public Member updateStatus(Long memberId, MemberStatus newStatus) {
    Member member = findById(memberId);
    member.changeStatus(newStatus);
    return member;
  }

  // 비밀번호 변경
  @Transactional
  public Member updatePassword(Long memberId, String newPassword) {
    Member member = findById(memberId);
    member.changePassword(newPassword);
    return member;
  }

  // 권한 변경
  @Transactional
  public Member updateRole(Long memberId, Role newRole) {
    Member member = findById(memberId);
    member.changeRole(newRole);
    return member;
  }
}
