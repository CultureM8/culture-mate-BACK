package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.Image;
import com.culturemate.culturemate_api.domain.inquiry.Inquiry;
import com.culturemate.culturemate_api.domain.inquiry.InquiryCategory;
import com.culturemate.culturemate_api.domain.inquiry.InquiryStatus;
import com.culturemate.culturemate_api.domain.member.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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
        private List<String> imageUrls;
        private Role role;

        public static Response from(Inquiry inquiry) {
            return Response.builder()
                    .inquiryId(inquiry.getId())
                    .title(inquiry.getTitle())
                    .content(inquiry.getContent())
                    .author(MemberDto.ProfileResponse.from(inquiry.getAuthor()))
                    .role(inquiry.getAuthor().getRole())
                    .category(inquiry.getCategory())
                    .status(inquiry.getStatus())
                    .createdAt(inquiry.getCreatedAt())
                    .answer(inquiry.getAnswer() != null ? InquiryAnswerDto.Response.from(inquiry.getAnswer()) : null)
                    .imageUrls(inquiry.getImages() != null
                    ? inquiry.getImages().stream().map(Image::getPath).collect(Collectors.toList())
                    : List.of())
                    .build();
        }
    }
    
    @Getter
    @Builder
    public static class ListResponse {
        private Long inquiryId;
        private String title;
        private String content;
        private MemberDto.ProfileResponse author;
        private Role role;
        private InquiryCategory category;
        private InquiryStatus status;
        private Instant createdAt;
        private InquiryAnswerDto.Response answer;
        private List<String> imageUrls;

        public static ListResponse from(Inquiry inquiry) {
            return ListResponse.builder()
                    .inquiryId(inquiry.getId())
                    .title(inquiry.getTitle())
                    .content(inquiry.getContent())
                    .author(MemberDto.ProfileResponse.from(inquiry.getAuthor()))
                    .role(inquiry.getAuthor().getRole())
                    .category(inquiry.getCategory())
                    .status(inquiry.getStatus())
                    .createdAt(inquiry.getCreatedAt())
                    .answer(inquiry.getAnswer() != null
                    ? InquiryAnswerDto.Response.from(inquiry.getAnswer())
                    : null)
                    .imageUrls(inquiry.getImages() != null
                    ? inquiry.getImages().stream()
                    .map(Image::getPath)
                    .collect(Collectors.toList())
                    : List.of())
                    .build();
        }
    }
}