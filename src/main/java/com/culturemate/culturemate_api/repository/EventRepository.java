package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

//  통합 검색
  @EntityGraph(attributePaths = {"region", "ticketPrice"})
  @Query("SELECT e FROM Event e WHERE " +
         "(:keyword IS NULL OR :keyword = '' OR e.title LIKE CONCAT('%', :keyword, '%')) AND " +
         "(:regions IS NULL OR e.region IN :regions) AND " +
         "(:startDate IS NULL OR e.endDate >= :startDate) AND " +
         "(:endDate IS NULL OR e.startDate <= :endDate) AND " +
         "(:eventType IS NULL OR e.eventType = :eventType)")
  List<Event> findBySearch(@Param("keyword") String keyword,
                          @Param("regions") List<Region> regions,
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate,
                          @Param("eventType") EventType eventType);
}
