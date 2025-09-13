package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberDetail;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.dto.MemberDto;
import com.culturemate.culturemate_api.domain.Image;
import com.culturemate.culturemate_api.domain.ImageTarget;
import com.culturemate.culturemate_api.service.ImageService;
import com.culturemate.culturemate_api.service.ImagePermissionService;
import com.culturemate.culturemate_api.service.MemberDetailService;
import com.culturemate.culturemate_api.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Member Detail API", description = "회원 상세 정보(마이페이지) 관리 API")
@RestController
@RequestMapping("/api/v1/member-detail")
@RequiredArgsConstructor
public class MemberDetailController {

  private final MemberDetailService memberDetailService;
  private final MemberService memberService;
  private final ImageService imageService;
  private final ImagePermissionService imagePermissionService;

  // 상세 조회
  @GetMapping("/{memberId}")
  public ResponseEntity<MemberDto.DetailResponse> getMemberDetail(@PathVariable Long memberId) {
    MemberDetail memberDetail = memberDetailService.findByMemberId(memberId);
    return ResponseEntity.ok(MemberDto.DetailResponse.from(memberDetail));
  }

  // 생성
  @PostMapping("/{memberId}")
  public ResponseEntity<MemberDto.DetailResponse> createMemberDetail(@PathVariable Long memberId,
                                                     @Valid @RequestBody MemberDto.DetailRequest dto) {
    Member member = memberService.findById(memberId);
    MemberDetail created = memberDetailService.create(member, dto);
    return ResponseEntity.status(201).body(MemberDto.DetailResponse.from(created));  // HTTP 201 Created + 데이터 반환
  }

  // 수정
  @PutMapping("/{memberId}")
  public ResponseEntity<MemberDto.DetailResponse> updateMemberDetail(@PathVariable Long memberId,
                                                       @Valid @RequestBody MemberDto.DetailRequest dto,
                                                       @AuthenticationPrincipal AuthenticatedUser requester) {
    MemberDetail updated = memberDetailService.update(memberId, dto, requester.getMemberId());
    return ResponseEntity.ok(MemberDto.DetailResponse.from(updated));  // HTTP 200 OK + 데이터 반환
  }

  // 삭제
  @DeleteMapping("/{memberId}")
  public ResponseEntity<Void> deleteMemberDetail(@PathVariable Long memberId,
                                                 @AuthenticationPrincipal AuthenticatedUser requester) {
    memberDetailService.delete(memberId, requester.getMemberId());
    return ResponseEntity.noContent().build();  // HTTP 204 No Content
  }

  // =========================== 이미지 관련 API ===========================
  
  // 통합 이미지 업로드/수정 (프로필, 배경)
  @PatchMapping("/{memberId}/image")
  public ResponseEntity<Void> updateMemberImage(@PathVariable Long memberId,
                                          @RequestParam("image") MultipartFile imageFile,
                                          @RequestParam("type") String imageType) {
    switch (imageType.toLowerCase()) {
      case "profile":
        memberDetailService.updateProfileImage(memberId, imageFile);
        break;
      case "background":
        memberDetailService.updateBackgroundImage(memberId, imageFile);
        break;
      default:
        throw new IllegalArgumentException("지원하지 않는 이미지 타입입니다: " + imageType + " (사용 가능: profile, background)");
    }
    return ResponseEntity.ok().build();
  }

  // 통합 이미지 삭제 (프로필, 배경)
  @DeleteMapping("/{memberId}/image")
  public ResponseEntity<Void> deleteMemberImage(@PathVariable Long memberId,
                                          @RequestParam("type") String imageType) {
    switch (imageType.toLowerCase()) {
      case "profile":
        memberDetailService.deleteProfileImage(memberId);
        break;
      case "background":
        memberDetailService.deleteBackgroundImage(memberId);
        break;
      default:
        throw new IllegalArgumentException("지원하지 않는 이미지 타입입니다: " + imageType + " (사용 가능: profile, background)");
    }
    return ResponseEntity.noContent().build();
  }

  // ===== 갤러리 관리 =====

  // 갤러리 이미지 업로드 (다중)
  @PostMapping("/{memberId}/gallery")
  public ResponseEntity<Void> uploadMemberGalleryImages(@PathVariable Long memberId,
                                                 @RequestParam("images") List<MultipartFile> images) {
    imageService.uploadMultipleImages(images, ImageTarget.MEMBER_GALLERY, memberId);
    return ResponseEntity.status(201).build();
  }

  // 갤러리 이미지 목록 조회 (경로만 반환)
  @GetMapping("/{memberId}/gallery")
  public ResponseEntity<List<String>> getMemberGalleryImages(@PathVariable Long memberId) {
    List<Image> images = imageService.getImagesByTargetTypeAndId(ImageTarget.MEMBER_GALLERY, memberId);
    List<String> imagePaths = images.stream()
        .map(Image::getPath)
        .toList();
    return ResponseEntity.ok(imagePaths);
  }

  // 갤러리 이미지 삭제 (경로 기반)
  @DeleteMapping("/{memberId}/gallery")
  public ResponseEntity<Void> deleteMemberGalleryImage(@PathVariable Long memberId,
                                                 @RequestParam String imagePath) {
    // 1. 권한 검증
    imagePermissionService.validateDeletePermission(imagePath, memberId);
    
    // 2. 이미지 삭제
    imageService.deleteImageByPath(imagePath);
    
    return ResponseEntity.noContent().build();
  }

  // 갤러리 이미지 전체 삭제
  @DeleteMapping("/{memberId}/gallery/all")
  public ResponseEntity<Void> deleteAllMemberGalleryImages(@PathVariable Long memberId) {
    Long requesterId = memberId; // 명확성을 위해 변수명 분리
    
    // 1. 권한 검증
    imagePermissionService.validateDeleteAllPermission(ImageTarget.MEMBER_GALLERY, memberId, requesterId);
    
    // 2. 이미지 삭제
    imageService.deleteAllImagesByTarget(ImageTarget.MEMBER_GALLERY, memberId);
    
    return ResponseEntity.noContent().build();
  }
}
