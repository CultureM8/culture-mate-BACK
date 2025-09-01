package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.member.MemberDetail;
import com.culturemate.culturemate_api.domain.together.VisibleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailDto {
  private Long id;
  private String userName;
  private Long profileImageId;
  private Long backgroundImageId;
  private String intro;
  private String MBTI;
  private Integer togetherScore;
  private String email;
  private VisibleType visibility;

  public static MemberDetailDto fromEntity(MemberDetail memberDetail) {
    return MemberDetailDto.builder()
      .id(memberDetail.getId())
      .userName(memberDetail.getUser_name())
      .profileImageId(memberDetail.getProfile_image_id())
      .backgroundImageId(memberDetail.getBackground_image_id())
      .intro(memberDetail.getIntro())
      .MBTI(memberDetail.getMBTI())
      .togetherScore(memberDetail.getTogether_score())
      .email(memberDetail.getEmail())
      .visibility(memberDetail.getVisibility())
      .build();
  }
}
