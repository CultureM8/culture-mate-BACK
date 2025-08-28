package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Member 엔티티 Repository
 * 
 * [Spring Data JPA 자동 제공 메서드들]
 * - save(Member entity) : 엔티티 저장/수정
 * - saveAll(Iterable<Member> entities) : 여러 엔티티 일괄 저장
 * - findById(Long id) : ID로 단건 조회 (Optional 반환)
 * - findAll() : 전체 조회
 * - findAll(Pageable pageable) : 페이징 조회
 * - findAll(Sort sort) : 정렬 조회
 * - count() : 전체 개수
 * - existsById(Long id) : ID 존재 여부 확인
 * - delete(Member entity) : 엔티티 삭제
 * - deleteById(Long id) : ID로 삭제
 * - deleteAll() : 전체 삭제
 * 
 * [커스텀 쿼리 메서드 작성법]
 * - 메서드명으로 자동 쿼리 생성: findBy필드명, existsBy필드명 등
 * - 예시: findByName(String name) → SELECT * FROM member WHERE name = ?
 * - 복합조건: findByNameAndAge(String name, Integer age)
 * - LIKE 검색: findByNameContaining(String keyword)
 * - 정렬: findByNameOrderByCreatedAtDesc(String name)
 * - 페이징: findByName(String name, Pageable pageable)
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByLoginId(String loginId);
  
  boolean existsByLoginId(String loginId);
  
  List<Member> findByStatus(MemberStatus status);

}
