package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.event.EventReview;
import com.culturemate.culturemate_api.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventReviewResponseDto {

  private Long id;
  private Long eventId;
  private MemberDto.ProfileResponse author;
  private Integer rating;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static EventReviewResponseDto from(EventReview eventReview) {
    return EventReviewResponseDto.builder()
      .id(eventReview.getId())
      .eventId(eventReview.getEvent().getId())
      .author(MemberDto.ProfileResponse.from(eventReview.getMember()))
      .rating(eventReview.getRating())
      .content(eventReview.getContent())
      .createdAt(eventReview.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
      .updatedAt(eventReview.getUpdatedAt() != null ? 
                 eventReview.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
      .build();
  }

}