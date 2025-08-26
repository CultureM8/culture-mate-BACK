package com.culturemate.culturemate_api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "TICKET_PRICE")
public class TicketPrice {
  @Id
  @GeneratedValue
  @Column(name = "ticket_price_id")
  private Long id;

  private String ticket_type;   // 티켓타입
  private Integer price;        // 가격

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id" ,nullable = false)
  private Event event;
}
