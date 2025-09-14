package com.culturemate.culturemate_api.config;

import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.service.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final LoginService loginService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = getTokenFromRequest(request);

    if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        if (jwtUtil.validateToken(token)) {
          String loginId = jwtUtil.getLoginIdFromToken(token);
          UserDetails userDetails = loginService.loadUserByUsername(loginId);

          // MemberStatus 검증 추가
          if (userDetails instanceof AuthenticatedUser) {
            AuthenticatedUser authenticatedUser = (AuthenticatedUser) userDetails;
            
            if (!isAllowedStatus(authenticatedUser.getStatus())) {
              log.warn("비활성 상태 사용자의 접근 시도: {} (상태: {})", 
                      loginId, authenticatedUser.getStatus());
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              response.getWriter().write("{\"error\": \"계정이 비활성 상태입니다.\"}");
              return;
            }
          }

          UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);

          log.debug("JWT 인증 성공: {}", loginId);
        }
      } catch (Exception e) {
        log.error("JWT 토큰 처리 중 오류 발생", e);
        // 토큰 검증 실패 시 로그만 남기고 계속 진행 (인증되지 않은 상태로)
      }
    }

    filterChain.doFilter(request, response);
  }

  /**
   * HTTP 요청에서 JWT 토큰 추출 (Authorization 헤더 또는 Cookie)
   */
  private String getTokenFromRequest(HttpServletRequest request) {
    // 1. Authorization 헤더에서 토큰 추출 (클라이언트 사이드용)
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    
    // 2. Cookie에서 토큰 추출 (서버 사이드용)
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("accessToken".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    
    return null;
  }

  /**
   * MemberStatus 허용 여부 검증
   */
  private boolean isAllowedStatus(MemberStatus status) {
    return switch (status) {
      case ACTIVE -> true;          // 정상 - 전체 기능 허용
      case DORMANT -> true;         // 휴면 - 기본 기능만 허용 (추후 세분화 가능)
      case SUSPENDED -> false;      // 일시정지 - 접근 차단
      case BANNED -> false;         // 영구정지 - 접근 차단
      case DELETED -> false;        // 탈퇴 - 접근 차단
    };
  }
}