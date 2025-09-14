package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.dto.ChatMessageDto;
import com.culturemate.culturemate_api.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final ChatRoomService chatRoomService;
  private final SimpMessagingTemplate messagingTemplate;


  // 메시지 전송 엔드포인트
  @MessageMapping("/chatroom/{roomId}/send")
  public void sendMessage(@DestinationVariable Long roomId,
                                ChatMessageDto messageDto,
                                Principal principal) {

    // LOG: 수신된 메시지 정보 출력
    System.out.println("=== [" + java.time.LocalDateTime.now() + "] Received message in /chatroom/" + roomId + "/send ===");
    System.out.println("  - Room ID (path): " + roomId);
    System.out.println("  - Principal: " + (principal != null ? principal.getClass().getSimpleName() : "null"));

    // Principal에서 AuthenticatedUser 추출
    AuthenticatedUser user = null;
    if (principal instanceof UsernamePasswordAuthenticationToken) {
      UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
      if (auth.getPrincipal() instanceof AuthenticatedUser) {
        user = (AuthenticatedUser) auth.getPrincipal();
        System.out.println("  - AuthenticatedUser 추출 성공");
      } else {
        System.out.println("  - Principal 타입: " + (auth.getPrincipal() != null ? auth.getPrincipal().getClass().getSimpleName() : "null"));
      }
    }

    System.out.println("  - User: " + (user != null ? user.getUsername() : "null"));
    System.out.println("  - User ID: " + (user != null ? user.getMemberId() : "null"));

    // AuthenticatedUser 상세 정보 출력
    if (user != null) {
      System.out.println("  - AuthenticatedUser 상세 정보:");
      System.out.println("    * memberId: " + user.getMemberId());
      System.out.println("    * loginId: " + user.getLoginId());
      System.out.println("    * nickname: " + user.getNickname());
      System.out.println("    * role: " + user.getRole());
      System.out.println("    * status: " + user.getStatus());
      System.out.println("    * username (inherited): " + user.getUsername());
    } else {
      System.out.println("  - AuthenticatedUser 객체가 null입니다!");
    }
    if (messageDto != null) {
      System.out.println("  - Message DTO fields:");
      System.out.println("    * id: " + messageDto.getId());
      System.out.println("    * roomId: " + messageDto.getRoomId());
      System.out.println("    * senderId: " + messageDto.getSenderId());
      System.out.println("    * content: '" + messageDto.getContent() + "'");
      System.out.println("    * createdAt: " + messageDto.getCreatedAt());
    } else {
      System.out.println("  - Message DTO: null");
    }

    try {
      // 1. DTO 검증
      if (messageDto == null) {
        throw new IllegalArgumentException("메시지 데이터가 없습니다");
      }

      if (messageDto.getContent() == null || messageDto.getContent().trim().isEmpty()) {
        throw new IllegalArgumentException("메시지 내용이 필요합니다");
      }

      // 2. 사용자 인증 확인
      if (user == null) {
        throw new IllegalArgumentException("인증이 필요합니다");
      }

      Long senderId = user.getMemberId();
      System.out.println("  - Using authenticated user ID: " + senderId);

      // 3. roomId 일치 검증 (path variable vs DTO)
      Long effectiveRoomId = roomId;
      if (messageDto.getRoomId() != null && !messageDto.getRoomId().equals(roomId)) {
        System.out.println("  - WARNING: roomId mismatch - using path variable: " + roomId);
      }

      // 4. 채팅방 참여 권한 검증 및 메시지 저장 (서비스에서 처리)
      ChatMessage savedMessage = chatRoomService.sendMessage(
          effectiveRoomId, senderId, messageDto.getContent().trim()
      );

      System.out.println("  - Message saved successfully: " + savedMessage.getId());

      // 5. 채팅방 구독자들에게 메시지 전송
      ChatMessageDto responseDto = ChatMessageDto.from(savedMessage);
      messagingTemplate.convertAndSend(
        "/topic/chatroom/" + effectiveRoomId,
        responseDto
      );

      System.out.println("  - Message broadcasted to /topic/chatroom/" + effectiveRoomId);

    } catch (Exception e) {
      System.err.println("  - ERROR in sendMessage: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }

  // 채팅방 입장 알림
  @MessageMapping("/chatroom/{roomId}/join")
  public void joinRoom(@DestinationVariable Long roomId,
                      @AuthenticationPrincipal AuthenticatedUser user) {
    if (user == null) {
      throw new IllegalArgumentException("인증이 필요합니다");
    }

    // 권한 검증 및 멤버 추가
    chatRoomService.checkAndAddMemberToRoom(roomId, user.getMemberId());

    // 입장 알림
    messagingTemplate.convertAndSend("/topic/chatroom/" + roomId,
        Map.of("type", "JOIN", "message", user.getNickname() + "님이 입장했습니다"));
  }

}
