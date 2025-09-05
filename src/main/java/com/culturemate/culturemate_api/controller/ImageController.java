//package com.culturemate.culturemate_api.controller;
//
//import com.culturemate.culturemate_api.dto.ImageUploadRequestDto;
//import com.culturemate.culturemate_api.service.ImageService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/api/v1/image")
//@RequiredArgsConstructor
//public class ImageController {
//
//  private final ImageService imageService;
//
//  @GetMapping
//  public String getImage() {
//    return "imageUpload";
//  }
//
//  @PostMapping("/upload")
//  @ResponseBody // 문자로 리턴
//  public String uploadMainAndThumbnail(@Valid @ModelAttribute ImageUploadRequestDto requestDto) {
//    // 메인+썸네일 업로드 (TOGETHER, EVENT, MEMBER_PROFILE, MEMBER_BACKGROUND)
//    try {
//      MultipartFile file = requestDto.getFiles().get(0);
//      String[] paths = imageService.uploadMainAndThumbnail(file,
//                                                          requestDto.getImageTarget(),
//                                                          requestDto.getTargetId());
//      return "썸네일+메인 이미지 업로드 성공";
//    } catch (Exception e) {
//      return "업로드 실패: " + e.getMessage();
//    }
//  }
//
//  @PostMapping("/upload/multiple")
//  @ResponseBody
//  public String uploadMultipleImages(@Valid @ModelAttribute ImageUploadRequestDto requestDto) {
//    // 다중 컨텐츠 이미지 업로드 (EVENT_CONTENT, BOARD_CONTENT, MEMBER_GALLERY, CHAT_MESSAGE)
//    imageService.uploadMultipleImages(requestDto.getFiles(),
//                                    requestDto.getImageTarget(),
//                                    requestDto.getTargetId());
//    return "다중 이미지 업로드 성공 - " + requestDto.getFiles().size() + "개 파일 처리됨";
//  }
//
//}
