package com.culturemate.culturemate_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TogetherRequestDto {

  private Long eventId;
  private Long hostId;
  private String title;
  private RegionDto regionDto;
  private String address;
  private String addressDetail;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate meetingDate;
  private Integer maxParticipants;
  private String content;

}
