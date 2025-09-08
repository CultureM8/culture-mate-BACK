package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.community.Comment;
import com.culturemate.culturemate_api.dto.CommentRequestDto;
import com.culturemate.culturemate_api.dto.CommentResponseDto;
import com.culturemate.culturemate_api.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Comment API", description = "댓글 관리 API")
@RestController
@RequestMapping("/api/v1/board/{boardId}/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @Operation(summary = "댓글 생성", description = "게시글에 댓글을 생성합니다")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "댓글 생성 성공"),
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
  })
  @PostMapping
  public ResponseEntity<CommentResponseDto> add(
    @Parameter(description = "게시글 ID", required = true) @PathVariable Long boardId,
    @Parameter(description = "댓글 내용", required = true) @RequestBody CommentRequestDto requestDto) {
    Comment created = commentService.create(
        boardId, 
        requestDto.getAuthorId(), 
        requestDto.getParentId(), 
        requestDto.getComment()
    );
    return ResponseEntity.status(201).body(CommentResponseDto.from(created)); // 201 Created
  }

  // 댓글 수정
  @PutMapping("/{commentId}")
  public ResponseEntity<CommentResponseDto> modify(@PathVariable Long commentId,
                                                  @RequestBody CommentRequestDto requestDto) {
    Comment updated = commentService.update(commentId, requestDto.getComment(), requestDto.getAuthorId());
    return ResponseEntity.ok(CommentResponseDto.from(updated)); // 200 OK
  }

  // 부모 댓글만 조회 (replyCount 포함)
  @GetMapping
  public ResponseEntity<List<CommentResponseDto>> getParentCommentsByBoard(@PathVariable Long boardId) {
    List<CommentResponseDto> comments = commentService.findParentCommentsByBoard(boardId)
      .stream()
      .map(CommentResponseDto::from)
      .collect(Collectors.toList());
    return ResponseEntity.ok(comments); // 200 OK
  }

  // 특정 댓글의 대댓글 조회
  @GetMapping("/{parentId}/replies")
  public ResponseEntity<List<CommentResponseDto>> getReplies(@PathVariable Long parentId) {
    List<CommentResponseDto> replies = commentService.findReplies(parentId)
      .stream()
      .map(CommentResponseDto::from)
      .collect(Collectors.toList());
    return ResponseEntity.ok(replies); // 200 OK
  }

  // 삭제
  @DeleteMapping("/{commentId}")
  public ResponseEntity<Void> remove(@PathVariable Long commentId,
                                    @RequestParam Long requesterId) {
    commentService.delete(commentId, requesterId);
    return ResponseEntity.noContent().build(); // 204 No Content
  }

  // 좋아요
  @PostMapping("/{commentId}/like")
  public ResponseEntity<String> toggleCommentLike(@PathVariable Long commentId,
                                                  @RequestParam Long memberId) {
    boolean liked = commentService.toggleCommentLike(commentId, memberId);

    if (liked) {
      return ResponseEntity.ok("댓글 좋아요 성공");
    } else {
      return ResponseEntity.ok("댓글 좋아요 취소");
    }
  }

}
