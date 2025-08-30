package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.domain.member.Role;
import com.culturemate.culturemate_api.dto.MemberDto;
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
  public MemberDto create(MemberDto memberDto) {
    if (memberRepository.existsByLoginId(memberDto.getLoginId())) {
      throw new IllegalArgumentException("이미 사용 중인 로그인 아이디입니다.");
    }

    Member member = Member.builder()
      .loginId(memberDto.getLoginId())
      .password(memberDto.getPassword())
      .role(memberDto.getRole() != null ? memberDto.getRole() : Role.MEMBER)
      .status(MemberStatus.ACTIVE)
      .joinedAt(Instant.now())
      .build();

    Member saved = memberRepository.save(member);

    return MemberDto.builder()
      .id(saved.getId())
      .loginId(saved.getLoginId())
      .role(saved.getRole())
      .status(saved.getStatus())
      .build();
  }

  // 회원 삭제
  @Transactional
  public void delete(Long memberId) {
    memberRepository.deleteById(memberId);
  }

  // 전체 회원 조회
  public List<MemberDto> getAllMembers(boolean isAdmin) {
    return memberRepository.findAll()
      .stream()
      .map(member -> MemberDto.builder()
        .id(member.getId())
        .loginId(member.getLoginId())
        .role(member.getRole())
        .status(member.getStatus())
        .password(isAdmin ? member.getPassword() : null) // 관리자일 경우 비밀번호 보여줌
        .build())
      .collect(Collectors.toList());
  }

  // ID로 회원 조회
  public MemberDto getById(Long id) {
    Member member = memberRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    return MemberDto.builder()
      .id(member.getId())
      .loginId(member.getLoginId())
      .role(member.getRole())
      .status(member.getStatus())
      .build();
  }

  // 로그인 아이디로 회원 조회
  public MemberDto findByLoginId(String loginId) {
    Member member = memberRepository.findByLoginId(loginId)
      .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 회원이 없습니다."));
    return MemberDto.builder()
      .id(member.getId())
      .loginId(member.getLoginId())
      .role(member.getRole())
      .status(member.getStatus())
      .build();
  }

  // 상태별 회원 목록 조회
  public List<MemberDto> findByStatus(MemberStatus status) {
    return memberRepository.findByStatus(status)
      .stream()
      .map(member -> MemberDto.builder()
        .id(member.getId())
        .loginId(member.getLoginId())
        .role(member.getRole())
        .status(member.getStatus())
        .build())
      .collect(Collectors.toList());
  }

  // 회원 상태 변경
  @Transactional
  public void updateStatus(Long memberId, MemberStatus newStatus) {
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    member.changeStatus(newStatus);
  }

  // 비밀번호 변경
  @Transactional
  public void updatePassword(Long memberId, String newPassword) {
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    member.changePassword(newPassword);
  }

  // 권한 변경
  @Transactional
  public void updateRole(Long memberId, Role newRole) {
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    member.changeRole(newRole);
  }
}
