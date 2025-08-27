package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Region;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RegionRepository {

  private final EntityManager em;

  public void save(Region region) {
    em.persist(region);
  }

  public Optional<Region> findById(Long id) {
    return Optional.ofNullable(em.find(Region.class, id));
  }

  public List<Region> findAll() {
    return em.createQuery("select r from Region r", Region.class).getResultList();
  }

  public List<Region> findRegionsByCondition(Region region) {
    if(region.getLevel1().isEmpty() || region.getLevel1().equals("전체")) {
      // 모든 지역 검색
      return em.createQuery("select r from Region r", Region.class).getResultList();
    } else if(region.getLevel2().isEmpty() || region.getLevel2().equals("전체")) {
      // Level1만 일치하는 모든 지역 검색
      return em.createQuery("select r from Region r where r.level1 = :level1", Region.class)
                 .setParameter("level1", region.getLevel1())
                 .getResultList();
    } else if(region.getLevel3().isEmpty() || region.getLevel3().equals("전체")) {
      // Level1, Level2가 일치하는 모든 지역 검색
      return em.createQuery("select r from Region r where r.level1 = :level1 and r.level2 = :level2", Region.class)
                 .setParameter("level1", region.getLevel1())
                 .setParameter("level2", region.getLevel2())
                 .getResultList();
    } else {
      // 정확히 일치하는 지역만 검색
      return em.createQuery("select r from Region r where r.level1 = :level1 and r.level2 = :level2 and r.level3 = :level3", Region.class)
                 .setParameter("level1", region.getLevel1())
                 .setParameter("level2", region.getLevel2())
                 .setParameter("level3", region.getLevel3())
                 .getResultList();
    }
  }

  public void delete(Region region) {
    em.remove(region);
  }

  public void deleteById(Long id) {
    Optional<Region> region = findById(id);
    if (region.isPresent()) {
      em.remove(region.get());
    }
  }

}