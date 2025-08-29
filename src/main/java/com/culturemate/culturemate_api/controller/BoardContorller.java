package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardContorller {

  private final BoardService boardService;

  // 작성자로 조회
  @GetMapping("/author/{memberId}")
  public List<Board> getBoardsByAuthor(@PathVariable Long memberId) {
    return boardService.getBoardsByAuthor(memberId);
  }

  // 이벤트로 조회
  @GetMapping("/event/{eventId}")
  public List<Board> getBoardsByEvent(@PathVariable Long eventId) {
    return boardService.getBoardsByEvent(eventId);
  }

  // 이벤트 타입으로 조회
  @GetMapping("/event-type/{eventType}")
  public List<Board> getBoardsByEventType(@PathVariable EventType eventType) {
    return boardService.getBoardsByEventType(eventType);
  }

  // 제목으로 검색
  @GetMapping("/search/title")
  public List<Board> searchBoardsByTitle(@RequestParam String keyword) {
    return boardService.searchBoardsByTitle(keyword);
  }

  // 이벤트 + 키워드 검색
  @GetMapping("/search/event/{eventId}")
  public List<Board> searchBoardsByEventAndKeyword(
    @PathVariable Long eventId,
    @RequestParam String keyword) {
    return boardService.searchBoardsByEventAndKeyword(eventId, keyword);
  }

  // 게시글 생성
  @PostMapping("/{memberId}")
  public Board createBoard(@PathVariable Long memberId, @RequestBody Board board) {
    return boardService.createBoard(memberId, board);
  }

  // 게시글 수정
  @PutMapping("/{boardId}")
  public Board updateBoard(
    @PathVariable Long boardId,
    @RequestParam String title,
    @RequestParam String content) {
    return boardService.updateBoard(boardId, title, content);
  }

  // 게시글 삭제
  @DeleteMapping("/{boardId}")
  public void deleteBoard(@PathVariable Long boardId) {
    boardService.deleteBoard(boardId);
  }
}
