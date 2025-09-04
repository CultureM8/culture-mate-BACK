package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.member.MemberDetail;
import com.culturemate.culturemate_api.domain.member.VisibleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailResponseDto {
  private Long id;
  private String nickname;
  private String profileImagePath;
  private String backgroundImagePath;
  private String intro;
  private String mbti;
  private Integer togetherScore;
  private String email;
  private VisibleType visibility;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static MemberDetailResponseDto from(MemberDetail memberDetail) {
    return MemberDetailResponseDto.builder()
      .id(memberDetail.getId())
      .nickname(memberDetail.getNickname())
      .profileImagePath(memberDetail.getMainImagePath())
      .backgroundImagePath(memberDetail.getBackgroundImagePath())
      .intro(memberDetail.getIntro())
      .mbti(memberDetail.getMbti())
      .togetherScore(memberDetail.getTogetherScore())
      .email(memberDetail.getEmail())
      .visibility(memberDetail.getVisibility())
      .createdAt(memberDetail.getCreatedAt() != null ? 
                 memberDetail.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
      .updatedAt(memberDetail.getUpdatedAt() != null ? 
                 memberDetail.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
      .build();
  }
}
