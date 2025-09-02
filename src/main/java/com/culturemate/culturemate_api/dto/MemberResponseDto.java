package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.domain.member.Role;
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
public class MemberResponseDto {
  private Long id;             // Response 시 필요
  private String loginId;      // Request + Response
  private String password;     // Request 시 필요, Response 시는 null 처리 권장
  private Role role;           // Response 또는 Request에 따라 설정
  private MemberStatus status; // Response용, 생성 시 기본값 세팅 가능
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // 간단한 Entity -> DTO 변환용 (컨트롤러에서 사용)
  public static MemberResponseDto from(Member member) {
    return MemberResponseDto.builder()
      .id(member.getId())
      .loginId(member.getLoginId())
      .role(member.getRole())
      .status(member.getStatus())
      .createdAt(member.getJoinedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
      .updatedAt(member.getUpdatedAt() != null ? 
                 member.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
      .build();
  }
}