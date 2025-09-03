package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.chatting.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
}
