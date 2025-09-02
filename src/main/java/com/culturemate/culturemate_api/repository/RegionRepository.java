package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

//  List<Region> findByLevel1(String level1);
  
//  List<Region> findByLevel1AndLevel2(String level1, String level2);
  @Query("SELECT r FROM Region r WHERE " +
    "(:level1 IS NULL AND r.level1 IS NULL OR :level1 = r.level1) AND " +
    "(:level2 IS NULL AND r.level2 IS NULL OR :level2 = r.level2) AND " +
    "(:level3 IS NULL AND r.level3 IS NULL OR :level3 = r.level3)")
  Region findExactRegion(@Param("level1") String level1,
                         @Param("level2") String level2,
                         @Param("level3") String level3);
  
  /**
   * 지역 조건에 따른 동적 검색
   * - level1이 "전체"이거나 비어있으면 모든 지역 검색
   * - level2가 "전체"이거나 비어있으면 level1만 일치하는 모든 지역 검색
   * - level3가 "전체"이거나 비어있으면 level1, level2가 일치하는 모든 지역 검색
   * - 모든 값이 있으면 정확히 일치하는 지역만 검색
   */
  @Query("SELECT r FROM Region r WHERE " +
         "(:level1 IS NULL OR :level1 = '' OR :level1 = '전체' OR r.level1 = :level1) AND " +
         "(:level2 IS NULL OR :level2 = '' OR :level2 = '전체' OR r.level2 = :level2) AND " +
         "(:level3 IS NULL OR :level3 = '' OR :level3 = '전체' OR r.level3 = :level3)")
  List<Region> findRegionsByCondition(@Param("level1") String level1, 
                                     @Param("level2") String level2, 
                                     @Param("level3") String level3);

}