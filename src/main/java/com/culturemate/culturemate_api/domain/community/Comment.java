package com.culturemate.culturemate_api.domain.community;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "comments")
public class Comment {

  //=== 필드 ===//
  @Id @GeneratedValue
  @Column(name = "comment_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Board board;

  @ManyToOne(fetch = FetchType.LAZY)
  private Comment parent;

  private Instant createdAt;

  private String content;

  private Integer likeCount;
  private Integer dislikeCount;

  //=== 생성/수정 로직 ===//
  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

}
