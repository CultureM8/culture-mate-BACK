package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.chatting.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
