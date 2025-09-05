package com.culturemate.culturemate_api.dto;

import com.culturemate.culturemate_api.domain.member.MemberStatus;
import com.culturemate.culturemate_api.domain.member.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUser extends User {
  private final Long memberId;
  private final Role role;
  private final MemberStatus status;

  public CustomUser(String username,
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
}
