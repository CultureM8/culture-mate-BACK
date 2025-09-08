package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.ImageTarget;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberDetail;
import com.culturemate.culturemate_api.dto.MemberDto;
import com.culturemate.culturemate_api.repository.MemberDetailRepository;
import com.culturemate.culturemate_api.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberDetailService {
  private final MemberDetailRepository memberDetailRepository;
  private final MemberRepository memberRepository;
  private final ImageService imageService;

  // 모두 조회
  public MemberDetail findByMemberId(Long memberId) {
    return memberDetailRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원 상세 정보가 존재하지 않습니다."));
  }

  // 생성 (Member 객체와 DTO 사용) - @MapsId 활용
  public MemberDetail create(Member member, MemberDto.DetailRequest dto) {
    MemberDetail memberDetail = MemberDetail.builder()
      .member(member)  // @MapsId에 의해 ID 자동 매핑
      .nickname(dto.getNickname())
      .intro(dto.getIntro())
      .mbti(dto.getMbti())
      .email(dto.getEmail())
      .build();

    return memberDetailRepository.save(memberDetail);
  }

  // 수정
  public MemberDetail update(Long memberId, MemberDto.DetailRequest dto) {
    MemberDetail memberDetail = memberDetailRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원 상세 정보가 존재하지 않습니다."));

    // 필드 업데이트
    memberDetail.setNickname(dto.getNickname());
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

  // ===== 이미지 관리 =====
  
  // 프로필 이미지 업로드/수정 (썸네일 + 메인)
  public void updateProfileImage(Long memberId, MultipartFile imageFile) {
    MemberDetail memberDetail = findByMemberId(memberId);
    
    try {
      // 기존 이미지 삭제
      if (memberDetail.getThumbnailImagePath() != null || memberDetail.getMainImagePath() != null) {
        imageService.deletePhysicalFiles(memberDetail.getThumbnailImagePath(), memberDetail.getMainImagePath());
      }
      
      // 새 이미지 업로드
      String mainImagePath = imageService.uploadSingleImage(imageFile, ImageTarget.MEMBER_PROFILE, "main");
      String thumbnailImagePath = imageService.uploadThumbnail(imageFile, ImageTarget.MEMBER_PROFILE);
      
      // MemberDetail 업데이트
      memberDetail.setMainImagePath(mainImagePath);
      memberDetail.setThumbnailImagePath(thumbnailImagePath);
      
    } catch (Exception e) {
      throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
    }
  }

  // 배경 이미지 업로드/수정
  public void updateBackgroundImage(Long memberId, MultipartFile imageFile) {
    MemberDetail memberDetail = findByMemberId(memberId);
    
    try {
      // 기존 이미지 삭제
      if (memberDetail.getBackgroundImagePath() != null) {
        imageService.deletePhysicalFiles(memberDetail.getBackgroundImagePath());
      }
      
      // 새 이미지 업로드 (배경은 메인만)
      String backgroundImagePath = imageService.uploadSingleImage(imageFile, ImageTarget.MEMBER_BACKGROUND, null);
      
      // MemberDetail 업데이트
      memberDetail.setBackgroundImagePath(backgroundImagePath);
      
    } catch (Exception e) {
      throw new RuntimeException("배경 이미지 업로드 실패: " + e.getMessage(), e);
    }
  }

  // 프로필 이미지 삭제
  public void deleteProfileImage(Long memberId) {
    MemberDetail memberDetail = findByMemberId(memberId);
    
    // 물리적 파일 삭제
    if (memberDetail.getThumbnailImagePath() != null || memberDetail.getMainImagePath() != null) {
      imageService.deletePhysicalFiles(memberDetail.getThumbnailImagePath(), memberDetail.getMainImagePath());
    }
    
    // DB 필드 초기화
    memberDetail.setThumbnailImagePath(null);
    memberDetail.setMainImagePath(null);
  }

  // 배경 이미지 삭제
  public void deleteBackgroundImage(Long memberId) {
    MemberDetail memberDetail = findByMemberId(memberId);
    
    // 물리적 파일 삭제
    if (memberDetail.getBackgroundImagePath() != null) {
      imageService.deletePhysicalFiles(memberDetail.getBackgroundImagePath());
    }
    
    // DB 필드 초기화
    memberDetail.setBackgroundImagePath(null);
  }

}
