package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.dto.ChatMessageDto;
import com.culturemate.culturemate_api.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatRoomService chatRoomService;
  private final SimpMessagingTemplate messagingTemplate;

  // 클라이언트 → 서버로 메시지 전송
  @MessageMapping("/chat.sendMessage")
  public void sendMessage(ChatMessageDto messageDto) {
    // ChatService에서 비즈니스 로직 처리
    ChatMessage savedMessage = chatRoomService.sendMessage(
        messageDto.getRoomId(),
        messageDto.getSenderId(),
        messageDto.getContent()
    );

    // 클라이언트에 DTO로 전송
    messagingTemplate.convertAndSend(
      "/topic/chatroom/" + messageDto.getRoomId(),
      ChatMessageDto.from(savedMessage)
    );
  }

}
