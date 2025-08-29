package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.together.Participants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Participants 엔티티 Repository
 * Together와 Member 간의 참여 관계를 관리
 */
@Repository
public interface ParticipantsRepository extends JpaRepository<Participants, Long> {

  /**
   * 특정 Together의 모든 참여자 조회
   */
  List<Participants> findByTogetherId(Long togetherId);

  /**
   * 특정 Member가 참여한 모든 Together 조회
   */
  List<Participants> findByParticipantId(Long participantId);

  /**
   * 특정 Together에 특정 Member가 참여하는지 조회
   */
  @Query("SELECT p FROM Participants p WHERE p.together.id = :togetherId AND p.participant.id = :participantId")
  Participants findByTogetherIdAndParticipantId(@Param("togetherId") Long togetherId, 
                                                @Param("participantId") Long participantId);

  /**
   * 특정 Together에 특정 Member가 참여하는지 존재 여부만 확인
   * Spring Data JPA 메서드명 규칙 사용 (더 간단)
   */
  boolean existsByTogetherIdAndParticipantId(Long togetherId, Long participantId);

  /**
   * 특정 Together의 참여자 수 조회
   */
  @Query("SELECT COUNT(p) FROM Participants p WHERE p.together.id = :togetherId")
  long countParticipantsByTogetherId(@Param("togetherId") Long togetherId);

  /**
   * 특정 Member가 참여한 Together 개수 조회
   */
  @Query("SELECT COUNT(p) FROM Participants p WHERE p.participant.id = :participantId")
  long countTogethersByParticipantId(@Param("participantId") Long participantId);

}