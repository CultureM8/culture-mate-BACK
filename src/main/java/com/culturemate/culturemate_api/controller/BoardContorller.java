package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.dto.BoardDto;
import com.culturemate.culturemate_api.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardContorller {

  private final BoardService boardService;

  // 작성자 기준 게시글 조회
  @GetMapping("/author/{memberId}")
  public ResponseEntity<List<BoardDto>> getBoardsByAuthor(@PathVariable Long memberId) {
    return ResponseEntity.ok(boardService.getBoardsByAuthor(memberId));
  }

  // 이벤트 기준 게시글 조회
  @GetMapping("/event/{eventId}")
  public ResponseEntity<List<BoardDto>> getBoardsByEvent(@PathVariable Long eventId) {
    return ResponseEntity.ok(boardService.getBoardsByEvent(eventId));
  }

  // 이벤트 타입 기준 게시글 조회
  @GetMapping("/event-type/{eventType}")
  public ResponseEntity<List<BoardDto>> getBoardsByEventType(@PathVariable String eventType) {
    try {
      EventType type = EventType.valueOf(eventType.toUpperCase());
      return ResponseEntity.ok(boardService.getBoardsByEventType(type));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  // 제목 검색
  @GetMapping("/search")
  public ResponseEntity<List<BoardDto>> searchBoardsByTitle(@RequestParam String title) {
    return ResponseEntity.ok(boardService.searchBoardsByTitle(title));
  }

  // 이벤트 + 키워드 검색
  @GetMapping("/event/{eventId}/search")
  public ResponseEntity<List<BoardDto>> searchBoardsByEventAndKeyword(
    @PathVariable Long eventId,
    @RequestParam String keyword) {
    return ResponseEntity.ok(boardService.searchBoardsByEventAndKeyword(eventId, keyword));
  }

  // 게시글 생성
  @PostMapping("/author/{memberId}")
  public ResponseEntity<BoardDto> createBoard(@PathVariable Long memberId, @RequestBody BoardDto boardDto) {
    return ResponseEntity.ok(boardService.createBoard(memberId, boardDto));
  }

  // 게시글 수정
  @PutMapping("/{boardId}")
  public ResponseEntity<BoardDto> updateBoard(@PathVariable Long boardId, @RequestBody BoardDto boardDto) {
    return ResponseEntity.ok(boardService.updateBoard(boardId, boardDto.getTitle(), boardDto.getContent()));
  }

  // 게시글 삭제
  @DeleteMapping("/{boardId}")
  public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
    boardService.deleteBoard(boardId);
    return ResponseEntity.noContent().build();
  }
}
