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
  private Long id;
  private String loginId;
  private Role role;
  private MemberStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Entity -> ResponseDto 변환 (Response 전용)
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