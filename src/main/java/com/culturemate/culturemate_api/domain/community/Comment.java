package com.culturemate.culturemate_api.domain.community;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Getter
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

  private int likeCount;
  private int dislikeCount;

  //=== 메서드 ===//
  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

}
