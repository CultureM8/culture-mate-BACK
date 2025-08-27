package com.culturemate.culturemate_api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
public class Board {

  //=== 필드 ===//
  @Id @GeneratedValue
  @Column(name = "board_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  private EventType eventType;

  @ManyToOne(fetch = FetchType.LAZY)
  private Event event;

  @ManyToOne(fetch = FetchType.LAZY)
  private Member authorId;

  private String title;

  @Column(length = 2000)
  private String content;

  private Instant createdAt;
  private Instant updatedAt;

  @OneToMany(mappedBy = "board")
  private List<Comment> comments;

  private int likeCount = 0;
  private int dislikeCount = 0;


  //=== 메서드 ===//
  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = Instant.now();
  }

  public int getCommentCount() {
    return comments.size();
  }

}
