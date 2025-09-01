package com.culturemate.culturemate_api.domain.community;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.domain.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Board {

  //=== 필드 ===//
  @Id @GeneratedValue
  @Column(name = "board_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  private EventType eventType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="event_id")
  private Event event;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="member_id")
  private Member author;

  @Setter
  @NotNull
  private String title;

  @Column(length = 2000)
  @Setter
  @NotNull
  private String content;

  @Column(nullable = false)
  private Instant createdAt;
  private Instant updatedAt;

  @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments;

  @Setter
  private Integer likeCount = 0;
  private Integer dislikeCount = 0;

  //=== 조회 로직 ===//
  public Integer getCommentCount() {
    return comments.size();
  }

  //=== 생성/수정 로직 ===//
  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = Instant.now();
  }

}
