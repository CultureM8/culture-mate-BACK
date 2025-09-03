package com.culturemate.culturemate_api.domain.chatting;

import com.culturemate.culturemate_api.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessage {
  @Id
  @GeneratedValue
  @Column(name = "message_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id")
  private ChatRoom chatRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member author;

  private String content;
  private Instant createAt;


  //=== 생성/수정 로직 ===//
  @PrePersist
  public void onCreate() {
    this.createAt = Instant.now();
  }

}
