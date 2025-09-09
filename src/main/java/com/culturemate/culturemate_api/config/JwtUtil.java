package com.culturemate.culturemate_api.config;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {

  @Value("${jwt.secret:myDefaultSecretKey123456789012345678901234567890}")
  private String secret;

  @Value("${jwt.expiration:86400000}") // 24시간 (milliseconds)
  private Long expiration;

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * JWT 토큰 생성 - AuthenticatedUser 기반
   */
  public String generateToken(AuthenticatedUser user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("memberId", user.getMemberId());
    claims.put("role", user.getRole().name());
    claims.put("status", user.getStatus().name());

    return Jwts.builder()
      .setClaims(claims)
      .setSubject(user.getUsername())
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + expiration))
      .signWith(getSigningKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  /**
   * JWT 토큰 생성 - Member 엔티티 기반
   */
  public String generateToken(Member member) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("memberId", member.getId());
    claims.put("role", member.getRole().name());
    claims.put("status", member.getStatus().name());

    return Jwts.builder()
      .setClaims(claims)
      .setSubject(member.getLoginId())
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + expiration))
      .signWith(getSigningKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  /**
   * 토큰에서 사용자명 추출
   */
  public String getUsernameFromToken(String token) {
    return getClaimsFromToken(token).getSubject();
  }

  /**
   * 토큰에서 memberId 추출
   */
  public Long getMemberIdFromToken(String token) {
    return Long.valueOf(getClaimsFromToken(token).get("memberId").toString());
  }

  /**
   * 토큰 유효성 검증
   */
  public boolean validateToken(String token, String username) {
    try {
      return getUsernameFromToken(token).equals(username) && !isTokenExpired(token);
    } catch (Exception e) {
      log.error("토큰 검증 실패: {}", e.getMessage());
      return false;
    }
  }

  /**
   * 토큰에서 Claims 추출
   */
  private Claims getClaimsFromToken(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  /**
   * 토큰 만료 여부 확인
   */
  private boolean isTokenExpired(String token) {
    return getClaimsFromToken(token).getExpiration().before(new Date());
  }
}