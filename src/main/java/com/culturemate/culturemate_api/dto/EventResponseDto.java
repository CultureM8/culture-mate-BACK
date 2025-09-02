package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {

  private Long id;
  private EventType eventType;
  private String title;
  private RegionDto regionDto;
  private String eventLocation;
  private String address;
  private String addressDetail;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;
  private Integer durationMin;
  private Integer minAge;
  private String description;
  private BigDecimal avgRating;
  private Integer reviewCount;
  private Integer interestCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static EventResponseDto from(Event event) {
    return EventResponseDto.builder()
      .id(event.getId())
      .eventType(event.getEventType())
      .title(event.getTitle())
      .regionDto(RegionDto.from(event.getRegion()))
      .eventLocation(event.getEventLocation())
      .address(event.getAddress())
      .addressDetail(event.getAddressDetail())
      .startDate(event.getStartDate())
      .endDate(event.getEndDate())
      .durationMin(event.getDurationMin())
      .minAge(event.getMinAge())
      .description(event.getDescription())
      .avgRating(event.getAvgRating())
      .reviewCount(event.getReviewCount())
      .interestCount(event.getInterestCount())
      .createdAt(event.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
      .updatedAt(event.getUpdatedAt() != null ? 
                 event.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
      .build();
  }

}
