package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.member.MemberStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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

  public Optional<Member> findById(Long id) {
    return Optional.ofNullable(em.find(Member.class, id));
  }

  public Optional<Member> findByLoginId(String loginId) {
    try {
      Member result = em.createQuery(
          "select m from Member m where m.loginId = :loginId", Member.class)
          .setParameter("loginId", loginId)
          .getSingleResult();
      return Optional.of(result);
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  public boolean existsByLoginId(String loginId) {
    return findByLoginId(loginId).isPresent();
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

  public void delete(Member member) {
    em.remove(member);
  }

  public void deleteById(Long id) {
    Optional<Member> member = findById(id);
    if (member.isPresent()) {
      em.remove(member);
    }
  }

}
