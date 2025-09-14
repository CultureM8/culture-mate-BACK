package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.dto.AuthenticatedUser;
import com.culturemate.culturemate_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LoginService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//    DB에서 username을 가진 유저를 찾아와서
//    Member 엔티티 기반으로 AuthenticatedUser 생성

    System.out.println("=== [" + java.time.LocalDateTime.now() + "] LoginService.loadUserByUsername called ===");
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

}


