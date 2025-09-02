package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventReview;
import com.culturemate.culturemate_api.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventReviewRepository extends JpaRepository<EventReview, Long> {

  List<EventReview> findByEvent(Event event);
  
  List<EventReview> findByEventOrderByCreatedAtDesc(Event event);

  List<EventReview> findByMember(Member member);
}