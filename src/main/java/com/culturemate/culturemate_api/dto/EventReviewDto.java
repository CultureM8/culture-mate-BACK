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

public class EventReviewDto {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {

  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response {

  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ResponseDetail {

  }

}