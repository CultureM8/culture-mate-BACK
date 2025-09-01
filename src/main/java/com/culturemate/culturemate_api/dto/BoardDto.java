package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.event.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
  private Long id;
  private String title;
  private String content;
  private Long authorId;
  private String authorLoginId;
  private Long eventId;
  private EventType eventType;
  private Integer likeCount;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate createdAt;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate updatedAt;

  public static BoardDto from(Board board) {
    return BoardDto.builder()
      .id(board.getId())
      .title(board.getTitle())
      .content(board.getContent())
      .authorId(board.getAuthor().getId())
      .authorLoginId(board.getAuthor().getLoginId())
      .eventId(board.getEvent() != null ? board.getEvent().getId() : null)
      .eventType(board.getEvent() != null ? board.getEvent().getEventType() : null)
      .likeCount(board.getLikeCount())
      .build();
  }
}
