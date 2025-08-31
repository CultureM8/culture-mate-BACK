package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.community.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
  private Long id;
  private Long boardId;
  private Long parentId;
  private String content;
  private Instant createdAt;
  private Integer likeCount;
  private Integer dislikeCount;

  public static CommentDto fromEntity(Comment comment) {
    return CommentDto.builder()
      .id(comment.getId())
      .boardId(comment.getBoard().getId())
      .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
      .content(comment.getContent())
      .createdAt(comment.getCreatedAt())
      .likeCount(comment.getLikeCount())
      .dislikeCount(comment.getDislikeCount())
      .build();
  }
}