package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.dto.EventSearchFilter;
import com.culturemate.culturemate_api.repository.EventRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional
public class EventService {

  private final EventRepository eventRepository;
  private final RegionService regionService;

  public Event create(Event event) {
    return eventRepository.save(event);
  }

  @Transactional(readOnly = true)
  public Event read(Long eventId) {
    return eventRepository.findById(eventId).orElse(null);
  }

  @Transactional(readOnly = true)
  public List<Event> readAll() {
    return eventRepository.findAll();
  }

  // OverRide 객체기반 검색
  @Transactional(readOnly = true)
  public List<Event> readByRegion(Region region) {
    List<Region> regionList
      = regionService.readByCondition(region.getLevel1(), region.getLevel2(), region.getLevel3());
    return eventRepository.findByRegion(regionList);
  }
  // OverRide 문자기반 검색
  @Transactional(readOnly = true)
  public List<Event> readByRegion(String level1, String level2, String level3) {
    List<Region> regionList = regionService.readByCondition(level1, level2, level3);
    return eventRepository.findByRegion(regionList);
  }

  @Transactional(readOnly = true)
  public List<Event> readByType(EventType type) {
    return eventRepository.findByEventType(type);
  }

  @Transactional(readOnly = true)
  public List<Event> readByTitle(String title) {
    return eventRepository.findByTitleContaining(title);
  }

  @Transactional(readOnly = true)
  public List<Event> readByPeriodBetween(LocalDate start, LocalDate end) {
    return eventRepository.findByPeriodBetween(start, end);
  }

  @Transactional(readOnly = true)
  public List<Event> readByFilters(EventSearchFilter filter) {
    List<Region> regions = null;
    if (filter.hasRegion()) {
      regions = regionService.readByCondition(
        filter.getLevel1(),
        filter.getLevel2(),
        filter.getLevel3()
      );
    }
    
    EventType eventType = null;
    if (filter.hasEventType()) {
      eventType = EventType.valueOf(filter.getEventType());
    }
    
    return eventRepository.findByFilters(
      regions,
      filter.getStartDate(),
      filter.getEndDate(),
      eventType
    );
  }

  public void update(Event newEvent) {
    if(!eventRepository.existsById(newEvent.getId())) {
      throw new IllegalArgumentException("이벤트가 존재하지 않습니다.");
    }
    eventRepository.save(newEvent);
  }

  public void delete(Long eventId) {
    eventRepository.deleteById(eventId);
  }

}
