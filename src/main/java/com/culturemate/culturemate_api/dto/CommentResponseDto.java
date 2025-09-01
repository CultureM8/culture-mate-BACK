package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.community.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
  private Long id;
  private Long boardId;
  private Long parentId;
  private String content;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt;
  private Integer likeCount;
  private Integer dislikeCount;

  public static CommentResponseDto fromEntity(Comment comment) {
    return CommentResponseDto.builder()
      .id(comment.getId())
      .boardId(comment.getBoard().getId())
      .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
      .content(comment.getContent())
      .createdAt(comment.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate())
      .likeCount(comment.getLikeCount())
      .dislikeCount(comment.getDislikeCount())
      .build();
  }
}