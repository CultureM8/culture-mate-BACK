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
  private final Long memberId;
  private final Role role;
  private final MemberStatus status;

  public AuthenticatedUser(String username,
                    String password,
                    Collection<? extends GrantedAuthority> authorities,
                    Long memberId,
                    Role role,
                    MemberStatus status
  ) {
    super(username, password, authorities);
    this.memberId = memberId;
    this.role = role;
    this.status = status;
  }

  /**
   * Member 엔티티에서 AuthenticatedUser 생성
   */
  public static AuthenticatedUser from(Member member, Collection<? extends GrantedAuthority> authorities) {
    return new AuthenticatedUser(
      member.getLoginId(),
      member.getPassword(),
      authorities,
      member.getId(),
      member.getRole(),
      member.getStatus()
    );
  }
}
