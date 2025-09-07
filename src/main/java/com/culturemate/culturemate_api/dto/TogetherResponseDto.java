package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.together.Participants;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.repository.ParticipantsRepository;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TogetherResponseDto {

  private long id;
  private Long eventId;
  private Long hostId;
  private String title;
  private RegionDto regionDto;
  private String address;
  private String addressDetail;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate meetingDate;
  private Integer maxParticipants;
  private Integer currentParticipants;
  private String content;
  private Boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static TogetherResponseDto from(Together together) {
    return TogetherResponseDto.builder()
      .id(together.getId())
      .eventId(together.getEvent().getId())
      .hostId(together.getHost().getId())
      .title(together.getTitle())
      .regionDto(RegionDto.from(together.getRegion()))
      .address(together.getAddress())
      .addressDetail(together.getAddressDetail())
      .meetingDate(together.getMeetingDate())
      .maxParticipants(together.getMaxParticipants())
      .currentParticipants(together.getParticipantCount())
      .content(together.getContent())
      .active(false) // 임시값, TogetherService에서 isActive() 계산 후 설정
      .createdAt(together.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
      .updatedAt(together.getUpdatedAt() != null ? 
                 together.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
      .build();
  }

}
