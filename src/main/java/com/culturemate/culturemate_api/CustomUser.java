package com.culturemate.culturemate_api;

import com.culturemate.culturemate_api.domain.member.MemberStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUser extends User {
  public MemberStatus status;

  public CustomUser(String username,
                    String password,
                    Collection<? extends GrantedAuthority> authorities
  ) {
    super(username, password, authorities);
  }
}
