package com.culturemate.culturemate_api.domain.together;

import com.culturemate.culturemate_api.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Participants {

  //=== 필드 ===//
  @Id @GeneratedValue
  @Column(name = "participant_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "together_id")
  private Together together;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member participant;

  // 동행 요청 승인/거절
  @Setter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ParticipationStatus status = ParticipationStatus.PENDING;

}
