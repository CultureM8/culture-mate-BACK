package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.dto.BoardDto;
import com.culturemate.culturemate_api.dto.BoardSearchDto;
import com.culturemate.culturemate_api.dto.ImageUploadRequestDto;
import com.culturemate.culturemate_api.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;

  // 전체 게시물 조회
  @GetMapping
  public ResponseEntity<List<BoardDto.Response>> getAllBoards() {
    return ResponseEntity.ok().body(
      boardService.findAll().stream()
        .map(BoardDto.Response::from)
        .collect(Collectors.toList()));
  }

  // 특정 게시물 조회
  @GetMapping("/{boardId}")
  public ResponseEntity<BoardDto.Response> getBoard(@PathVariable Long boardId) {
    return ResponseEntity.ok().body(
      BoardDto.Response.from(boardService.findById(boardId))
    );
  }

  // 작성자 기준 게시글 조회
  @GetMapping("/author/{memberId}")
  public ResponseEntity<List<BoardDto.Response>> getByAuthor(@PathVariable Long memberId) {
    return ResponseEntity.ok(
      boardService.findByAuthor(memberId).stream()
        .map(BoardDto.Response::from)
        .collect(Collectors.toList())
    );
  }

  // 통합 검색
  @GetMapping("/search")
  public ResponseEntity<List<BoardDto.Response>> search(BoardSearchDto searchDto) {
    if (searchDto.isEmpty()) {
      throw new IllegalArgumentException("검색 조건을 하나 이상 입력해주세요.");
    }
    
    List<BoardDto.Response> boards = boardService.search(searchDto).stream()
      .map(BoardDto.Response::from)
      .collect(Collectors.toList());
    return ResponseEntity.ok(boards);
  }

  // 게시글 생성
  @PostMapping
  public ResponseEntity<BoardDto.Response> add(@Valid @RequestBody BoardDto.Request requestDto) {
    return ResponseEntity.ok(
      BoardDto.Response.from(boardService.create(requestDto))
    );
  }

  // 게시글 수정
  @PutMapping("/{boardId}")
  public ResponseEntity<BoardDto.Response> modify(@PathVariable Long boardId,
                                                      @Valid @RequestBody BoardDto.Request requestDto) {
    return ResponseEntity.ok(
      BoardDto.Response.from(boardService.update(boardId, requestDto))
    );
  }

  // 게시글 삭제
  @DeleteMapping("/{boardId}")
  public ResponseEntity<Void> remove(@PathVariable Long boardId) {
    boardService.delete(boardId);
    return ResponseEntity.noContent().build();
  }

  // 좋아요 토글 (추가/취소)
  @PostMapping("/{boardId}/like")
  public ResponseEntity<String> toggleLike(@PathVariable Long boardId,
                                           @RequestParam Long memberId) {
    boolean liked = boardService.toggleBoardLike(boardId, memberId);

    if (liked) {
      return ResponseEntity.ok("좋아요 성공");
    } else {
      return ResponseEntity.ok("좋아요 취소");
    }
  }
}
