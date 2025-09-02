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

  private Long id;  // 수정 시 사용할 ID (새 생성 시에는 null)
  private Long eventId;
  private String ticketType;
  private Integer price;

}
