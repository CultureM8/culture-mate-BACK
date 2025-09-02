package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.event.EventType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
  private Long id;
  private String title;
  private String content;
  private Long authorId;
  private String authorLoginId;
  private Long eventId;
  private EventType eventType;
  private Integer likeCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static BoardResponseDto from(Board board) {
    return BoardResponseDto.builder()
      .id(board.getId())
      .title(board.getTitle())
      .content(board.getContent())
      .authorId(board.getAuthor().getId())
      .authorLoginId(board.getAuthor().getLoginId())
      .eventId(board.getEvent() != null ? board.getEvent().getId() : null)
      .eventType(board.getEventType() != null ? board.getEventType() : 
                 (board.getEvent() != null ? board.getEvent().getEventType() : null))
      .likeCount(board.getLikeCount())
      .createdAt(board.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
      .updatedAt(board.getUpdatedAt() != null ? 
                 board.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
      .build();
  }
}
