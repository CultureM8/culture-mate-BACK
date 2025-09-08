package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.dto.CustomUser;
import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.chat.ChatMessageDto;
import com.culturemate.culturemate_api.dto.chat.ChatRoomDto;
import com.culturemate.culturemate_api.service.ChatRoomService;
import com.culturemate.culturemate_api.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;
  private final MemberService memberService;

  @GetMapping("/rooms")
  public String rooms(Model model) {
    return "/chat/rooms";
  }

  @GetMapping("/rooms/list")
  @ResponseBody
  public ResponseEntity<List<ChatRoomDto.Response>> room() {
    List<ChatRoom> chatRooms = chatRoomService.findAllRoom();
    List<ChatRoomDto.Response> response = chatRooms.stream()
      .map(ChatRoomDto.Response::from)
      .collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/room")
  @ResponseBody
  public ResponseEntity<ChatRoomDto.Response> createRoom(@RequestParam String name) {
    ChatRoom chatRoom = chatRoomService.createChatRoom(name, null);
    return ResponseEntity.ok(ChatRoomDto.Response.from(chatRoom));
  }

  @GetMapping("/room/enter/{roomId}")
  public String roomDetail(Model model, @PathVariable Long roomId, @AuthenticationPrincipal CustomUser customUser) {
    ChatRoom room = chatRoomService.findById(roomId);

    Member member = memberService.findByLoginId(customUser.getUsername());

    model.addAttribute("room", room);
    model.addAttribute("memberId", member.getId());

    chatRoomService.addMemberToRoom(roomId, member.getId());

    return "/chat/room";
  }

  @GetMapping("/room/{roomId}")
  @ResponseBody
  public ResponseEntity<ChatRoomDto.ResponseDetail> roomInfo(@PathVariable Long roomId) {
    ChatRoom chatRoom = chatRoomService.findById(roomId);
    return ResponseEntity.ok(ChatRoomDto.ResponseDetail.from(chatRoom));
  }

  // 이전 대화내역 불러오기
  @GetMapping("/room/{roomId}/messages")
  @ResponseBody
  public ResponseEntity<List<ChatMessageDto>> getPreviousMessages(@PathVariable Long roomId) {
    List<ChatMessage> messages = chatRoomService.getMessagesByRoomId(roomId);
    List<ChatMessageDto> messageDtos = messages.stream()
      .map(ChatMessageDto::from)
      .collect(Collectors.toList());
    return ResponseEntity.ok(messageDtos);
  }
}
