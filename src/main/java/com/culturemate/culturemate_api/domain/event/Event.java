package com.culturemate.culturemate_api.domain.event;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.member.InterestEvents;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {
  @Id @GeneratedValue
  @Column(name = "event_id")
  private Long id;

  @Setter
  @Column(nullable = false)
  private EventType eventType;     // 이벤트 유형

  @Setter
  @Column(nullable = false)
  private String title;            // 이벤트 이름

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = false)
  private Region region;           // 지역ID

  @Setter
  @Column(nullable = false)
  private String eventLocation;    // 장소명
  // 정확한 주소는 영화관 같은건 없을 수 있음
  @Setter
  private String address;          // 도로명주소
  @Setter
  private String addressDetail;    // 상세주소

  @Setter
  @Column(nullable = false)
  private LocalDate startDate;     // 시작일
  @Setter
  @Column(nullable = false)
  private LocalDate endDate;       // 종료일

  @Setter
  private Integer durationMin;     // 예상 소요시간
  @Setter
  @Builder.Default
  private Integer minAge = 0;      // 최소 연령
  @Setter
  @Column(nullable = false)
  private String description;      // 요약설명

  @Builder.Default
  private BigDecimal avgRating = BigDecimal.ZERO;    // 평균 별점
  @Setter
  @Builder.Default
  private Integer reviewCount = 0;                   // 리뷰 수
  @Setter
  @Builder.Default
  private Integer interestCount = 0;                 // 관심수

  // 가격정보는 연결용 ToMany가 아니라 필수적인 정보
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<TicketPrice> ticketPrice = new ArrayList<>();

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<EventReview> eventReview = new ArrayList<>();
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Board> board = new ArrayList<>();
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<InterestEvents> interestEvents = new ArrayList<>();

  // 관리용 필드
  private Instant createdAt;
  private Instant updatedAt;


  //=== 생성/수정 로직 ===//
  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = Instant.now();
  }

  public void updateAvgRating(BigDecimal newAvgRating, Integer newReviewCount) {
    this.avgRating = newAvgRating != null ? newAvgRating : BigDecimal.ZERO;
    this.reviewCount = newReviewCount != null ? newReviewCount : 0;
  }

  public void recalculateAvgRating() {
    if (eventReview.isEmpty()) {
      this.avgRating = BigDecimal.ZERO;
      return;
    }

    BigDecimal sum = eventReview.stream()
        .map(EventReview::getRating)
        .map(BigDecimal::valueOf)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    this.avgRating = sum.divide(BigDecimal.valueOf(eventReview.size()), 2, java.math.RoundingMode.HALF_UP);
  }

}
