package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.inquiry.Inquiry;
import com.culturemate.culturemate_api.domain.inquiry.InquiryCategory;
import com.culturemate.culturemate_api.domain.inquiry.InquiryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

public class InquiryDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "제목을 입력해주세요.")
        private String title;

        @NotBlank(message = "내용을 입력해주세요.")
        private String content;

        @NotNull(message = "카테고리를 선택해주세요.")
        private InquiryCategory category;
    }

    @Getter
    @Builder
    public static class Response {
        private Long inquiryId;
        private String title;
        private String content;
        private MemberDto.ProfileResponse author;
        private InquiryCategory category;
        private InquiryStatus status;
        private Instant createdAt;
        private InquiryAnswerDto.Response answer;

        public static Response from(Inquiry inquiry) {
            return Response.builder()
                    .inquiryId(inquiry.getId())
                    .title(inquiry.getTitle())
                    .content(inquiry.getContent())
                    .author(MemberDto.ProfileResponse.from(inquiry.getAuthor()))
                    .category(inquiry.getCategory())
                    .status(inquiry.getStatus())
                    .createdAt(inquiry.getCreatedAt())
                    .answer(inquiry.getAnswer() != null ? InquiryAnswerDto.Response.from(inquiry.getAnswer()) : null)
                    .build();
        }
    }
    
    @Getter
    @Builder
    public static class ListResponse {
        private Long inquiryId;
        private String title;
        private MemberDto.ProfileResponse author;
        private InquiryCategory category;
        private InquiryStatus status;
        private Instant createdAt;

        public static ListResponse from(Inquiry inquiry) {
            return ListResponse.builder()
                    .inquiryId(inquiry.getId())
                    .title(inquiry.getTitle())
                    .author(MemberDto.ProfileResponse.from(inquiry.getAuthor()))
                    .category(inquiry.getCategory())
                    .status(inquiry.getStatus())
                    .createdAt(inquiry.getCreatedAt())
                    .build();
        }
    }
}