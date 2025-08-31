package com.culturemate.culturemate_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionDto {
  
  private String level1;
  private String level2; 
  private String level3;
  
  public boolean hasRegion() {
    return level1 != null && 
           !level1.trim().isEmpty() && 
           !"전체".equals(level1.trim());
  }
}