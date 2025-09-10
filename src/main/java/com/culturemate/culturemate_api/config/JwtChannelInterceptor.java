package com.culturemate.culturemate_api.config;

import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      // WebSocket 연결 시 JWT 토큰 검증
      String token = getTokenFromHeaders(accessor);
      
      if (token == null) {
        log.warn("WebSocket 연결 시 JWT 토큰이 없습니다");
        throw new IllegalArgumentException("WebSocket 연결에는 JWT 토큰이 필요합니다");
      }
      
      try {
        if (!jwtUtil.validateToken(token)) {
          log.warn("WebSocket JWT 토큰 검증 실패");
          throw new IllegalArgumentException("유효하지 않은 JWT 토큰입니다");
        }
        
        String loginId = jwtUtil.getLoginIdFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);
        
        // MemberStatus 검증 추가
        if (userDetails instanceof AuthenticatedUser) {
          AuthenticatedUser authenticatedUser = (AuthenticatedUser) userDetails;
          
          if (!isAllowedStatus(authenticatedUser.getStatus())) {
            log.warn("비활성 상태 사용자의 WebSocket 연결 시도: {} (상태: {})", 
                    loginId, authenticatedUser.getStatus());
            throw new IllegalArgumentException("계정이 비활성 상태입니다");
          }
        }
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        
        accessor.setUser(authentication);
        log.debug("WebSocket JWT 인증 성공: {}", loginId);
        
      } catch (IllegalArgumentException e) {
        // 인증 실패 시 예외를 다시 throw하여 연결 거부
        throw e;
      } catch (Exception e) {
        log.error("WebSocket JWT 처리 중 오류 발생", e);
        throw new IllegalArgumentException("JWT 토큰 처리 중 오류가 발생했습니다");
      }
    }
    
    return message;
  }

  /**
   * STOMP 헤더에서 JWT 토큰 추출
   */
  private String getTokenFromHeaders(StompHeaderAccessor accessor) {
    // Authorization 헤더에서 토큰 추출
    String authHeader = accessor.getFirstNativeHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    
    // token 헤더에서도 시도 (클라이언트 호환성)
    String token = accessor.getFirstNativeHeader("token");
    if (token != null && !token.isEmpty()) {
      return token;
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