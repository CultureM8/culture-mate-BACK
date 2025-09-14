package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.config.JwtUtil;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Auth API", description = "인증 및 인가 관리 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthApiController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  @Operation(summary = "로그인", description = "회원 로그인을 수행하고 JWT 토큰을 반환합니다")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "로그인 성공 - JWT 토큰과 사용자 정보 반환"),
    @ApiResponse(responseCode = "401", description = "인증 실패")
  })
  @PostMapping("/login")
  public ResponseEntity<?> login(
    @Parameter(description = "로그인 정보", required = true) @RequestBody MemberDto.Login loginRequest,
    HttpServletResponse response) {
    try {
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword())
      );

      AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
      
      // JWT 토큰 생성
      String token = jwtUtil.generateToken(authenticatedUser);
      
      // Cookie에 토큰 설정 (SSR용)
      Cookie tokenCookie = new Cookie("accessToken", token);
      tokenCookie.setHttpOnly(true); // XSS 공격 방지
      tokenCookie.setSecure(false); // 개발 환경에서는 false, 프로덕션에서는 true
      tokenCookie.setPath("/");
      tokenCookie.setMaxAge(24 * 60 * 60); // 24시간
      response.addCookie(tokenCookie);
      
      MemberDto.Response userResponse = MemberDto.Response.builder()
        .id(authenticatedUser.getMemberId())
        .loginId(authenticatedUser.getUsername())
        .role(authenticatedUser.getRole())
        .build();

      // 토큰과 사용자 정보 함께 반환
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("token", token);
      responseBody.put("user", userResponse);
      responseBody.put("message", "로그인 성공");

      return ResponseEntity.ok(responseBody);

    } catch (AuthenticationException e) {
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("status", "error");
      errorResponse.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
      return ResponseEntity.status(401).body(errorResponse);
    }
  }

  @Operation(summary = "로그아웃", description = "로그아웃을 수행하고 쿠키를 삭제합니다")
  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
    // Cookie 삭제
    Cookie tokenCookie = new Cookie("accessToken", null);
    tokenCookie.setHttpOnly(true);
    tokenCookie.setSecure(false);
    tokenCookie.setPath("/");
    tokenCookie.setMaxAge(0); // 즉시 삭제
    response.addCookie(tokenCookie);

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("message", "로그아웃 성공");
    return ResponseEntity.ok(responseBody);
  }
}
