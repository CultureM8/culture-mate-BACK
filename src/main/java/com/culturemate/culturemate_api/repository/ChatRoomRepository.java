package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
