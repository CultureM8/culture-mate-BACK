package com.culturemate.culturemate_api.domain.event;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.member.InterestEvents;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Event")
public class Event {
  @Id
  @GeneratedValue
  @Column(name = "event_id")
  private Long id;

  private EventType eventType;     // 이벤트 유형
  private String title;        // 이벤트 제목

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = false)
  private Region region;     // 지역ID

  private String eventLocation;    // 장소명
  private String address;           // 도로명주소
  private String addressDetail;    // 상세주소
  private LocalDate startDate;     // 시작일
  private LocalDate endDate;       // 종료일
  private Integer durationMin;     // 예상 소요시간
  private Integer minAge;          // 최소 연령
  private String content;           // 내용
  private BigDecimal avgRating;    // 별점
  private Integer interestCount;   // 관심수

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TicketPrice> ticketPrice = new ArrayList<>();
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EventReview> eventReview = new ArrayList<>();
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Board> board = new ArrayList<>();
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InterestEvents> interestEvents = new ArrayList<>();
}
