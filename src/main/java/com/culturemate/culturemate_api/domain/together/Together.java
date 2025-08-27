package com.culturemate.culturemate_api.domain.together;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
public class Together {

  //=== 필드 ===//
  @Id @GeneratedValue
  @Column(name = "together_id")
  private long id;

  @ManyToOne
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  @ManyToOne
  @JoinColumn(name = "host_id")
  private Member host;

  // OneToMany는 실제 저장되는 속성이 아니고, 관계 매핑용
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "together")
  private List<Participants> participants;

  @Column(nullable = false)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = false)
  private Region region;

  @Column(nullable = false)
  private String address;          // 기본 주소 (도로명 주소)
  @Column(nullable = false)
  private String addressDetail;    // 상세 주소

  private LocalDate meetingDate;

  private int maxParticipants;
  private int currentParticipants;

  @Column(length = 2000)
  private String content;

  private boolean isRecruiting = true;

  private int interestCount = 0;

  private Instant createdAt;
  private Instant updatedAt;

  //=== 메서드 ===//
  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = Instant.now();
  }

  public int getCurrentParticipants() {
    return participants.size();
  }

}
