package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.domain.member.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AuthenticatedUser extends User {
  private final Member member;

  public AuthenticatedUser(Member member, Collection<? extends GrantedAuthority> authorities) {
    super(member.getLoginId(), member.getPassword(), authorities);
    this.member = member;
  }

  /**
   * Member 엔티티에서 AuthenticatedUser 생성
   */
  public static AuthenticatedUser from(Member member, Collection<? extends GrantedAuthority> authorities) {
    return new AuthenticatedUser(member, authorities);
  }

  // 편의 메서드들
  public Long getMemberId() {
    return member.getId();
  }

  public String getLoginId() {
    return member.getLoginId();
  }

  public String getNickname() {
    return member.getMemberDetail() != null ? member.getMemberDetail().getNickname() : member.getLoginId();
  }

  public Role getRole() {
    return member.getRole();
  }

  public MemberStatus getStatus() {
    return member.getStatus();
  }
}
