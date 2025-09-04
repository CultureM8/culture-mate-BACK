package com.culturemate.culturemate_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

  @Value("${server.servlet.context-path:}")
  private String contextPath;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("CultureMate API")
            .version("1.0.0")
            .description("""
                CultureMate 플랫폼의 백엔드 API 서버
                
                ## 주요 기능
                - 🎭 **Event Management**: 문화 이벤트 관리 (CRUD, 검색, 관심 등록)
                - 👥 **Member Management**: 회원 관리 및 프로필 (상세정보, 이미지)  
                - 🤝 **Together**: 그룹 활동 및 참가자 관리
                - 💬 **Community**: 게시판 및 댓글 시스템
                - 📷 **Image Management**: 통합 이미지 업로드/관리 시스템
                - 💬 **Real-time Chat**: WebSocket 기반 실시간 채팅
                
                ## 인증
                현재 Spring Security 기반 세션 인증을 사용합니다.
                
                ## 이미지 처리
                - **단일 이미지**: 메인 + 썸네일 자동 생성
                - **다중 이미지**: 갤러리, 컨텐츠용
                - **권한 검증**: 도메인별 권한 관리
                """)
            .contact(new Contact()
                .name("CultureMate Team")
                .email("contact@culturemate.com")
                .url("https://github.com/culturemate/culture-mate-BACK"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")))
        .servers(List.of(
            new Server()
                .url("http://localhost:8080" + contextPath)
                .description("Development Server"),
            new Server()
                .url("https://api.culturemate.com" + contextPath)
                .description("Production Server (예시)")
        ));
  }
}