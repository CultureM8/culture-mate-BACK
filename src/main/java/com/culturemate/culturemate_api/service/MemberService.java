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

  // Entity를 DTO로 변환하는 내부 메서드
  private MemberDto toDto(Member member) {
    return MemberDto.builder()
      .id(member.getId())
      .loginId(member.getLoginId())
      .role(member.getRole())
      .status(member.getStatus())
      .build();
  }

  // Entity를 DTO로 변환하는 내부 메서드 (관리자용 - 비밀번호 포함)
  private MemberDto toDtoWithPassword(Member member) {
    return MemberDto.builder()
      .id(member.getId())
      .loginId(member.getLoginId())
      .password(member.getPassword())
      .role(member.getRole())
      .status(member.getStatus())
      .build();
  }

  // 회원 가입
  @Transactional
  public Member create(MemberDto memberDto) {
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

    return memberRepository.save(member);
  }

  // 회원 삭제
  @Transactional
  public void delete(Long memberId) {
    memberRepository.deleteById(memberId);
  }

  // 전체 회원 조회 (엔티티 반환)
  public List<Member> findAll() {
    return memberRepository.findAll();
  }

  // 전체 회원 DTO 조회 (외부 API용)
  public List<MemberDto> findAllDto(boolean isAdmin) {
    return findAll()
      .stream()
      .map(member -> isAdmin ? toDtoWithPassword(member) : toDto(member))
      .collect(Collectors.toList());
  }

  // ID로 회원 조회 (엔티티 반환)
  public Member findById(Long id) {
    return memberRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
  }

  // ID로 회원 DTO 조회 (외부 API용)
  public MemberDto findByIdDto(Long id) {
    return toDto(findById(id));
  }

  // 로그인 아이디로 회원 조회 (엔티티 반환)
  public Member findByLoginId(String loginId) {
    return memberRepository.findByLoginId(loginId)
      .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 회원이 없습니다."));
  }

  // 로그인 아이디로 회원 DTO 조회 (외부 API용)
  public MemberDto findByLoginIdDto(String loginId) {
    return toDto(findByLoginId(loginId));
  }

  // 상태별 회원 조회 (엔티티 반환)
  public List<Member> findByStatus(MemberStatus status) {
    return memberRepository.findByStatus(status);
  }

  // 상태별 회원 DTO 목록 조회 (외부 API용)
  public List<MemberDto> findByStatusDto(MemberStatus status) {
    return findByStatus(status)
      .stream()
      .map(this::toDto)
      .collect(Collectors.toList());
  }

  // 회원 상태 변경
  @Transactional
  public void updateStatus(Long memberId, MemberStatus newStatus) {
    Member member = findById(memberId);
    member.changeStatus(newStatus);
  }

  // 비밀번호 변경
  @Transactional
  public void updatePassword(Long memberId, String newPassword) {
    Member member = findById(memberId);
    member.changePassword(newPassword);
  }

  // 권한 변경
  @Transactional
  public void updateRole(Long memberId, Role newRole) {
    Member member = findById(memberId);
    member.changeRole(newRole);
  }
}
