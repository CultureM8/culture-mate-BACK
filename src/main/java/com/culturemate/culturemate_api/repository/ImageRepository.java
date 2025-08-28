package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Image;
import com.culturemate.culturemate_api.domain.ImageTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Image 엔티티 Repository
 * 
 * [Spring Data JPA 자동 제공 메서드들]
 * - save(Image entity) : 엔티티 저장/수정
 * - saveAll(Iterable<Image> entities) : 여러 엔티티 일괄 저장
 * - findById(Long id) : ID로 단건 조회 (Optional 반환)
 * - findAll() : 전체 조회
 * - findAll(Pageable pageable) : 페이징 조회
 * - findAll(Sort sort) : 정렬 조회
 * - count() : 전체 개수
 * - existsById(Long id) : ID 존재 여부 확인
 * - delete(Image entity) : 엔티티 삭제
 * - deleteById(Long id) : ID로 삭제
 * - deleteAll() : 전체 삭제
 * 
 * [커스텀 쿼리 메서드 작성법]
 * - 메서드명으로 자동 쿼리 생성: findBy필드명, existsBy필드명 등
 * - 예시: findByUrl(String url) → SELECT * FROM image WHERE url = ?
 * - 복합조건: findByTargetTypeAndTargetId(ImageTarget targetType, Long targetId)
 * - 정렬: findByTargetTypeOrderByCreatedAtDesc(ImageTarget targetType)
 * - 페이징: findByTargetType(ImageTarget targetType, Pageable pageable)
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

  List<Image> findByTargetTypeAndTargetId(ImageTarget targetType, Long targetId);
  
  List<Image> findByTargetType(ImageTarget targetType);
  
  @Modifying
  @Transactional
  @Query("DELETE FROM Image i WHERE i.targetType = :targetType AND i.targetId = :targetId")
  void deleteByTargetTypeAndTargetId(@Param("targetType") ImageTarget targetType, 
                                    @Param("targetId") Long targetId);

}
