package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.member.VisibleType;
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
  
  
  @NotBlank(message = "사용자명은 필수입니다")
  private String nickname;
  
//  private Long profileImageId;
//  private Long backgroundImageId;
  private String intro;
  private String mbti;

  @Email(message = "올바른 이메일 형식이 아닙니다")
  private String email;

  private VisibleType visibility;
  
  public static MemberDetailRequestDto from(RegisterDto registerDto) {
    return MemberDetailRequestDto.builder()
      .nickname(registerDto.getNickname())
      .intro(registerDto.getIntro())
      .mbti(registerDto.getMbti())
      .email(registerDto.getEmail())
      .build();
  }

}