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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {
  @Id
  @GeneratedValue
  @Column(name = "event_id")
  private Long id;

  @Column(nullable = false)
  private EventType eventType;     // 이벤트 유형

  @Column(nullable = false)
  private String title;            // 이벤트 이름

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = false)
  private Region region;           // 지역ID
  @Column(nullable = false)
  private String eventLocation;    // 장소명
  // 정확한 주소는 영화관 같은건 없을 수 있음
  private String address;          // 도로명주소
  private String addressDetail;    // 상세주소

  @Column(nullable = false)
  private LocalDate startDate;     // 시작일
  @Column(nullable = false)
  private LocalDate endDate;       // 종료일

  private Integer durationMin;     // 예상 소요시간
  private Integer minAge = 0;      // 최소 연령
  @Column(nullable = false)
  private String description;      // 요약설명

  private BigDecimal avgRating = BigDecimal.ZERO;    // 평균 별점
  @Setter
  private Integer reviewCount = 0;                   // 리뷰 수
  @Setter
  private Integer interestCount = 0;                 // 관심수

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TicketPrice> ticketPrice = new ArrayList<>();

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EventReview> eventReview = new ArrayList<>();
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Board> board = new ArrayList<>();
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
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
