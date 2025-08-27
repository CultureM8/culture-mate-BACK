package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

  private final EntityManager em;

  public void save(Member member) {
    em.persist(member);
  }

  public Member findById(Long id) {
    return em.find(Member.class, id);
  }

  public Optional<Member> findOptionalById(Long id) {
    return Optional.ofNullable(em.find(Member.class, id));
  }

  public Optional<Member> findByLoginId(String loginId) {
    List<Member> result = em.createQuery(
        "select m from Member m where m.loginId = :loginId", Member.class)
        .setParameter("loginId", loginId)
        .getResultList();
    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }

  public boolean existsByLoginId(String loginId) {
    Long count = em.createQuery(
        "select count(m) from Member m where m.loginId = :loginId", Long.class)
        .setParameter("loginId", loginId)
        .getSingleResult();
    return count > 0;
  }

  public List<Member> findAll() {
    return em.createQuery("select m from Member m", Member.class).getResultList();
  }

  public List<Member> findByStatus(MemberStatus status) {
    return em.createQuery(
        "select m from Member m where m.status = :status", Member.class)
        .setParameter("status", status)
        .getResultList();
  }

  public List<Member> findActiveMembers() {
    return findByStatus(MemberStatus.ACTIVE);
  }

  public void delete(Member member) {
    em.remove(member);
  }

  public void deleteById(Long id) {
    Member member = findById(id);
    if (member != null) {
      em.remove(member);
    }
  }

}
