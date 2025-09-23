package com.culturemate.culturemate_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${custom.path.upload.default}")
  private String uploadPath;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // /images/** 경로를 실제 파일 시스템 경로로 매핑
    registry.addResourceHandler("/images/**")
        .addResourceLocations("file:" + uploadPath + "/")
        .setCachePeriod(3600); // 1시간 캐시
  }
}