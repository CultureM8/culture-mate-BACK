package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Region 엔티티 Repository
 * 
 * [Spring Data JPA 자동 제공 메서드들]
 * - save(Region entity) : 엔티티 저장/수정
 * - saveAll(Iterable<Region> entities) : 여러 엔티티 일괄 저장
 * - findById(Long id) : ID로 단건 조회 (Optional 반환)
 * - findAll() : 전체 조회
 * - findAll(Pageable pageable) : 페이징 조회
 * - findAll(Sort sort) : 정렬 조회
 * - count() : 전체 개수
 * - existsById(Long id) : ID 존재 여부 확인
 * - delete(Region entity) : 엔티티 삭제
 * - deleteById(Long id) : ID로 삭제
 * - deleteAll() : 전체 삭제
 * 
 * [커스텀 쿼리 메서드 작성법]
 * - 메서드명으로 자동 쿼리 생성: findBy필드명, existsBy필드명 등
 * - 예시: findByLevel1(String level1) → SELECT * FROM region WHERE level1 = ?
 * - 복합조건: findByLevel1AndLevel2(String level1, String level2)
 * - 정렬: findByLevel1OrderByLevel2Asc(String level1)
 * - 페이징: findByLevel1(String level1, Pageable pageable)
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

  List<Region> findByLevel1(String level1);
  
  List<Region> findByLevel1AndLevel2(String level1, String level2);
  
  List<Region> findByLevel1AndLevel2AndLevel3(String level1, String level2, String level3);
  
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