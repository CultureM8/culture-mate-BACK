package com.culturemate.culturemate_api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
public class Together {

  @Id @GeneratedValue
  @Column(name = "together_id")
  private long id;

//  @ManyToOne
//  @JoinColumn(name = "event_id")
//  private Event event;

  @ManyToOne
  @JoinColumn(name = "host_id")
  private Member host;

  // OneToMany는 실제 저장되는 속성이 아니고, 관계 매핑용
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "together")
  private List<Participants> participants;

  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id")
  private Region region;

  private String meetingLocation;  // 모임 장소명
  private String address;           // 기본 주소 (도로명 주소)
  private String addressDetail;    // 상세 주소

  private LocalDate meetingDate;

  private int maxParticipants;
  private int currentParticipants;

  @Column(length = 2000)
  private String content;

  private boolean isRecruiting = true;

  private int interestCount = 0;

  private Instant createdAt;

  private Instant modifiedAt;

}
