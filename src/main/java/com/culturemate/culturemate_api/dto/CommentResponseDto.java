package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.community.Comment;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
  private Long id;
  private Long boardId;
  private Long authorId;
  private Long parentId;
  private String content;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt;
  @DateTimeFormat(pattern = "yyyy-MM-dd")  
  private LocalDate updatedAt;
  private Integer likeCount;
  // private Integer dislikeCount; // TODO: 나중에 싫어요 기능 추가 시 활성화

  public static CommentResponseDto from(Comment comment) {
    return CommentResponseDto.builder()
      .id(comment.getId())
      .boardId(comment.getBoard().getId())
      .authorId(comment.getAuthor().getId())
      .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
      .content(comment.getContent())
      .createdAt(comment.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate())
      .updatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDate() : null)
      .likeCount(comment.getLikeCount())
      // .dislikeCount(comment.getDislikeCount()) // TODO: 나중에 싫어요 기능 추가 시 활성화
      .build();
  }
}