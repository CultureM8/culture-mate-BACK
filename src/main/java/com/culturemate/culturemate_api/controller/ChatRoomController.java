package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.CustomUser;
import com.culturemate.culturemate_api.dto.ChatMessageDto;
import com.culturemate.culturemate_api.dto.ChatRoomDto;
import com.culturemate.culturemate_api.service.ChatRoomService;
import com.culturemate.culturemate_api.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatroom")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;
  private final MemberService memberService;

  // 모든 채팅방 조회 (관리자용)
  @GetMapping
  public ResponseEntity<List<ChatRoomDto.Response>> getAllRooms() {
    List<ChatRoom> chatRooms = chatRoomService.findAllRoom();
    List<ChatRoomDto.Response> response = chatRooms.stream()
      .map(ChatRoomDto.Response::from)
      .collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  // 채팅방 생성
  @PostMapping("/create")
  public ResponseEntity<ChatRoomDto.Response> createRoom(@RequestParam String name) {
    ChatRoom chatRoom = chatRoomService.createChatRoom(name, null);
    return ResponseEntity.ok(ChatRoomDto.Response.from(chatRoom));
  }

  // 특정 사용자의 채팅방 불러오기 (로그인한 회원)
  @GetMapping("/my")
  public ResponseEntity<List<ChatRoomDto.Response>> getMyChatRooms(@AuthenticationPrincipal CustomUser principal) {
    Member member = memberService.findByLoginId(principal.getUsername());
    List<ChatRoom> chatRooms = chatRoomService.findRoomsByMember(member.getId());
    List<ChatRoomDto.Response> response = chatRooms.stream()
        .map(ChatRoomDto.Response::from)
        .collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  // 채팅방 입장 (새로운 채팅방)
  @PostMapping("/{roomId}/join")
  public ResponseEntity<Void> joinRoom(@PathVariable Long roomId,
                                       @AuthenticationPrincipal CustomUser principal) {
    Member member = memberService.findByLoginId(principal.getUsername());
    chatRoomService.addMemberToRoom(roomId, member.getId());
    return ResponseEntity.noContent().build();
  }

  // 채팅방 접속 (내가 참여중인 채팅방)
  @GetMapping("/{roomId}")
  public ResponseEntity<ChatRoomDto.ResponseDetail> getRoom(@PathVariable Long roomId) {
    ChatRoom chatRoom = chatRoomService.findById(roomId);
    return ResponseEntity.ok(ChatRoomDto.ResponseDetail.from(chatRoom));
  }

  // 이전 대화내역 불러오기
  @GetMapping("/{roomId}/messages")
  public ResponseEntity<List<ChatMessageDto>> getPreviousMessages(@PathVariable Long roomId) {
    List<ChatMessage> messages = chatRoomService.getMessagesByRoomId(roomId);
    List<ChatMessageDto> messageDtos = messages.stream()
      .map(ChatMessageDto::from)
      .collect(Collectors.toList());
    return ResponseEntity.ok(messageDtos);
  }

  // 채팅방 나가기
  @DeleteMapping("/{roomId}/leave")
  public ResponseEntity<Void> leaveChatRoom(@PathVariable Long roomId,
                                            @AuthenticationPrincipal CustomUser principal) {
    Member member = memberService.findByLoginId(principal.getUsername());
    chatRoomService.removeMemberFromRoom(roomId, member.getId());
    return ResponseEntity.noContent().build();
  }

}
