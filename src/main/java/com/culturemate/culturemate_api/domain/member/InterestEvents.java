package com.culturemate.culturemate_api.domain.member;

import com.culturemate.culturemate_api.domain.event.Event;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class InterestEvents {
  @Id
  @GeneratedValue
  @Column(name = "interest_event_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  private Event event;

  public InterestEvents(Member member, Event event){
    this.member = member;
    this.event = event;
  }

}
