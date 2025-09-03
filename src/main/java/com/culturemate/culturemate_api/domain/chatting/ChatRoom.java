package com.culturemate.culturemate_api.domain.chatting;

import com.culturemate.culturemate_api.domain.community.Board;
import com.culturemate.culturemate_api.domain.together.Together;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoom {
  @Id
  @GeneratedValue
  @Column(name = "room_id")
  private Long id;

  private String roomName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "together_id", nullable = false)
  private Together together;

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<ChatMember> chatMembers = new ArrayList<>();
}
