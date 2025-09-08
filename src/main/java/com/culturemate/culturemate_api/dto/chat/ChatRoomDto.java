package com.culturemate.culturemate_api.dto.chat;

import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class ChatRoomDto {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Response {
    private Long id;
    private String roomName;
    private int participantCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;

    public static Response from(ChatRoom chatRoom) {
      return Response.builder()
        .id(chatRoom.getId())
        .roomName(chatRoom.getRoomName())
        .participantCount(chatRoom.getParticipantCount())
        .createdAt(chatRoom.getCreatedAt() != null ? 
                   chatRoom.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
        .lastMessageAt(chatRoom.getLastMessageAt() != null ? 
                       chatRoom.getLastMessageAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
        .build();
    }
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ResponseDetail {
    private Long id;
    private String roomName;
    private int participantCount;
    private List<MemberDto.ProfileResponse> participants;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;

    public static ResponseDetail from(ChatRoom chatRoom) {
      List<MemberDto.ProfileResponse> participants = chatRoom.getChatMembers().stream()
        .map(chatMember -> MemberDto.ProfileResponse.from(chatMember.getMember()))
        .collect(Collectors.toList());

      return ResponseDetail.builder()
        .id(chatRoom.getId())
        .roomName(chatRoom.getRoomName())
        .participantCount(chatRoom.getParticipantCount())
        .participants(participants)
        .createdAt(chatRoom.getCreatedAt() != null ? 
                   chatRoom.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
        .lastMessageAt(chatRoom.getLastMessageAt() != null ? 
                       chatRoom.getLastMessageAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
        .build();
    }
  }
}