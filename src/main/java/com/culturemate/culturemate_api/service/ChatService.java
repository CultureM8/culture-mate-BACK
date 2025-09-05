package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.chatting.ChatMember;
import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.repository.ChatMemberRepository;
import com.culturemate.culturemate_api.repository.ChatMessageRepository;
import com.culturemate.culturemate_api.repository.ChatRoomRepository;
import com.culturemate.culturemate_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MemberRepository memberRepository;

    public ChatRoom createChatRoom(String name, Together together) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(name)
                .together(together)
                .build();
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom createGeneralChatRoom(String name, List<Long> memberIds) {
        ChatRoom chatRoom = ChatRoom.builder()
            .roomName(name)
            .together(null) // 일반 채팅방이므로 together는 null
            .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 채팅방에 멤버 추가
        memberIds.forEach(memberId -> addMemberToRoom(savedChatRoom.getId(), memberId));

        return savedChatRoom;
    }

    public void addMemberToRoom(Long roomId, Long memberId) {
        ChatRoom chatRoom = findRoomById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room Id:" + roomId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find member with id: " + memberId));

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

    public Optional<ChatRoom> findRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId);
    }

    /**
     * 채팅 메시지 저장 (Controller 호환용)
     * @param chatMessage
     * @return
     */
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    /**
     * 이전 대화 내역 불러오기
     * @param roomId
     * @return
     */
    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByChatRoomId(roomId);
    }
}
