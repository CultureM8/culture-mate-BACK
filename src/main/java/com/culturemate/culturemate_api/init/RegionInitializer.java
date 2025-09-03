package com.culturemate.culturemate_api.init;

import com.culturemate.culturemate_api.service.RegionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
public class RegionInitializer {

  private final RegionService regionService;

  @Autowired
  public RegionInitializer(RegionService regionService) {
    this.regionService = regionService;
  }

  public void regionInit() {
    try(InputStream jsonFile = Thread.currentThread()
                            .getContextClassLoader()
                            .getResourceAsStream("db/KoreaRegions.json")) {

      if (jsonFile == null) { throw new IllegalStateException("파일을 찾을 수 없음"); }
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Map<String, List<String>>> regions
        = mapper.readValue(jsonFile, new TypeReference<Map<String, Map<String, List<String>>>>() {});

      // 1단계: 모든 지역 데이터를 Set으로 수집 (중복 자동 제거)
      Set<List<String>> uniqueRegions = new HashSet<>();
      String level1, level2, level3;

      for (Map.Entry<String, Map<String, List<String>>> level1Val : regions.entrySet()) {
        level1 = level1Val.getKey();
        
        for (Map.Entry<String, List<String>> level2Val : level1Val.getValue().entrySet()) {
          level2 = level2Val.getKey();
          List<String> level3List = level2Val.getValue();
          
          // level2가 "전체"이면 건너뛰기
          if ("전체".equals(level2)) {
            continue;
          }
          
          for (String level3Val : level3List) {
            level3 = level3Val;
            // level3가 "전체"이면 건너뛰기
            if ("전체".equals(level3)) {
              continue;
            }
            
            // 계층별로 분해해서 Set에 추가 (중복 자동 제거)
            uniqueRegions.add(Arrays.asList(level1, null, null));      // 1단계
            uniqueRegions.add(Arrays.asList(level1, level2, null));    // 2단계  
            uniqueRegions.add(Arrays.asList(level1, level2, level3));  // 3단계
          }
        }
      }
      
      // 2단계: Set에서 RegionService로 저장
      System.out.println("총 " + uniqueRegions.size() + "개 지역 데이터 저장 시작");
      for (List<String> regionData : uniqueRegions) {
        regionService.create(regionData.get(0), regionData.get(1), regionData.get(2));
      }

//      System.out.println(stb.toString());
      System.out.println("지역 데이터 초기화 완료");
    } catch (Exception e) {
      System.out.println("데이터 불러오기 실패\n" + e.getMessage());
    }
  }
}
