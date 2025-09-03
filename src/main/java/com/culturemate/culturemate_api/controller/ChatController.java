package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.chat.ChatMessageDto;
import com.culturemate.culturemate_api.repository.MemberRepository;
import com.culturemate.culturemate_api.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;
  private final MemberRepository memberRepository;
  private final SimpMessagingTemplate messagingTemplate;

  // 클라이언트 → 서버로 메시지 전송
  @MessageMapping("/chat.sendMessage")
  public void sendMessage(ChatMessageDto messageDto) {
    Member sender = memberRepository.findById(messageDto.getSenderId())
      .orElseThrow(() -> new IllegalArgumentException("Invalid sender Id: " + messageDto.getSenderId()));
    ChatRoom room = chatService.findRoomById(messageDto.getRoomId());

    ChatMessage savedMessage = chatService.saveMessage(
      ChatMessage.builder()
        .chatRoom(room)
        .author(sender)
        .content(messageDto.getContent())
        .build()
    );

    // 클라이언트에 DTO로 전송
    messagingTemplate.convertAndSend(
      "/topic/chatroom/" + messageDto.getRoomId(),
      new ChatMessageDto(
        savedMessage.getChatRoom().getId(),
        savedMessage.getAuthor().getId(),
        savedMessage.getContent()
      )
    );

  }
}
