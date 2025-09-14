package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberDetail;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.domain.member.Role;
import com.culturemate.culturemate_api.domain.member.VisibleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class MemberDto {

  @Getter
  @Setter
  public static class Login {
    private String loginId;
    private String password;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Register {
    
    @NotBlank
    @Size(min = 4, max = 20, message = "로그인 아이디는 4자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "로그인 아이디는 영문, 숫자, 언더스코어만 사용 가능합니다.")
    private String loginId;
    
    @NotBlank
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;
    
    @NotBlank
    private String nickname;

    private String intro;
    private String mbti;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "MemberDetailRequest")
  public static class DetailRequest {
    
    @NotBlank(message = "사용자명은 필수입니다")
    private String nickname;
    
    private String intro;
    private String mbti;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    private VisibleType visibility;
    
    public static DetailRequest from(Register registerRequest) {
      return DetailRequest.builder()
        .nickname(registerRequest.getNickname())
        .intro(registerRequest.getIntro())
        .mbti(registerRequest.getMbti())
        .email(registerRequest.getEmail())
        .build();
    }

  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "MemberResponse", description = "회원 상세 정보 응답 DTO")
  public static class Response {
    private Long id;
    private String loginId;
    private Role role;
    private MemberStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Response from(Member member) {
      return Response.builder()
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

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "MemberProfileResponse", description = "회원 프로필 정보 응답 DTO")
  public static class ProfileResponse {
    private Long id;
    private String nickname;
    private String thumbnailImagePath;
    private String intro;

    public static ProfileResponse from(Member member) {
      MemberDetail detail = member.getMemberDetail();
      return ProfileResponse.builder()
        .id(member.getId())
        .nickname(detail != null ? detail.getNickname() : null)
        .thumbnailImagePath(detail != null ? detail.getThumbnailImagePath() : null)
        .intro(detail != null ? detail.getIntro() : null)
        .build();
    }
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(name = "MemberDetailResponse", description = "회원 상세 정보 응답 DTO")
  public static class DetailResponse {
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

    public static DetailResponse from(MemberDetail memberDetail) {
      return DetailResponse.builder()
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
}