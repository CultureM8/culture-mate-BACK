package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.dto.EventSearchDto;
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
@Transactional(readOnly=true)
public class EventService {

  private final EventRepository eventRepository;
  private final RegionService regionService;

  @Transactional
  public Event create(Event event) {
    return eventRepository.save(event);
  }

  public Event read(Long eventId) {
    return eventRepository.findById(eventId).orElse(null);
  }

  public List<Event> readAll() {
    return eventRepository.findAll();
  }

  // 새로운 통합 검색 메서드
  public List<Event> search(EventSearchDto searchDto) {
    List<Region> regions = null;
    if (searchDto.hasRegion()) {
      regions = regionService.readByCondition(
        searchDto.getLevel1(),
        searchDto.getLevel2(),
        searchDto.getLevel3()
      );
    }
    
    EventType eventType = null;
    if (searchDto.hasEventType()) {
      eventType = EventType.valueOf(searchDto.getEventType().toUpperCase());
    }
    
    // 새로운 통합 검색 Repository 메서드 사용
    return eventRepository.findBySearch(
      searchDto.hasTitle() ? searchDto.getTitle() : null,
      regions,
      searchDto.getStartDate(),
      searchDto.getEndDate(),
      eventType
    );
  }


  @Transactional
  public void update(Event newEvent) {
    if(!eventRepository.existsById(newEvent.getId())) {
      throw new IllegalArgumentException("해당 이벤트가 존재하지 않습니다.");
    }
    eventRepository.save(newEvent);
  }

  @Transactional
  public void delete(Long eventId) {
    eventRepository.deleteById(eventId);
  }

}
