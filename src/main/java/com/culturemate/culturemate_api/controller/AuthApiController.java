package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.dto.CustomUser;
import com.culturemate.culturemate_api.dto.MemberDto;
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

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthApiController {

  private final AuthenticationManager authenticationManager;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody MemberDto.Login loginRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword())
      );

      CustomUser customUser = (CustomUser) authentication.getPrincipal();
      MemberDto.Response responseDto = MemberDto.Response.builder()
        .id(customUser.getMemberId())
        .loginId(customUser.getUsername())
        .role(customUser.getRole())
        .build();

      return ResponseEntity.ok(responseDto);

    } catch (AuthenticationException e) {
      Map<String, Object> data = new HashMap<>();
      data.put("status", "error");
      data.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
      return ResponseEntity.status(401).body(data);
    }
  }
}
