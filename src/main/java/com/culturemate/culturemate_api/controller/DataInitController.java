package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.init.AdminInitializer;
import com.culturemate.culturemate_api.init.MemberInitializer;
import com.culturemate.culturemate_api.init.RegionInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/init")
@RequiredArgsConstructor
public class DataInitController {

  private final RegionInitializer regionInitializer;
  private final AdminInitializer adminInitializer;
  private final MemberInitializer memberInitializer;

  /**
   * 지역 데이터 초기화
   */
  @PostMapping("/regions")
  public ResponseEntity<Map<String, String>> initRegions() {
    try {
      regionInitializer.regionInit();
      return ResponseEntity.ok(Map.of("message", "지역 데이터 초기화 완료"));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of("message", "지역 데이터 초기화 실패: " + e.getMessage()));
    }
  }

  /**
   * 관리자 데이터 초기화
   */
  @PostMapping("/admin")
  public ResponseEntity<Map<String, String>> initAdmin() {
    try {
      adminInitializer.adminInit();
      return ResponseEntity.ok(Map.of("message", "관리자 데이터 초기화 완료"));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of("message", "관리자 데이터 초기화 실패: " + e.getMessage()));
    }
  }

  /**
   * 더미 회원 데이터 초기화
   */
  @PostMapping("/members")
  public ResponseEntity<Map<String, String>> initMembers(@RequestParam(defaultValue = "20") int count) {
    try {
      memberInitializer.memberInit(count);
      return ResponseEntity.ok(Map.of("message", count + "개의 더미 회원 데이터 초기화 완료"));
    } catch (Exception e) {
      return ResponseEntity.ok(Map.of("message", "더미 회원 데이터 초기화 실패: " + e.getMessage()));
    }
  }
}