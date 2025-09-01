package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.member.InterestEvents;
import com.culturemate.culturemate_api.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestEventsRepository extends JpaRepository<InterestEvents, Long> {
  
  // 특정 회원이 특정 이벤트에 관심을 표시했는지 확인
  boolean existsByMemberAndEvent(Member member, Event event);
  
  // 특정 회원과 이벤트 조합으로 조회
  Optional<InterestEvents> findByMemberAndEvent(Member member, Event event);
  
  // 회원의 모든 관심 이벤트 조회
  List<InterestEvents> findByMember(Member member);
  
  // 이벤트에 관심을 표시한 모든 회원 조회
  List<InterestEvents> findByEvent(Event event);
}