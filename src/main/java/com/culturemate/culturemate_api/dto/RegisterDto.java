package com.culturemate.culturemate_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
  
  @NotBlank(message = "로그인 아이디는 필수입니다.")
  @Size(min = 4, max = 20, message = "로그인 아이디는 4자 이상 20자 이하여야 합니다.")
  @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "로그인 아이디는 영문, 숫자, 언더스코어만 사용 가능합니다.")
  private String loginId;
  
  @NotBlank(message = "비밀번호는 필수입니다.")
  @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다.")
  private String password;
  
  // 향후 확장 가능한 필드들 (필요시 주석 해제하여 사용)
  // private String email;
  // private String nickname; 
  // private String phoneNumber;
  // private LocalDate birthDate;
}