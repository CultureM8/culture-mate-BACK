package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.Role;
import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//    DB에서 username을 가진 유저를 찾아와서
//    Member 엔티티 기반으로 AuthenticatedUser 생성

    System.out.println("=== [" + java.time.LocalDateTime.now() + "] AuthService.loadUserByUsername called ===");
    System.out.println("  - Username: " + username);

    try {
      var result = memberRepository.findByLoginId(username);
      System.out.println("  - DB 조회 결과: " + (result.isEmpty() ? "없음" : "존재"));

      if(result.isEmpty()){
        System.err.println("  - ERROR: 사용자를 찾을 수 없음: " + username);
        throw new UsernameNotFoundException("존재하지 않는 사용자입니다: " + username);
      }

      var member = result.get();
      System.out.println("  - Member 정보:");
      System.out.println("    * ID: " + member.getId());
      System.out.println("    * LoginId: " + member.getLoginId());
      System.out.println("    * Role: " + member.getRole());
      System.out.println("    * Status: " + member.getStatus());
      System.out.println("    * Member is null: " + (member == null));

      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority(member.getRole().name()));

      System.out.println("  - AuthenticatedUser 생성 시도...");
      AuthenticatedUser authenticatedUser = AuthenticatedUser.from(member, authorities);
      System.out.println("  - AuthenticatedUser 생성 성공");

      return authenticatedUser;
    } catch (Exception e) {
      System.err.println("  - ERROR in loadUserByUsername: " + e.getMessage());
      e.printStackTrace();
      throw e;
    }
  }


  // 권한 검증
  public void validateProfileAccess(Long profileMemberId, Long requesterId) {
    Member requester = memberRepository.findById(requesterId)
        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    // ADMIN은 모든 프로필 수정/삭제 가능
    if (requester.getRole() == Role.ADMIN) {
      return;
    }

    // 일반 사용자는 본인 프로필만
    if (!profileMemberId.equals(requesterId)) {
      throw new IllegalArgumentException("본인의 프로필만 수정/삭제할 수 있습니다.");
    }
  }

  public void validateAdminAccess(Long requesterId) {
    Member requester = memberRepository.findById(requesterId)
        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    if (requester.getRole() != Role.ADMIN) {
      throw new IllegalArgumentException("관리자 권한이 필요합니다.");
    }
  }

}


