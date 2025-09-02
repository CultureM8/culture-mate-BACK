package com.culturemate.culturemate_api.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class RegionInitializer {

  public static void regionInit() throws IOException {
    try(InputStream jsonFile = Thread.currentThread()
                            .getContextClassLoader()
                            .getResourceAsStream("db/korea_regions.json"))
    {
      if (jsonFile == null) { throw new IllegalStateException("파일을 찾을 수 없음"); }
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Map<String, List<String>>> regions
        = mapper.readValue(jsonFile, new TypeReference<Map<String, Map<String, List<String>>>>() {});

      StringBuilder stb = new StringBuilder();
      String level1, level2, level3;
      stb.append("null").append(" ").append("null").append(" ").append("null").append("\n");

      for (Map.Entry<String, Map<String, List<String>>> level1Val : regions.entrySet()) {
        level1 = level1Val.getKey();
        
        // level1이 "전체"이면 null로 처리
        if ("전체".equals(level1)) {
          level1 = null;
          continue;
        }
        
        for (Map.Entry<String, List<String>> level2Val : level1Val.getValue().entrySet()) {
          level2 = level2Val.getKey();
          List<String> level3List = level2Val.getValue();
          
          // level2가 "전체"이고 하위에 값이 없으면 null로 처리
          if ("전체".equals(level2) && (level3List == null || level3List.isEmpty())) {
            level2 = null;
            level3 = null;
            stb.append(level1).append(" ").append("null").append(" ").append("null").append("\n");
            continue;
          }
          
          for (String level3Val : level3List) {
            level3 = level3Val;
            // level3가 "전체"이면 null로 처리
            if ("전체".equals(level3)) {
              level3 = null;
            }
            stb.append(level1).append(" ").append(level2).append(" ").append(level3).append("\n");
          }
        }
      }

      System.out.println(stb.toString());
    }
  }
}
