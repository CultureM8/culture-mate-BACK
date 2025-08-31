package com.culturemate.culturemate_api.domain.member;

import com.culturemate.culturemate_api.domain.together.VisibleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail {
  @Id
  @Column(name = "member_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId // member도메인과 완전히 동일한 id값 매핑
  @JoinColumn(name = "member_id")
  private Member member;

  private String user_name;
  private Long profile_image_id;
  private Long background_image_id;
  private String intro;
  private String MBTI;
  private Integer together_score;
  private String email;

  @Enumerated(EnumType.STRING)
  private VisibleType visibility;

}

