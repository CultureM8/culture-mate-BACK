package com.culturemate.culturemate_api.domain.member;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberDetail {
  @Id
  @Column(name = "member_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId // member도메인과 완전히 동일한 id값 매핑
  @JoinColumn(name = "member_id")
  private Member member;

  private String nickname;
  private Long profileImageId;
  private Long backgroundImageId;
  private String intro;
  private String mbti;
  private Integer togetherScore;
  private String email;

  @Enumerated(EnumType.STRING)
  private VisibleType visibility;

  @Column(nullable = false)
  private Instant createdAt;
  private Instant updatedAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = Instant.now();
  }

}

