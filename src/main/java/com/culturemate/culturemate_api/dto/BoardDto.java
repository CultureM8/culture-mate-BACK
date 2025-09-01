package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.event.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
  private Long id;
  private String title;
  private String content;
  private Long authorId;
  private String authorLoginId;
  private Long eventId;
  private EventType eventType;
  private Integer likeCount;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate updatedAt;
}
