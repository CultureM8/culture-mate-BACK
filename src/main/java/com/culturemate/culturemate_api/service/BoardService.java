package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.community.BoardLike;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.BoardDto;
import com.culturemate.culturemate_api.repository.BoardLikeRepository;
import com.culturemate.culturemate_api.repository.BoardRepository;
import com.culturemate.culturemate_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
  private final BoardRepository boardRepository;
  private final BoardLikeRepository boardLikeRepository;
  private final MemberRepository memberRepository;
  private final EventService eventService;

  // 작성자로 조회
  @Transactional(readOnly = true)
  public List<BoardDto> getBoardsByAuthor(Long memberId) {
    Member author = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    return boardRepository.findByAuthor(author)
      .stream()
      .map(BoardDto::from)
      .toList();
  }

  // 이벤트로 게시글 조회
  public List<BoardDto> getBoardsByEvent(Long eventId) {
    Event event = eventService.read(eventId);
    return boardRepository.findByEvent(event)
      .stream()
      .map(BoardDto::from)
      .toList();
  }

  // 이벤트 타입으로 게시글 조회
  public List<BoardDto> getBoardsByEventType(EventType eventType) {
    return boardRepository.findByEventType(eventType)
      .stream()
      .map(BoardDto::from)
      .toList();
  }

  // 제목으로 검색
  public List<BoardDto> searchBoardsByTitle(String title) {
    return boardRepository.findByTitleContaining(title)
      .stream()
      .map(BoardDto::from)
      .toList();
  }

  // 이벤트 + 키워드 검색
  public List<BoardDto> searchBoardsByEventAndKeyword(Long eventId, String keyword) {
    Event event = eventService.read(eventId);
    if (event == null) throw new IllegalArgumentException("이벤트가 존재하지 않습니다.");
    return boardRepository.findByEventAndTitleContaining(event, keyword)
      .stream()
      .map(BoardDto::from)
      .toList();
  }

  // 게시글 생성
  @Transactional
  public BoardDto createBoard(Long memberId, BoardDto boardDto) {
    Member author = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    Board board = Board.builder()
      .title(boardDto.getTitle())
      .content(boardDto.getContent())
      .author(author)
      .build();
    Board saved = boardRepository.save(board);
    return BoardDto.from(saved);
  }

  // 게시글 수정
  @Transactional
  public BoardDto updateBoard(Long boardId, String title, String content) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

    board.setTitle(title);    // 엔티티에 Setter -> 새로운 객체를 만들어버릴 수 있으므로 엔티티에 Setter를 넣는걸 권장한다
    board.setContent(content);

    return BoardDto.from(board);
  }

  // 게시글 삭제
  @Transactional
  public void deleteBoard(Long boardId) {
    boardRepository.deleteById(boardId);
  }

  // 좋아요
  @Transactional
  public boolean toggleBoardLike(Long boardId, Long memberId) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
    Member member = memberRepository.findById(memberId)
      .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

    Optional<BoardLike> existing = boardLikeRepository.findByBoardAndMember(board, member);

    if (existing.isPresent()) {
      // 이미 좋아요 눌렀으면 취소
      boardLikeRepository.delete(existing.get());
      board.setLikeCount(board.getLikeCount() - 1);
      return false; // 취소됨
    } else {
      // 좋아요 추가
      BoardLike boardLike = BoardLike.builder()
        .board(board)
        .member(member)
        .build();
      boardLikeRepository.save(boardLike);
      board.setLikeCount(board.getLikeCount() + 1);
      return true; // 좋아요 성공
    }
  }

}
