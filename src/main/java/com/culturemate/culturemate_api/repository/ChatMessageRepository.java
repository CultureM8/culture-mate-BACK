package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);
}
