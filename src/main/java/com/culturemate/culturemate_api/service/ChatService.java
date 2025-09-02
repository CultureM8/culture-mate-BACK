package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.repository.ChatMessageRepository;
import com.culturemate.culturemate_api.repository.ChatRoomRepository;
import com.culturemate.culturemate_api.repository.TogetherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TogetherRepository togetherRepository; // '같이해요' 정보를 가져오기 위해 필요

    /**
     * 채팅방 생성
     * @param roomName
     * @param togetherId
     * @return
     */
    @Transactional
    public ChatRoom createChatRoom(String roomName, Long togetherId) {
        Together together = togetherRepository.findById(togetherId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid togetherId: " + togetherId));

        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .together(together)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    /**
     * 메시지 저장
     * @param chatMessage
     * @return
     */
    @Transactional
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    /**
     * 특정 채팅방의 모든 메시지 조회
     * @param roomId
     * @return
     */
    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        // TODO: 페이징 처리 추가 고려
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid roomId: " + roomId));
        // return chatMessageRepository.findByChatRoom(chatRoom);
        return null; // 임시
    }

    /**
     * 채팅방 ID로 채팅방 조회
     * @param roomId
     * @return
     */
    public ChatRoom findRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid roomId: " + roomId));
    }
}
