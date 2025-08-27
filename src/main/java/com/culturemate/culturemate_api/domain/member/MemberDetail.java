package com.culturemate.culturemate_api.domain.member;

import com.culturemate.culturemate_api.domain.together.VisibleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="MEMBER_DETAIL")
public class MemberDetail {
  @Id
  @Column(name = "member_detail_id")
  private Long id;

  private String user_name;
  private Long profile_image_id;
  private Long background_image_id;
  private String intro;
  private String MBTI;
  private Integer together_score;
  private String email;

  @Enumerated(EnumType.STRING)
  private VisibleType visibility;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;
}

