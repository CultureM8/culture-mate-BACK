package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.event.EventType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {

  @NotNull(message = "이벤트 유형은 필수입니다.")
  private EventType eventType;
  
  @NotBlank(message = "이벤트 제목은 필수입니다.")
  private String title;
  
  @NotNull(message = "지역 정보는 필수입니다.")
  private RegionDto regionDto;
  
  @NotBlank(message = "장소명은 필수입니다.")
  private String eventLocation;
  
  private String address;
  private String addressDetail;
  
  @NotNull(message = "시작일은 필수입니다.")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;
  
  @NotNull(message = "종료일은 필수입니다.")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;
  
  @Min(value = 0, message = "소요시간은 0분 이상이어야 합니다.")
  private Integer durationMin;
  
  @Min(value = 0, message = "최소 연령은 0세 이상이어야 합니다.")
  private Integer minAge;
  
  @NotBlank(message = "이벤트 설명은 필수입니다.")
  private String description;

  private List<TicketPriceDto> ticketPriceDto;

}
