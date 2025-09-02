package com.culturemate.culturemate_api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventReviewRequestDto {
  
  @NotNull(message = "이벤트 ID는 필수입니다")
  private Long eventId;
  
  @NotNull(message = "작성자 ID는 필수입니다")
  private Long memberId;
  
  @NotNull(message = "평점은 필수입니다")
  @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
  @Max(value = 5, message = "평점은 5점 이하여야 합니다")
  private Integer rating;
  
  private String content;  // null 허용 (리뷰 내용은 선택사항)

}