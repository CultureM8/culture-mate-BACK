package com.culturemate.culturemate_api.domain.event;

import com.culturemate.culturemate_api.domain.member.Member;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private Member member;

  @Column(nullable = false)
  private Integer rating;

  private String content;

  //=== 검증 로직 ===//
  public void setRating(Integer rating) {
    if(rating > 5) {
      this.rating = 5;
    } else if(rating < 1) {
      this.rating = 1;
    } else {
      this.rating = rating;
    }
  }

}
