package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.Region;
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

  public static RegionDto from(Region region) {
    return RegionDto.builder()
      .level1(region.getLevel1())
      .level2(region.getLevel2())
      .level3(region.getLevel3())
      .build();
  }
}