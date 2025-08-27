package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventRepository {

  private final EntityManager em;
  private final RegionRepository regionRepository;

  public void save(Event event) {
    em.persist(event);
  }

  public Optional<Event> findById(long id) {
    return Optional.ofNullable(em.find(Event.class, id));
  }

  public List<Event> findAll() {
    return em.createQuery("from Event", Event.class).getResultList();
  }

  public List<Event> findByTitle(String title) {
    return em.createQuery("select e from Event e where e.title LIKE :title", Event.class)
             .setParameter("title", "%" + title + "%")
             .getResultList();
  }

  public List<Event> findByRegion(Region region) {
    List<Region> toSearch = regionRepository.findRegionsByCondition(region);
    
    // 찾은 지역들과 일치하는 이벤트 검색
    return em.createQuery("select e from Event e where e.region in :regions", Event.class)
             .setParameter("regions", toSearch)
             .getResultList();
  }


}
