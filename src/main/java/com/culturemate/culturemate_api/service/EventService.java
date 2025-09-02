package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.domain.event.TicketPrice;
import com.culturemate.culturemate_api.domain.member.InterestEvents;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.EventRequestDto;
import com.culturemate.culturemate_api.dto.EventSearchDto;
import com.culturemate.culturemate_api.dto.TicketPriceDto;
import com.culturemate.culturemate_api.repository.EventRepository;
import com.culturemate.culturemate_api.repository.InterestEventsRepository;
import com.culturemate.culturemate_api.repository.MemberRepository;
import com.culturemate.culturemate_api.repository.TicketPriceRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly=true)
public class EventService {

  private final EventRepository eventRepository;
  private final TicketPriceRepository ticketPriceRepository;
  private final InterestEventsRepository interestEventsRepository;
  private final RegionService regionService;
  private final MemberService memberService;

  @Transactional
  public Event create(EventRequestDto requestDto) {
    Region region = regionService.findExact(requestDto.getRegionDto());
    
    Event event = Event.builder()
      .eventType(requestDto.getEventType())
      .title(requestDto.getTitle())
      .region(region)
      .eventLocation(requestDto.getEventLocation())
      .address(requestDto.getAddress())
      .addressDetail(requestDto.getAddressDetail())
      .startDate(requestDto.getStartDate())
      .endDate(requestDto.getEndDate())
      .durationMin(requestDto.getDurationMin())
      .minAge(requestDto.getMinAge())
      .description(requestDto.getDescription())
      .build();

    eventRepository.save(event);

    for (TicketPriceDto dto : requestDto.getTicketPriceDto()) {
      TicketPrice newTicketPrice = TicketPrice.builder()
        .event(event)
        .ticketType(dto.getTicketType())
        .price(dto.getPrice())
        .build();
      ticketPriceRepository.save(newTicketPrice);
    }
      
    return event;
  }

  public Event findById(Long eventId) {
    return eventRepository.findById(eventId)
      .orElseThrow(() -> new IllegalArgumentException("해당 이벤트가 존재하지 않습니다."));
  }

  public List<Event> findAll() {
    return eventRepository.findAll();
  }

  // 새로운 통합 검색 메서드
  public List<Event> search(EventSearchDto searchDto) {
    List<Region> regions = null;
    if (searchDto.hasRegion()) {
      regions = regionService.findByCondition(searchDto.getRegionDto());
    }
    
    EventType eventType = null;
    if (searchDto.hasEventType()) {
      eventType = EventType.valueOf(searchDto.getEventType().toUpperCase());
    }
    
    // 새로운 통합 검색 Repository 메서드 사용
    return eventRepository.findBySearch(
      searchDto.hasKeyword() ? searchDto.getKeyword() : null,
      regions,
      searchDto.getStartDate(),
      searchDto.getEndDate(),
      eventType
    );
  }


  @Transactional
  public Event update(Long id, EventRequestDto requestDto) {
    Event event = findById(id);
    Region region = regionService.findExact(requestDto.getRegionDto());
    
    event.setEventType(requestDto.getEventType());
    event.setTitle(requestDto.getTitle());
    event.setRegion(region);
    event.setEventLocation(requestDto.getEventLocation());
    event.setAddress(requestDto.getAddress());
    event.setAddressDetail(requestDto.getAddressDetail());
    event.setStartDate(requestDto.getStartDate());
    event.setEndDate(requestDto.getEndDate());
    event.setDurationMin(requestDto.getDurationMin());
    event.setMinAge(requestDto.getMinAge());
    event.setDescription(requestDto.getDescription());
    
    return event;
  }

  @Transactional
  public void delete(Long eventId) {
    eventRepository.deleteById(eventId);
  }

  // 이벤트 관심 표시 토글
  @Transactional
  public boolean toggleEventInterest(Long eventId, Long memberId) {
    Event event = findById(eventId);  // EventService에서 조회
    Member member = memberService.findById(memberId);  // 의존성 주입된 MemberService 사용

    Optional<InterestEvents> existing = interestEventsRepository.findByMemberAndEvent(member, event);

    if (existing.isPresent()) {
      // 관심 표시 취소
      interestEventsRepository.delete(existing.get());
      event.setInterestCount(event.getInterestCount() - 1);
      return false; // 취소됨
    } else {
      // 관심 표시 추가
      InterestEvents interestEvents = InterestEvents.builder()
        .member(member)
        .event(event)
        .build();
      interestEventsRepository.save(interestEvents);
      event.setInterestCount(event.getInterestCount() + 1);
      return true; // 추가됨
    }
  }

}
