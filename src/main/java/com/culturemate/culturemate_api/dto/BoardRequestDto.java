package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.event.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDto {
  
  @NotBlank(message = "제목은 필수입니다.")
  private String title;
  
  @NotBlank(message = "내용은 필수입니다.")
  private String content;
  
  @NotNull(message = "작성자 정보는 필수입니다.")
  private Long authorId;
  
  private EventType eventType;
  
  private Long eventId;
}