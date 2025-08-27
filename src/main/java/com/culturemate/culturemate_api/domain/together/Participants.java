package com.culturemate.culturemate_api.domain.together;

import com.culturemate.culturemate_api.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Participants {

  //=== 필드 ===//
  @Id @GeneratedValue
  @Column(name = "participant_id")
  private Long id;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "together_id")
  private Together together;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member participant;

}
