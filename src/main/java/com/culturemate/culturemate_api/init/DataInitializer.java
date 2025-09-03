package com.culturemate.culturemate_api.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

  private final RegionInitializer regionInitializer;
  private final AdminInitializer adminInitializer;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    System.out.println("=== 데이터 초기화 시작 ===");
    
    // 지역 데이터 초기화
//    regionInitializer.regionInit();
    
    // 관리자 데이터 초기화
    adminInitializer.adminInit();
    
    System.out.println("=== 데이터 초기화 완료 ===");
  }
}