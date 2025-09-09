package com.culturemate.culturemate_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize ->
            authorize
                // 인증 없이 접근 가능한 엔드포인트
                .requestMatchers(
                    // 인증 API
                    "/api/v1/auth/login",
                    // 문서/개발 도구
                    "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/favicon.ico",
                    // Public 읽기 전용 API (예시)
                    "/api/v1/events",           // 이벤트 목록 조회 (공개)
                    "/api/v1/events/{id}",      // 이벤트 상세 조회 (공개)
                    "/api/v1/together",         // Together 목록 조회 (공개)
                    "/api/v1/together/{id}",    // Together 상세 조회 (공개)
                    "/api/v1/boards",           // 게시판 목록 조회 (공개)
                    "/api/v1/boards/{id}"       // 게시글 상세 조회 (공개)
                ).permitAll()
                // 그 외 모든 API는 인증 필요
                .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
