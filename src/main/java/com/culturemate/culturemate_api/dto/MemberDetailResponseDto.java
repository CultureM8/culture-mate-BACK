package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.member.MemberDetail;
import com.culturemate.culturemate_api.domain.together.VisibleType;
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
  private String userName;
  private Long profileImageId;
  private Long backgroundImageId;
  private String intro;
  private String MBTI;
  private Integer togetherScore;
  private String email;
  private VisibleType visibility;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static MemberDetailResponseDto from(MemberDetail memberDetail) {
    return MemberDetailResponseDto.builder()
      .id(memberDetail.getId())
      .userName(memberDetail.getUserName())
      .profileImageId(memberDetail.getProfileImageId())
      .backgroundImageId(memberDetail.getBackgroundImageId())
      .intro(memberDetail.getIntro())
      .MBTI(memberDetail.getMBTI())
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
