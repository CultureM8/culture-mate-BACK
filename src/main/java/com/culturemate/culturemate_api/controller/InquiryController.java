package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.dto.InquiryDto;
import com.culturemate.culturemate_api.service.InquiryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Inquiry API (User)", description = "1:1 문의 API (사용자용)")
@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<InquiryDto.Response> createInquiry(
            @Valid @RequestBody InquiryDto.CreateRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        
        var inquiry = inquiryService.createInquiry(request, user.getMemberId());
        return ResponseEntity.ok(InquiryDto.Response.from(inquiry));
    }

    @GetMapping("/my")
    public ResponseEntity<List<InquiryDto.ListResponse>> getMyInquiries(
            @AuthenticationPrincipal AuthenticatedUser user) {
        
        var inquiries = inquiryService.getMyInquiries(user.getMemberId());
        return ResponseEntity.ok(inquiries.stream()
                .map(InquiryDto.ListResponse::from)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryDto.Response> getInquiry(
            @PathVariable Long inquiryId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        
        var inquiry = inquiryService.getInquiry(inquiryId, user.getMemberId());
        return ResponseEntity.ok(InquiryDto.Response.from(inquiry));
    }
}
