package com.culturemate.culturemate_api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
  
  @NotBlank
  @Size(min = 4, max = 20, message = "로그인 아이디는 4자 이상 20자 이하여야 합니다.")
  @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "로그인 아이디는 영문, 숫자, 언더스코어만 사용 가능합니다.")
  private String loginId;
  
  @NotBlank
  @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다.")
//  @Pattern 비밀번호 특수문자 등
  private String password;
  
  // MemberDetail
  @NotBlank
  private String nickname;

//  private Long profileImageId;
//  private Long backgroundImageId;
  private String intro;
  private String mbti;

  @Email(message = "올바른 이메일 형식이 아닙니다")
  private String email;

}