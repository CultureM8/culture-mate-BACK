package com.culturemate.culturemate_api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

  @NotNull(message = "이벤트 ID는 필수입니다.")
  private Long eventId;
  
  @NotNull(message = "호스트 ID는 필수입니다.")
  private Long hostId;
  
  @NotBlank(message = "제목은 필수입니다.")
  @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
  private String title;
  
  @NotNull(message = "지역 정보는 필수입니다.")
  private RegionDto regionDto;
  
  @NotBlank(message = "주소는 필수입니다.")
  @Size(max = 255, message = "주소는 255자를 초과할 수 없습니다.")
  private String address;
  
  @NotBlank(message = "상세주소는 필수입니다.")
  @Size(max = 255, message = "상세주소는 255자를 초과할 수 없습니다.")
  private String addressDetail;
  
  @NotNull(message = "모임 날짜는 필수입니다.")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate meetingDate;
  
  @NotNull(message = "최대 참여자 수는 필수입니다.")
  @Min(value = 2, message = "최대 참여자 수는 2명 이상이어야 합니다.")
  @Max(value = 100, message = "최대 참여자 수는 100명을 초과할 수 없습니다.")
  private Integer maxParticipants;
  
  @Size(max = 2000, message = "내용은 2000자를 초과할 수 없습니다.")
  private String content;

}
