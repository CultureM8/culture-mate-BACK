package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.TicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice, Long> {
  Optional<TicketPrice> findByEventAndTicketType(Event event, String ticketType);

  List<TicketPrice> findByEvent(Event event);
}
