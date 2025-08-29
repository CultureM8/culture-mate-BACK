package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.repository.BoardRepository;
import com.culturemate.culturemate_api.repository.EventRepository;
import com.culturemate.culturemate_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
  private final BoardRepository boardRepository;
  private final MemberRepository memberRepository;
  private final EventService eventService;

  // 작성자로 조회
  @Transactional(readOnly = true)
  public List<Board> getBoardsByAuthor(Long memberId) {
    Member author = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    return boardRepository.findByAuthor(author);
  }

  // 이벤트로 조회
  @Transactional(readOnly = true)
  public List<Board> getBoardsByEvent(Long eventId) {
    Event event = eventService.read(eventId); // 필요 시 eventRepository로 조회 가능
    return boardRepository.findByEvent(event);
  }

  // 이벤트 타입으로 조회
  @Transactional(readOnly = true)
  public List<Board> getBoardsByEventType(EventType eventType) {
    return boardRepository.findByEventType(eventType);
  }

  // 제목으로 검색
  @Transactional(readOnly = true)
  public List<Board> searchBoardsByTitle(String title) {
    return boardRepository.findByTitleContaining(title);
  }

  // 이벤트로 검색 키워드(제목)로 검색
  @Transactional(readOnly = true)
  public List<Board> searchBoardsByEventAndKeyword(Long eventId, String keyword) {
    Event event = eventService.read(eventId);
    if (event == null) {
      throw new IllegalArgumentException("이벤트가 존재하지 않습니다.");
    }
    return boardRepository.findByEventAndTitleContaining(event, keyword);
  }

  // 게시글 생성
  @Transactional
  public Board createBoard(Long memberId, Board board) {
    Member author = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    board.setAuthor(author);
    return boardRepository.save(board);
  }

  // 게시글 수정
  @Transactional
  public Board updateBoard(Long boardId, String title, String content) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
    board.setContent(content);
    return boardRepository.save(board);
  }

  // 게시글 삭제
  @Transactional
  public void deleteBoard(Long boardId) {
    boardRepository.deleteById(boardId);
  }
}
