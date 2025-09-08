package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.chatting.ChatMember;
import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.repository.ChatMemberRepository;
import com.culturemate.culturemate_api.repository.ChatMessageRepository;
import com.culturemate.culturemate_api.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMemberRepository chatMemberRepository;
  private final MemberService memberService;

  // 채팅방 생성
  @Transactional
  public ChatRoom createChatRoom(String name, Together together) {
    ChatRoom chatRoom = ChatRoom.builder()
      .roomName(name)
      .together(together)
      .build();
    return chatRoomRepository.save(chatRoom);
  }

  // 모든 채팅방 검색
  public List<ChatRoom> findAllRoom() {
    return chatRoomRepository.findAll();
  }

  // 특정 채팅방 검색
  public ChatRoom findById(Long roomId) {
    return chatRoomRepository.findById(roomId)
      .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다: " + roomId));
  }

  // 채팅방 멤버 추가
  @Transactional
  public void addMemberToRoom(Long roomId, Long memberId) {
    ChatRoom chatRoom = findById(roomId);
    Member member = memberService.findById(memberId);

    if (chatMemberRepository.findByChatRoomAndMember(chatRoom, member).isPresent()) {
      return;
    }

    ChatMember chatMember = ChatMember.builder()
      .chatRoom(chatRoom)
      .member(member)
      .build();
    chatMemberRepository.save(chatMember);
  }

  // 특정 모집글의 톡방에 멤버 추가
  @Transactional
  public void addMemberToRoomByTogether(Together together, Long memberId) {
    chatRoomRepository.findByTogether(together).ifPresent(chatRoom ->
      addMemberToRoom(chatRoom.getId(), memberId)
    );
  }

  // 채팅방에서 멤버 제거
  @Transactional
  public void removeMemberFromRoom(Long roomId, Long memberId) {
    ChatRoom chatRoom = findById(roomId);
    Member member = memberService.findById(memberId);
    
    chatMemberRepository.deleteByChatRoomAndMember(chatRoom, member);
  }

  // 특정 모집글의 톡방에서 멤버 제거
  @Transactional
  public void removeMemberFromRoomByTogether(Together together, Long memberId) {
    chatRoomRepository.findByTogether(together).ifPresent(chatRoom ->
      removeMemberFromRoom(chatRoom.getId(), memberId)
    );
  }

  // 채팅
  @Transactional
  public ChatMessage sendMessage(Long roomId, Long senderId, String content) {
    // 발신자 조회
    Member sender = memberService.findById(senderId);
    
    // 채팅방 조회
    ChatRoom room = findById(roomId);
    
    // 채팅방 멤버 권한 확인
    ChatMember author = chatMemberRepository.findByChatRoomAndMember(room, sender)
        .orElseThrow(() -> new IllegalArgumentException("해당 채팅방의 참여자가 아닙니다"));
    
    // 메시지 저장
    ChatMessage message = ChatMessage.builder()
        .chatRoom(room)
        .author(author)
        .content(content)
        .build();
    
    return chatMessageRepository.save(message);
  }

  // 이전대화 불러오기
  public List<ChatMessage> getMessagesByRoomId(Long roomId) {
    return chatMessageRepository.findByChatRoomId(roomId);
  }

  // 특정 멤버가 참여중인 채팅방 목록 조회
  public List<ChatRoom> findRoomsByMember(Long memberId) {
    Member member = memberService.findById(memberId);
    List<ChatMember> chatMembers = chatMemberRepository.findByMember(member);
    return chatMembers.stream()
        .map(ChatMember::getChatRoom)
        .collect(Collectors.toList());
  }
}
