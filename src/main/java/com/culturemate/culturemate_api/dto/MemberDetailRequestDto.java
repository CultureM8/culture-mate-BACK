package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.together.VisibleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDetailRequestDto {
  
  @NotNull(message = "회원 ID는 필수입니다")
  private Long memberId;
  
  @NotBlank(message = "사용자명은 필수입니다")
  private String userName;
  
  private Long profileImageId;
  private Long backgroundImageId;
  private String intro;
  private String MBTI;
  private Integer togetherScore;
  
  @Email(message = "올바른 이메일 형식이 아닙니다")
  private String email;
  
  @NotNull(message = "공개 설정은 필수입니다")
  private VisibleType visibility;

}