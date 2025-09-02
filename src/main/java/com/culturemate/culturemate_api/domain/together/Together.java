package com.culturemate.culturemate_api.domain.together;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.member.InterestEvents;
import com.culturemate.culturemate_api.domain.member.InterestTogethers;
import com.culturemate.culturemate_api.domain.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Together {

  //=== 필드 ===//
  @Id @GeneratedValue
  @Column(name = "together_id")
  private long id;

  @ManyToOne
  @JoinColumn(name = "event_id", nullable = false)
  @Setter
  private Event event;

  @ManyToOne
  @JoinColumn(name = "host_id", nullable = false)
  private Member host;

  // OneToMany는 실제 저장되는 속성이 아니고, 관계 매핑용
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "together", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Participants> participants = new ArrayList<>();

  @Column(nullable = false)
  @Setter
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = false)
  @Setter
  private Region region;

  @Column(nullable = false)
  @Setter
  private String address;          // 기본 주소 (도로명 주소)
  
  @Column(nullable = false)
  @Setter
  private String addressDetail;    // 상세 주소

  @Column(nullable = false)
  @Setter
  private LocalDate meetingDate;

  @Column(nullable = false)
  @Setter
  private Integer maxParticipants;

  //  @Formula("(SELECT COUNT(*) FROM participants p WHERE p.together_id = id)")
  @Setter
  @Builder.Default
  private Integer participantCount = 1; // 호스트 포함

  @Column(length = 2000)
  @Setter
  private String content;

  @Setter
  @Builder.Default
  private boolean isRecruiting = true;

  @Setter
  @Builder.Default
  private Integer interestCount = 0;

  private Instant createdAt;
  private Instant updatedAt;

  @OneToMany(mappedBy = "together", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<InterestTogethers> interestTogethers = new ArrayList<>();

  //=== 생성/수정 로직 ===//
  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = Instant.now();
  }

}
