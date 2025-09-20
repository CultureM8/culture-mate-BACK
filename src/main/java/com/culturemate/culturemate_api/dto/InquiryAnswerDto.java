package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.inquiry.InquiryAnswer;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

public class InquiryAnswerDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "답변 내용을 입력해주세요.")
        private String content;
    }

    @Getter
    @Builder
    public static class Response {
        private Long answerId;
        private String content;
        private MemberDto.ProfileResponse author;
        private Instant createdAt;
        private Instant updatedAt;

        public static Response from(InquiryAnswer answer) {
            return Response.builder()
                    .answerId(answer.getId())
                    .content(answer.getContent())
                    .author(MemberDto.ProfileResponse.from(answer.getAuthor()))
                    .createdAt(answer.getCreatedAt())
                    .updatedAt(answer.getUpdatedAt())
                    .build();
        }
    }
}