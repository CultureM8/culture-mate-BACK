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

@Getter
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
  @DateTimeFormat(pattern = "yyyy-MM-dd")  
  private LocalDate updatedAt;
  private Integer likeCount;
  private Integer dislikeCount;

  public static CommentResponseDto from(Comment comment) {
    return CommentResponseDto.builder()
      .id(comment.getId())
      .boardId(comment.getBoard().getId())
      .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
      .content(comment.getContent())
      .createdAt(comment.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate())
      .updatedAt(comment.getUpdatedAt() != null ? 
                 comment.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDate() : null)
      .likeCount(comment.getLikeCount())
      .dislikeCount(comment.getDislikeCount())
      .build();
  }
}