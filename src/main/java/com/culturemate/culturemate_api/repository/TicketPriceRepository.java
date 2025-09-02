package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.event.TicketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketPriceRepository extends JpaRepository<TicketPrice, Long> {
}
