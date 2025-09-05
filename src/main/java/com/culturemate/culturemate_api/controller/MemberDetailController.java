package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberDetail;
import com.culturemate.culturemate_api.dto.MemberDetailRequestDto;
import com.culturemate.culturemate_api.dto.MemberDetailResponseDto;
import com.culturemate.culturemate_api.domain.Image;
import com.culturemate.culturemate_api.domain.ImageTarget;
import com.culturemate.culturemate_api.service.ImageService;
import com.culturemate.culturemate_api.service.ImagePermissionService;
import com.culturemate.culturemate_api.service.MemberDetailService;
import com.culturemate.culturemate_api.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
  public ResponseEntity<MemberDetailResponseDto> getByMemberId(@PathVariable Long memberId) {
    MemberDetail memberDetail = memberDetailService.findByMemberId(memberId);
    return ResponseEntity.ok(MemberDetailResponseDto.from(memberDetail));  // HTTP 200 + MemberDetailResponseDto 반환
  }

  // 생성
  @PostMapping("/{memberId}")
  public ResponseEntity<MemberDetailResponseDto> add(@PathVariable Long memberId,
                                                     @Valid @RequestBody MemberDetailRequestDto dto) {
    Member member = memberService.findById(memberId);
    MemberDetail created = memberDetailService.create(member, dto);
    return ResponseEntity.status(201).body(MemberDetailResponseDto.from(created));  // HTTP 201 Created + 데이터 반환
  }

  // 수정
  @PutMapping("/{memberId}")
  public ResponseEntity<MemberDetailResponseDto> modify(@PathVariable Long memberId,
                                                       @Valid @RequestBody MemberDetailRequestDto dto) {
    MemberDetail updated = memberDetailService.update(memberId, dto);
    return ResponseEntity.ok(MemberDetailResponseDto.from(updated));  // HTTP 200 OK + 데이터 반환
  }

  // 삭제
  @DeleteMapping("/{memberId}")
  public ResponseEntity<Void> remove(@PathVariable Long memberId) {
    memberDetailService.delete(memberId);
    return ResponseEntity.noContent().build();  // HTTP 204 No Content
  }

  // =========================== 이미지 관련 API ===========================
  
  // 프로필 이미지 업로드/수정 (썸네일 + 메인)
  @PatchMapping("/{memberId}/profile-image")
  public ResponseEntity<Void> updateProfileImage(@PathVariable Long memberId,
                                                 @RequestParam("image") MultipartFile imageFile) {
    memberDetailService.updateProfileImage(memberId, imageFile);
    return ResponseEntity.ok().build();
  }

  // 배경 이미지 업로드/수정
  @PatchMapping("/{memberId}/background-image") 
  public ResponseEntity<Void> updateBackgroundImage(@PathVariable Long memberId,
                                                    @RequestParam("image") MultipartFile imageFile) {
    memberDetailService.updateBackgroundImage(memberId, imageFile);
    return ResponseEntity.ok().build();
  }

  // 프로필 이미지 삭제
  @DeleteMapping("/{memberId}/profile-image")
  public ResponseEntity<Void> deleteProfileImage(@PathVariable Long memberId) {
    memberDetailService.deleteProfileImage(memberId);
    return ResponseEntity.noContent().build();
  }

  // 배경 이미지 삭제  
  @DeleteMapping("/{memberId}/background-image")
  public ResponseEntity<Void> deleteBackgroundImage(@PathVariable Long memberId) {
    memberDetailService.deleteBackgroundImage(memberId);
    return ResponseEntity.noContent().build();
  }

  // ===== 갤러리 관리 =====

  // 갤러리 이미지 업로드 (다중)
  @PostMapping("/{memberId}/gallery")
  public ResponseEntity<Void> uploadGalleryImages(@PathVariable Long memberId,
                                                 @RequestParam("images") List<MultipartFile> images) {
    imageService.uploadMultipleImages(images, ImageTarget.MEMBER_GALLERY, memberId);
    return ResponseEntity.status(201).build();
  }

  // 갤러리 이미지 목록 조회 (경로만 반환)
  @GetMapping("/{memberId}/gallery")
  public ResponseEntity<List<String>> getGalleryImages(@PathVariable Long memberId) {
    List<Image> images = imageService.getImagesByTargetTypeAndId(ImageTarget.MEMBER_GALLERY, memberId);
    List<String> imagePaths = images.stream()
        .map(Image::getPath)
        .toList();
    return ResponseEntity.ok(imagePaths);
  }

  // 갤러리 이미지 삭제 (경로 기반)
  @DeleteMapping("/{memberId}/gallery")
  public ResponseEntity<Void> deleteGalleryImage(@PathVariable Long memberId,
                                                 @RequestParam String imagePath) {
    // 1. 권한 검증
    imagePermissionService.validateDeletePermission(imagePath, memberId);
    
    // 2. 이미지 삭제
    imageService.deleteImageByPath(imagePath);
    
    return ResponseEntity.noContent().build();
  }

  // 갤러리 이미지 전체 삭제
  @DeleteMapping("/{memberId}/gallery/all")
  public ResponseEntity<Void> deleteAllGalleryImages(@PathVariable Long memberId) {
    Long requesterId = memberId; // 명확성을 위해 변수명 분리
    
    // 1. 권한 검증
    imagePermissionService.validateDeleteAllPermission(ImageTarget.MEMBER_GALLERY, memberId, requesterId);
    
    // 2. 이미지 삭제
    imageService.deleteAllImagesByTarget(ImageTarget.MEMBER_GALLERY, memberId);
    
    return ResponseEntity.noContent().build();
  }
}
