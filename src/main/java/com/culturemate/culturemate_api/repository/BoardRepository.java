package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

  // 작성자로 조회
  List<Board> findByAuthor(Member author);

  // 이벤트로 조회
  List<Board> findByEvent(Event event);

  // 이벤트 타입으로 조회
  List<Board> findByEventType(EventType eventType);

  // Title로 조회
  List<Board> findByTitleContaining(String keyword);

  // 이벤트 + 제목 키워드 조건으로 조회
  List<Board> findByEventAndTitleContaining(Event event, String keyword);
}
