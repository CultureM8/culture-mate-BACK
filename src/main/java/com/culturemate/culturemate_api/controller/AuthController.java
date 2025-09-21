package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.config.JwtUtil;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.dto.MemberDto;
import com.culturemate.culturemate_api.service.AuthService;
import com.culturemate.culturemate_api.service.MemberService;
import jakarta.validation.Valid;
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

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Auth API", description = "인증 및 인가 관리 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final AuthService authService;
  private final MemberService memberService;

  @Operation(summary = "로그인", description = "회원 로그인을 수행하고 JWT 토큰을 반환합니다")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "로그인 성공 - JWT 토큰과 사용자 정보 반환"),
    @ApiResponse(responseCode = "401", description = "인증 실패")
  })
  @PostMapping("/login")
  public ResponseEntity<?> login(
    @Parameter(description = "로그인 정보", required = true) @RequestBody MemberDto.Login loginRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword())
      );

      AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
      
      // JWT 토큰 생성
      String token = jwtUtil.generateToken(authenticatedUser);
      
      MemberDto.Response userResponse = MemberDto.Response.builder()
        .id(authenticatedUser.getMemberId())
        .loginId(authenticatedUser.getUsername())
        .role(authenticatedUser.getRole())
        .build();

      // 토큰과 사용자 정보 함께 반환
      Map<String, Object> response = new HashMap<>();
      response.put("token", token);
      response.put("user", userResponse);
      response.put("message", "로그인 성공");

      return ResponseEntity.ok(response);

    } catch (AuthenticationException e) {
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("status", "error");
      errorResponse.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
      return ResponseEntity.status(401).body(errorResponse);
    }
  }

  @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "가입 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
    @ApiResponse(responseCode = "409", description = "이미 존재하는 회원")
  })
  @PostMapping("/register")
  public ResponseEntity<MemberDto.Response> register(
    @Parameter(description = "회원 가입 정보", required = true) @Valid @RequestBody MemberDto.Register registerDto) {
    Member savedMember = memberService.register(registerDto);
    return ResponseEntity.status(201).body(MemberDto.Response.from(savedMember));
  }
}
