package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.chatting.ChatMember;
import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.chat.ChatMessageDto;
import com.culturemate.culturemate_api.repository.ChatMemberRepository;
import com.culturemate.culturemate_api.repository.MemberRepository;
import com.culturemate.culturemate_api.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;
  private final MemberRepository memberRepository;
  private final ChatMemberRepository chatMemberRepository;
  private final SimpMessagingTemplate messagingTemplate;

  // 클라이언트 → 서버로 메시지 전송
  @MessageMapping("/chat.sendMessage")
  public void sendMessage(ChatMessageDto messageDto) {
    Member sender = memberRepository.findById(messageDto.getSenderId())
      .orElseThrow(() -> new IllegalArgumentException("Invalid sender Id: " + messageDto.getSenderId()));
    ChatRoom room = chatService.findRoomById(messageDto.getRoomId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid room Id: " + messageDto.getRoomId()));

    // room과 sender(Member)로 ChatMember 찾기
    ChatMember author = chatMemberRepository.findByChatRoomAndMember(room, sender)
            .orElseThrow(() -> new IllegalArgumentException("User is not in this chat room"));

    ChatMessage savedMessage = chatService.saveMessage(
      ChatMessage.builder()
        .chatRoom(room)
        .author(author) // ChatMember를 author로 설정
        .content(messageDto.getContent())
        .build()
    );

    // 클라이언트에 DTO로 전송
    messagingTemplate.convertAndSend(
      "/topic/chatroom/" + messageDto.getRoomId(),
      new ChatMessageDto(
        savedMessage.getChatRoom().getId(),
        savedMessage.getAuthor().getMember().getId(), // ChatMember에서 Member Id를 가져옴
        savedMessage.getContent()
      )
    );

  }

  // 이전 대화내역 불러오기
  @GetMapping("/api/chat/room/{roomId}/messages")
  @ResponseBody
  public ResponseEntity<List<ChatMessageDto>> getPreviousMessages(@PathVariable Long roomId) {
    List<ChatMessage> messages = chatService.getMessagesByRoomId(roomId);
    List<ChatMessageDto> messageDtos = messages.stream()
            .map(msg -> new ChatMessageDto(msg.getChatRoom().getId(), msg.getAuthor().getMember().getId(), msg.getContent()))
            .collect(Collectors.toList());
    return ResponseEntity.ok(messageDtos);
  }
}
