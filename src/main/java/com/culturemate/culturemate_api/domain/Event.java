package com.culturemate.culturemate_api.domain;

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

  private EventType event_type;     // 이벤트 유형
  private String event_name;        // 이벤트 이름
  private Long region_id;           // 지역ID
  private String event_location;    // 장소명
  private String address;           // 도로명주소
  private String address_detail;    // 상세주소
  private LocalDate start_date;     // 시작일
  private LocalDate end_date;       // 종료일
  private Integer duration_min;     // 예상 소요시간
  private Integer min_age;          // 최소 연령
  private String content;           // 내용
  private BigDecimal avg_rating;    // 별점
  private Integer interest_count;   // 관심수

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TicketPrice> ticketPrice = new ArrayList<>();
  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EventReview> eventReview = new ArrayList<>();
}
