package com.culturemate.culturemate_api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

  //=== 필드 ===//
  @Id @GeneratedValue
  @Column(name = "member_id")
  private Long id;

  @Column(unique = true)
  private String loginId;

  private String password;

  @Enumerated(EnumType.STRING)
  private Role role = Role.MEMBER;

  private Instant joinedAt;

  @Enumerated(EnumType.STRING)
  private MemberStatus status = MemberStatus.ACTIVE;

  //=== 메서드 ===//
  public void changeStatus() {
    //TODO : 회원 제재 상태 변경
  }

  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<MemberDetail> memberDetails = new ArrayList<>();
}
