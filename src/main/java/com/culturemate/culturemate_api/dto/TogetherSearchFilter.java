package com.culturemate.culturemate_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TogetherSearchFilter {
  private String level1;
  private String level2;
  private String level3;
  private LocalDate startDate;
  private LocalDate endDate;
  private String eventType;

  public boolean hasRegion() {
    return level1 != null && 
           !level1.trim().isEmpty() && 
           !"전체".equals(level1.trim());
  }
  
  public boolean hasDateRange() {
    return startDate != null || endDate != null;
  }
  
  public boolean hasEventType() {
    return eventType != null && !eventType.trim().isEmpty();
  }
}