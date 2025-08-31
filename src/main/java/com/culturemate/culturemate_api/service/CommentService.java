package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.community.Comment;
import com.culturemate.culturemate_api.dto.CommentDto;
import com.culturemate.culturemate_api.repository.BoardRepository;
import com.culturemate.culturemate_api.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
  private final CommentRepository commentRepository;
  private final BoardRepository boardRepository;

  public CommentDto createComment(Long boardId, Long parentId, String content) {
    var board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
    Comment parent = null;
    if (parentId != null) {
      parent = commentRepository.findById(parentId)
        .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
    }

    Comment comment = Comment.builder()
      .board(board)
      .parent(parent)
      .content(content)
      .likeCount(0)
      .dislikeCount(0)
      .build();

    return CommentDto.fromEntity(commentRepository.save(comment));
  }

  public List<CommentDto> getCommentsByBoard(Long boardId) {
    return commentRepository.findByBoardIdOrderByCreatedAtDesc(boardId)
      .stream()
      .map(CommentDto::fromEntity)
      .toList();
  }

  public List<CommentDto> getReplies(Long parentId) {
    return commentRepository.findByParentId(parentId)
      .stream()
      .map(CommentDto::fromEntity)
      .toList();
  }

  public void deleteComment(Long commentId) {
    commentRepository.deleteById(commentId);
  }

  public CommentDto likeComment(Long commentId) {
    Comment comment = commentRepository.findById(commentId)
      .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
    comment.getLikeCount();
    return CommentDto.fromEntity(comment);
  }

  public CommentDto dislikeComment(Long commentId) {
    Comment comment = commentRepository.findById(commentId)
      .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
    comment.getDislikeCount();
    return CommentDto.fromEntity(comment);
  }
}
