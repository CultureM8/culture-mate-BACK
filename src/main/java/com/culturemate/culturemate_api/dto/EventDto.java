package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class EventDto {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {
    @NotNull(message = "이벤트 유형은 필수입니다.")
    private EventType eventType;

    @NotBlank(message = "이벤트 이름은 필수입니다.")
    private String title;

    @Valid
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

    private Integer durationMin;
    private Integer minAge;

    private String description;

    private List<TicketPriceDto> ticketPriceDto;
    private List<String> imagesToDelete; // 수정 시 삭제할 이미지 경로들
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response {
    private Long id;
    private EventType eventType;
    private String title;
    private RegionDto regionDto;
    private String eventLocation;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String description;
    private String thumbnailImagePath;
    private BigDecimal avgRating;
    private Integer reviewCount;
    private Integer interestCount;

    public static Response from(Event event) {
      return Response.builder()
        .id(event.getId())
        .eventType(event.getEventType())
        .title(event.getTitle())
        .regionDto(RegionDto.from(event.getRegion()))
        .eventLocation(event.getEventLocation())
        .startDate(event.getStartDate())
        .endDate(event.getEndDate())
        .description(event.getDescription())
        .thumbnailImagePath(event.getThumbnailImagePath())
        .avgRating(event.getAvgRating())
        .reviewCount(event.getReviewCount())
        .interestCount(event.getInterestCount())
        .build();
    }
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ResponseDetail {
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
    private String thumbnailImagePath;
    private String mainImagePath;
    private List<String> contentImages;
    private BigDecimal avgRating;
    private Integer reviewCount;
    private Integer interestCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ResponseDetail from(Event event, List<String> contentImages) {
      return ResponseDetail.builder()
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
        .thumbnailImagePath(event.getThumbnailImagePath())
        .mainImagePath(event.getMainImagePath())
        .contentImages(contentImages)
        .avgRating(event.getAvgRating())
        .reviewCount(event.getReviewCount())
        .interestCount(event.getInterestCount())
        .createdAt(event.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
        .updatedAt(event.getUpdatedAt() != null ? 
                   event.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
        .build();
    }
  }

}