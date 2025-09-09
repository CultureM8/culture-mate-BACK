package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.event.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class BoardDto {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Request {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
    
    @NotNull(message = "작성자 정보는 필수입니다.")
    private Long authorId;
    
    private EventType eventType;
    
    private Long eventId;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response {
    private Long id;
    private String title;
    private String content;
    private MemberDto.ProfileResponse author;
    private EventDto.ResponseCard eventCard;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Response from(Board board) {
      return Response.builder()
        .id(board.getId())
        .title(board.getTitle())
        .content(board.getContent())
        .author(MemberDto.ProfileResponse.from(board.getAuthor()))
        .eventCard(board.getEvent() != null ? EventDto.ResponseCard.from(board.getEvent(), false) : null)
        .likeCount(board.getLikeCount())
        .createdAt(board.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
        .updatedAt(board.getUpdatedAt() != null ?
          board.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
        .build();
    }
  }
}
