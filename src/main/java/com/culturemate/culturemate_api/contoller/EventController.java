package com.culturemate.culturemate_api.contoller;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventReview;
import com.culturemate.culturemate_api.repository.EventRepository;
import com.culturemate.culturemate_api.repository.EventReviewRepository;
import com.culturemate.culturemate_api.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
  private final EventRepository eventRepository;
  private final EventReviewRepository eventReviewRepository;
  private final RegionRepository regionRepository;

  // 이벤트 전체 데이터 조회하기
  @GetMapping
  public List<Event> getAllEvents() {
    return eventRepository.findAll();
  }

  // 이벤트 ID로 데이터 조회
  @GetMapping("/{id}")
  public Event getEventById(@PathVariable Long id) {
    return eventRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Event not found: " + id));
  }

  // 제목으로 이벤트 검색
  @GetMapping("/search")
  public List<Event> searchEventsByTitle(@RequestParam String title) {
    return eventRepository.findByTitle(title);
  }

  // 지역으로 이벤트 검색
  @GetMapping("/region")
  public List<Event> getEventsByRegion(@RequestParam String level1,
                                       @RequestParam(required = false) String level2,
                                       @RequestParam(required = false) String level3) {
    Region region = new Region();
    region.setLevel1(level1);
    region.setLevel2(level2 != null ? level2 : "전체");
    region.setLevel3(level3 != null ? level3 : "전체");

    return eventRepository.findByRegion(region);
  }

  // 이벤트 등록
  @PostMapping
  public Event createEvent(@RequestBody Event event) {
    eventRepository.save(event);
    return event;
  }
}
