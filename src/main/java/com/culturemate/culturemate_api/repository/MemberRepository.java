package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByLoginId(String loginId);
  
  boolean existsByLoginId(String loginId);
  
  List<Member> findByStatus(MemberStatus status);

}
