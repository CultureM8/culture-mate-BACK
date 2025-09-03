package com.culturemate.culturemate_api.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long roomId;
    private Long senderId;    // 메시지 작성자 ID (클라이언트가 보내는 값),  DB 저장용 author
    private String content;
}
