package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.community.Comment;
import com.culturemate.culturemate_api.dto.CommentDto;
import com.culturemate.culturemate_api.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  // 댓글 생성
  @PostMapping
  public ResponseEntity<CommentDto> createComment(@RequestParam Long boardId,
                                                  @RequestParam(required = false) Long parentId,
                                                  @RequestParam String content) {
    CommentDto created = commentService.createComment(boardId, parentId, content);
    return ResponseEntity.status(201).body(created); // 201 Created
  }

  // 특정 게시글 댓글 조회
  @GetMapping("/board/{boardId}")
  public ResponseEntity<List<CommentDto>> getCommentsByBoard(@PathVariable Long boardId) {
    List<CommentDto> comments = commentService.getCommentsByBoard(boardId);
    return ResponseEntity.ok(comments); // 200 OK
  }

  // 특정 댓글의 대댓글 조회
  @GetMapping("/reply/{parentId}")
  public ResponseEntity<List<CommentDto>> getReplies(@PathVariable Long parentId) {
    List<CommentDto> replies = commentService.getReplies(parentId);
    return ResponseEntity.ok(replies); // 200 OK
  }

  // 삭제
  @DeleteMapping("/{commentId}")
  public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
    commentService.deleteComment(commentId);
    return ResponseEntity.noContent().build(); // 204 No Content
  }

  // 좋아요
  @PostMapping("/{commentId}/like")
  public ResponseEntity<CommentDto> likeComment(@PathVariable Long commentId) {
    CommentDto updated = commentService.likeComment(commentId);
    return ResponseEntity.ok(updated); // 200 OK
  }

  // 싫어요
  @PostMapping("/{commentId}/dislike")
  public ResponseEntity<CommentDto> dislikeComment(@PathVariable Long commentId) {
    CommentDto updated = commentService.dislikeComment(commentId);
    return ResponseEntity.ok(updated); // 200 OK
  }
}
