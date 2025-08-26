package com.culturemate.culturemate_api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "EVENT_REVIEW")
public class EventReview {
  @Id
  @GeneratedValue
  @Column(name = "review_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id" ,nullable = false)
  private Event event;

  private Long author_id;

  private Integer rating;
  private String content;


}
