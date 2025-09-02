package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.chat.ChatMessageDto;
import com.culturemate.culturemate_api.repository.MemberRepository;
import com.culturemate.culturemate_api.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MemberRepository memberRepository; // 추가

    /**
     * 특정 채팅방의 메시지 목록 조회 (클라이언트가 주기적으로 호출)
     * @param roomId
     * @return
     */
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long roomId) {
        List<ChatMessage> messages = chatService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 특정 채팅방에 메시지 전송
     * @param roomId
     * @param messageDto
     * @return
     */
    @PostMapping("/room/{roomId}/messages")
    public ResponseEntity<ChatMessage> sendMessage(@PathVariable Long roomId, @RequestBody ChatMessageDto messageDto) {
        // DTO에서 작성자 ID를 이용해 Member 엔티티를 찾음
        Member sender = memberRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender Id: " + messageDto.getSenderId()));

        // 채팅방을 찾음
        ChatRoom room = chatService.findRoomById(roomId);

        // ChatMessage 엔티티 생성 및 값 설정
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(room);
        chatMessage.setAuthor(sender);
        chatMessage.setContent(messageDto.getContent());

        // 메시지 저장
        ChatMessage savedMessage = chatService.saveMessage(chatMessage);
        return ResponseEntity.ok(savedMessage);
    }
}
