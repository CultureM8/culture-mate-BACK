package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.event.TicketPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketPriceDto {

  private Long eventId;
  private String ticketType;
  private Integer price;

}
