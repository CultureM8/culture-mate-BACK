package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.event.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(description = "게시물 생성/수정 요청 DTO")
  public static class Request {
    @Schema(description = "게시물 제목", example = "새로운 전시회 정보 공유", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @Schema(description = "게시물 내용", example = "이번에 새로 열린 전시에요...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @Schema(description = "작성자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "작성자 정보는 필수입니다.")
    private Long authorId;

    @Schema(description = "연관 이벤트 타입", example = "EXHIBITION")
    private EventType eventType;

    @Schema(description = "연관 이벤트 ID", example = "101")
    private Long eventId;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "게시물 상세 정보 응답 DTO")
  public static class Response {
    @Schema(description = "게시물 ID", example = "1")
    private Long id;
    @Schema(description = "게시물 제목", example = "새로운 전시회 정보 공유")
    private String title;
    @Schema(description = "게시물 내용", example = "이번에 새로 열린 전시에요...")
    private String content;
    @Schema(description = "작성자 프로필 정보")
    private MemberDto.ProfileResponse author;
    @Schema(description = "연관 이벤트 카드 정보")
    private EventDto.ResponseCard eventCard;
    @Schema(description = "좋아요 수", example = "15")
    private Integer likeCount;
    @Schema(description = "생성 시각")
    private LocalDateTime createdAt;
    @Schema(description = "최종 수정 시각")
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
