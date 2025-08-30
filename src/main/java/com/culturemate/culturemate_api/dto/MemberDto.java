package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
  private Long id;             // Response 시 필요
  private String loginId;      // Request + Response
  private String password;     // Request 시 필요, Response 시는 null 처리 권장
  private Role role;           // Response 또는 Request에 따라 설정
  private MemberStatus status; // Response용, 생성 시 기본값 세팅 가능
}