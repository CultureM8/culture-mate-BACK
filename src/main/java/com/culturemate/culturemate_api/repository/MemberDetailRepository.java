package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.member.MemberDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberDetailRepository extends JpaRepository<MemberDetail, Long> {

  // N+1 문제 해결: 관심 이벤트 타입과 태그를 한 번에 조회
  @Query("""
      SELECT DISTINCT md
      FROM MemberDetail md
      LEFT JOIN FETCH md.interestEventTypes
      LEFT JOIN FETCH md.interestTags it
      LEFT JOIN FETCH it.tag
      WHERE md.id = :memberId
      """)
  Optional<MemberDetail> findByIdWithAllInterests(@Param("memberId") Long memberId);
}
