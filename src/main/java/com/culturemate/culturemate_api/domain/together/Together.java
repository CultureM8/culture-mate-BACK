package com.culturemate.culturemate_api.domain.together;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.member.InterestEvents;
import com.culturemate.culturemate_api.domain.member.InterestTogethers;
import com.culturemate.culturemate_api.domain.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
  @NotNull(message = "이벤트는 필수입니다")
  @Setter
  private Event event;

  @ManyToOne
  @JoinColumn(name = "host_id")
  @NotNull(message = "호스트는 필수입니다")
  private Member host;

  // OneToMany는 실제 저장되는 속성이 아니고, 관계 매핑용
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "together", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Participants> participants;

  @Column(nullable = false)
  @NotBlank(message = "제목은 필수입니다")
  @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
  @Setter
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = false)
  @NotNull(message = "지역은 필수입니다")
  @Setter
  private Region region;

  @Column(nullable = false)
  @NotBlank(message = "주소는 필수입니다")
  @Size(max = 255, message = "주소는 255자를 초과할 수 없습니다")
  @Setter
  private String address;          // 기본 주소 (도로명 주소)
  
  @Column(nullable = false)
  @NotBlank(message = "상세주소는 필수입니다")
  @Size(max = 255, message = "상세주소는 255자를 초과할 수 없습니다")
  @Setter
  private String addressDetail;    // 상세 주소

  @NotNull(message = "모임 날짜는 필수입니다")
  @Setter
  private LocalDate meetingDate;

  @NotNull(message = "최대 참여자 수는 필수입니다")
  @Min(value = 2, message = "최대 참여자 수는 2명 이상이어야 합니다")
  @Max(value = 100, message = "최대 참여자 수는 100명을 초과할 수 없습니다")
  @Setter
  private Integer maxParticipants;

  @Column(length = 2000)
  @Size(max = 2000, message = "내용은 2000자를 초과할 수 없습니다")
  @Setter
  private String content;

  @Setter
  private boolean isRecruiting = true;

  private Integer interestCount = 0;

  private Instant createdAt;
  private Instant updatedAt;

  @OneToMany(mappedBy = "together", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InterestTogethers> interestTogethers = new ArrayList<>();

  @Formula("(SELECT COUNT(*) FROM participants p WHERE p.together_id = id)")
  private Integer participantCount;


  //=== 비즈니스 로직 ===//
  public Integer getCurrentParticipantsCount() {
    return participantCount != null ? participantCount : 0;
  }

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
