package com.culturemate.culturemate_api.domain.member;

import com.culturemate.culturemate_api.domain.community.Board;
import jakarta.persistence.*;
import lombok.Getter;

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
  public void changeStatus(MemberStatus newstatus) {
    this.status = newstatus;
  }

  @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private MemberDetail memberDetail;
  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Board> boards = new ArrayList<>();
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<InterestEvents> interestEvents = new ArrayList<>();
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<InterestTogethers> interestTogethers = new ArrayList<>();
}
