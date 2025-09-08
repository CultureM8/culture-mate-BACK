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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMemberRepository chatMemberRepository;
  private final MemberService memberService;

  @Transactional
  public ChatRoom createChatRoom(String name, Together together) {
    ChatRoom chatRoom = ChatRoom.builder()
      .roomName(name)
      .together(together)
      .build();
    return chatRoomRepository.save(chatRoom);
  }

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

  public List<ChatRoom> findAllRoom() {
    return chatRoomRepository.findAll();
  }

  public ChatRoom findById(Long roomId) {
    return chatRoomRepository.findById(roomId)
      .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다: " + roomId));
  }
  


  /**
   * Together를 통해 채팅방을 찾아서 멤버 추가
   *
   * @param together Together 객체
   * @param memberId 추가할 멤버 ID
   */
  @Transactional
  public void addMemberToRoomByTogether(Together together, Long memberId) {
    chatRoomRepository.findByTogether(together).ifPresent(chatRoom ->
        addMemberToRoom(chatRoom.getId(), memberId)
    );
  }

  /**
   * 채팅 메시지 전송 (비즈니스 로직 포함)
   *
   * @param roomId 채팅방 ID
   * @param senderId 발신자 ID
   * @param content 메시지 내용
   * @return 저장된 ChatMessage
   */
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

  /**
   * 이전 대화 내역 불러오기
   *
   * @param roomId
   * @return
   */
  public List<ChatMessage> getMessagesByRoomId(Long roomId) {
    return chatMessageRepository.findByChatRoomId(roomId);
  }
}
