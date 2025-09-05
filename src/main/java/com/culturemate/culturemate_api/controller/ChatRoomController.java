package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.dto.CustomUser;
import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.repository.MemberRepository;
import com.culturemate.culturemate_api.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

  private final ChatService chatService;
  private final MemberRepository memberRepository;

  @GetMapping("/rooms")
  public String rooms(Model model) {
    return "/chat/rooms";
  }

  @GetMapping("/rooms/list")
  @ResponseBody
  public List<ChatRoom> room() {
    return chatService.findAllRoom();
  }

  @PostMapping("/room")
  @ResponseBody
  public ChatRoom createRoom(@RequestParam String name) {
    return chatService.createChatRoom(name, null);
  }

  @GetMapping("/room/enter/{roomId}")
  public String roomDetail(Model model, @PathVariable Long roomId, @AuthenticationPrincipal CustomUser customUser) {
    ChatRoom room = chatService.findRoomById(roomId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid room Id:" + roomId));

    Member member = memberRepository.findByLoginId(customUser.getUsername())
      .orElseThrow(() -> new IllegalArgumentException("Invalid user"));

    model.addAttribute("room", room);
    model.addAttribute("memberId", member.getId());

    chatService.addMemberToRoom(roomId, member.getId());

    return "/chat/room";
  }

  @GetMapping("/room/{roomId}")
  @ResponseBody
  public ChatRoom roomInfo(@PathVariable Long roomId) {
    return chatService.findRoomById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room Id:" + roomId));
  }
}
