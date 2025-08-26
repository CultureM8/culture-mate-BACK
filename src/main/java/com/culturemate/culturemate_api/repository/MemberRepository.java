package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

  private final EntityManager em;

  public void save(Member member) {
    em.persist(member);
  }

  public Member findById(String id) {
    return em.find(Member.class, id);
  }

  public Member findByLoginId(String loginId) {
    return em.find(Member.class, loginId);
  }

  public List<Member> findAll() {
    return em.createQuery("select m from Member m", Member.class).getResultList();
  }

}
