package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberDetail;
import com.culturemate.culturemate_api.dto.MemberDetailDto;
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
  public MemberDetailDto searchMemberDetail(Long memberId) {
    MemberDetail memberDetail = memberDetailRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원 상세 정보가 존재하지 않습니다."));
    return MemberDetailDto.fromEntity(memberDetail);
  }

  // 생성
  public MemberDetailDto createMemberDetail(MemberDetailDto dto) {
    Member member = memberRepository.findById(dto.getId())
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

    MemberDetail memberDetail = MemberDetail.builder()
      .member(member)
      .user_name(dto.getUserName())
      .profile_image_id(dto.getProfileImageId())
      .background_image_id(dto.getBackgroundImageId())
      .intro(dto.getIntro())
      .MBTI(dto.getMBTI())
      .together_score(dto.getTogetherScore())
      .email(dto.getEmail())
      .visibility(dto.getVisibility())
      .build();

    MemberDetail saved = memberDetailRepository.save(memberDetail);
    return MemberDetailDto.fromEntity(saved);
  }

  // 수정
  public MemberDetailDto updateMemberDetail(Long memberId, MemberDetailDto dto) {
    MemberDetail memberDetail = memberDetailRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원 상세 정보가 존재하지 않습니다."));

    // 필드 업데이트
    memberDetail.setUser_name(dto.getUserName());
    memberDetail.setProfile_image_id(dto.getProfileImageId());
    memberDetail.setBackground_image_id(dto.getBackgroundImageId());
    memberDetail.setIntro(dto.getIntro());
    memberDetail.setMBTI(dto.getMBTI());
    memberDetail.setTogether_score(dto.getTogetherScore());
    memberDetail.setEmail(dto.getEmail());
    memberDetail.setVisibility(dto.getVisibility());

    return MemberDetailDto.fromEntity(memberDetail);
  }

  // 삭제
  public void deleteMemberDetail(Long memberId) {
    memberDetailRepository.deleteById(memberId);
  }

}
