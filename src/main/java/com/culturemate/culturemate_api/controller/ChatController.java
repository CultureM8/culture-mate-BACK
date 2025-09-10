package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.dto.ChatMessageDto;
import com.culturemate.culturemate_api.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatRoomService chatRoomService;
  private final SimpMessagingTemplate messagingTemplate;

  // 클라이언트 → 서버로 메시지 전송
  @MessageMapping("/chatroom/{roomId}/send")
  public void sendMessage(@DestinationVariable Long roomId, 
                         ChatMessageDto messageDto, 
                         @AuthenticationPrincipal AuthenticatedUser user) {
    // 1. 사용자 인증 확인
    if (user == null) {
      throw new IllegalArgumentException("인증이 필요합니다");
    }
    
    // 2. 채팅방 참여 권한 검증 및 메시지 저장 (서비스에서 처리)
    ChatMessage savedMessage = chatRoomService.sendMessage(
        roomId, user.getMemberId(), messageDto.getContent()
    );

    // 3. 채팅방 구독자들에게 메시지 전송
    messagingTemplate.convertAndSend(
      "/topic/chatroom/" + roomId,
      ChatMessageDto.from(savedMessage)
    );
  }

  // 채팅방 입장 알림
  @MessageMapping("/chatroom/{roomId}/join")
  public void joinRoom(@DestinationVariable Long roomId, 
                      @AuthenticationPrincipal AuthenticatedUser user) {
    if (user == null) {
      throw new IllegalArgumentException("인증이 필요합니다");
    }
    
    chatRoomService.addMemberToRoom(roomId, user.getMemberId());
    
    // 입장 알림
    messagingTemplate.convertAndSend("/topic/chatroom/" + roomId, 
        Map.of("type", "JOIN", "message", user.getNickname() + "님이 입장했습니다"));
  }

}
