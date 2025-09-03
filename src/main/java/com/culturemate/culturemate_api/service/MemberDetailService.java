package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberDetail;
import com.culturemate.culturemate_api.dto.MemberDetailRequestDto;
import com.culturemate.culturemate_api.repository.MemberDetailRepository;
import com.culturemate.culturemate_api.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberDetailService {
  private final MemberDetailRepository memberDetailRepository;
  private final MemberRepository memberRepository;

  // 모두 조회
  public MemberDetail findByMemberId(Long memberId) {
    return memberDetailRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원 상세 정보가 존재하지 않습니다."));
  }

  // 생성 (Member 객체와 DTO 사용) - @MapsId 활용
  public MemberDetail create(Member member, MemberDetailRequestDto dto) {
    MemberDetail memberDetail = MemberDetail.builder()
      .member(member)  // @MapsId에 의해 ID 자동 매핑
      .nickname(dto.getNickname())
//      .profileImageId(dto.getProfileImageId())
//      .backgroundImageId(dto.getBackgroundImageId())
      .intro(dto.getIntro())
      .mbti(dto.getMbti())
      .email(dto.getEmail())
      .build();

    return memberDetailRepository.save(memberDetail);
  }

  // 수정
  public MemberDetail update(Long memberId, MemberDetailRequestDto dto) {
    MemberDetail memberDetail = memberDetailRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원 상세 정보가 존재하지 않습니다."));

    // 필드 업데이트
    memberDetail.setNickname(dto.getNickname());
//    memberDetail.setProfileImageId(dto.getProfileImageId());
//    memberDetail.setBackgroundImageId(dto.getBackgroundImageId());
    memberDetail.setIntro(dto.getIntro());
    memberDetail.setMbti(dto.getMbti());
    memberDetail.setEmail(dto.getEmail());
    memberDetail.setVisibility(dto.getVisibility());

    return memberDetail;
  }

  // 삭제
  public void delete(Long memberId) {
    memberDetailRepository.deleteById(memberId);
  }

}
