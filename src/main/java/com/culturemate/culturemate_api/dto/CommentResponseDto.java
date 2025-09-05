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
  // parentId 제거: 대댓글은 별도 API로 조회
  private String content;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt;
  @DateTimeFormat(pattern = "yyyy-MM-dd")  
  private LocalDate updatedAt;
  private Integer likeCount;
  private int replyCount; // 대댓글 수

  public static CommentResponseDto from(Comment comment) {
    return CommentResponseDto.builder()
      .id(comment.getId())
      .boardId(comment.getBoard().getId())
      .authorId(comment.getAuthor().getId())
      // parentId 제거
      .content(comment.getContent())
      .createdAt(comment.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate())
      .updatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDate() : null)
      .likeCount(comment.getLikeCount())
      .replyCount(comment.getReplyCount())
      .build();
  }
}