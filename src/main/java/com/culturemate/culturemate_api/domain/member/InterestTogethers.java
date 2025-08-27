package com.culturemate.culturemate_api.domain.member;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.together.Together;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class InterestTogethers {
  @Id
  @GeneratedValue
  @Column(name = "interest_together_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "together_id")
  private Together together;

  public InterestTogethers(Member member, Together together){
    this.member = member;
    this.together = together;
  }
}
