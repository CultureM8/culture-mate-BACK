package com.culturemate.culturemate_api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
public class Member {

  @Id @GeneratedValue
  @Column(name = "member_id")
  private Long id;

  @Column(unique = true)
  private String login_id;

  private String password;

  @Enumerated(EnumType.STRING)
  private Role role = Role.MEMBER;

  private Instant joined_at;

  @Enumerated(EnumType.STRING)
  private MemberStatus status = MemberStatus.ACTIVE;

  public void changeStatus() {
    //TODO : 회원 제재 상태 변경
  }

}
