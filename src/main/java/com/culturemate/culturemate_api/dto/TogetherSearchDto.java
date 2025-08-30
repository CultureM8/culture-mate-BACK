package com.culturemate.culturemate_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TogetherSearchDto {

  // 검색어
  private String keyword;
  
  // 지역 검색
  private String level1;
  private String level2;
  private String level3;
  
  // 날짜 범위 검색
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;
  
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;
  
  // 이벤트 타입 검색
  private String eventType;

  // 특정 이벤트 검색
  private Long eventId;

  // 검증 및 유틸리티 메서드들
  public boolean hasKeyword() {
    return keyword != null && !keyword.trim().isEmpty();
  }
  
  public boolean hasRegion() {
    return level1 != null && 
           !level1.trim().isEmpty() && 
           !"전체".equals(level1.trim());
  }
  
  public boolean hasDateRange() {
    return startDate != null || endDate != null;
  }

  public boolean hasEventId() {
    return eventId != null;
  }

  public boolean hasEventType() {
    // 특정 이벤트를 지정하면 이벤트 타입에 대한 필터는 안함.
    if (this.hasEventId()) {
      return false;
    }
    return eventType != null && !eventType.trim().isEmpty();
  }

  public boolean isEmpty() {
    return !hasKeyword() && !hasRegion() && !hasDateRange() && !hasEventType() && !hasEventId();
  }
  
  // 검색 조건 문자열 생성 (로깅용)
  public String getSearchConditions() {
    StringBuilder sb = new StringBuilder();
    if (hasKeyword()) sb.append("keyword:").append(keyword).append(" ");
    if (hasRegion()) sb.append("region:").append(level1).append(" ");
    if (hasDateRange()) sb.append("dates:").append(startDate).append("~").append(endDate).append(" ");
    if (hasEventType()) sb.append("type:").append(eventType).append(" ");
    if (hasEventId()) sb.append("eventId:").append(eventId).append(" ");
    return sb.toString().trim();
  }
}