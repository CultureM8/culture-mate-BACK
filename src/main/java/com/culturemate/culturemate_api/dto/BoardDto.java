package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.event.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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


}
