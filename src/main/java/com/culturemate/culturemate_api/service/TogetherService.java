package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Participants;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.dto.TogetherSearchDto;
import com.culturemate.culturemate_api.repository.ParticipantsRepository;
import com.culturemate.culturemate_api.repository.TogetherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class TogetherService {

  private final TogetherRepository togetherRepository;
  private final ParticipantsRepository participantsRepository;
  private final MemberService memberService;
  private final RegionService regionService;

  @Transactional
  public void create(Together together) {
    togetherRepository.save(together);
  }

  public Together read(Long togetherId) {
    return togetherRepository.findById(togetherId).orElse(null);
  }

  public List<Together> readAll() {
    return togetherRepository.findAll();
  }

  public List<Together> readByEvent(Event event) {
    return togetherRepository.findByEvent(event);
  }

  // 해당 멤버가 호스트인 모집글
  public List<Together> readByHost(Member host) {
    return togetherRepository.findByHost(host);
  }
  // 호스트이든 동행인이든 상관없이 참여하는 동행을 불러옴
  public List<Together> readByMember(Member member) {
    return togetherRepository.findByParticipant(member);
  }

  public List<Together> search(TogetherSearchDto searchDto) {
    List<Region> regions = null;
    if (searchDto.hasRegion()) {
      regions = regionService.readByCondition(
        searchDto.getLevel1(),
        searchDto.getLevel2(),
        searchDto.getLevel3()
      );
    }

    EventType eventType = null;
    if (searchDto.hasEventType()) {
      eventType = EventType.valueOf(searchDto.getEventType().toUpperCase());
    }

    return togetherRepository.findBySearch(
      searchDto.hasKeyword() ? searchDto.getKeyword() : null,
      regions,
      searchDto.getStartDate(),
      searchDto.getEndDate(),
      eventType,
      searchDto.getEventId()
    );
  }

  @Transactional
  public void update(Together newTogether) {
    Together together = read(newTogether.getId());
    if(together == null){
      throw new IllegalArgumentException("해당 모집글이 존재하지 않습니다.");
    }
    togetherRepository.save(newTogether);
  }

  @Transactional
  public void delete(Long togetherId) {
    togetherRepository.deleteById(togetherId);
  }

  // ===== 참여자 관리 메서드 =====

  /**
   * 동행 참여 신청
   */
  @Transactional
  public void joinTogether(Long togetherId, Long memberId) {
    Together together = read(togetherId);
    if (together == null) {
      throw new IllegalArgumentException("해당 모집글이 존재하지 않습니다.");
    }

    // 이미 참여 중인지 확인
    if (isParticipating(togetherId, memberId)) {
      throw new IllegalStateException("이미 참여 중인 동행입니다.");
    }

    // Member 조회
    Member member = memberService.getById(memberId);

    // 참여자 추가
    Participants participation = Participants.builder()
        .together(together)
        .participant(member)
        .build();
    participantsRepository.save(participation);
  }

  /**
   * 동행 참여 취소
   */
  @Transactional
  public void leaveTogether(Long togetherId, Long memberId) {
    Participants participation = participantsRepository
        .findByTogetherIdAndParticipantId(togetherId, memberId);
    
    if (participation == null) {
      throw new IllegalArgumentException("참여하지 않은 동행입니다.");
    }

    participantsRepository.delete(participation);
  }

  /**
   * 참여자 목록 조회
   */
  public List<Member> getParticipants(Long togetherId) {
    List<Participants> participantsList = participantsRepository.findByTogetherId(togetherId);
    return participantsList.stream()
        .map(Participants::getParticipant) //Participants 객체에서 Member객체를 호출
        .collect(Collectors.toList());
  }

  /**
   * 참여 가능 여부 확인
   */
  public boolean canJoin(Long togetherId, Long memberId) {
    Together together = read(togetherId);
    if (together == null) return false;
    
    // 이미 참여 중인지 확인
    if (isParticipating(togetherId, memberId)) return false;
    
    // TODO: 추가 조건들 (정원 초과, 마감일 등)
    return true;
  }

  /**
   * 참여 여부 확인
   */
  public boolean isParticipating(Long togetherId, Long memberId) {
    return participantsRepository.existsByTogetherIdAndParticipantId(togetherId, memberId);
  }

  // ===== 상태 관리 메서드 =====

  /**
   * 모집 마감
   */
  @Transactional
  public void closeTogether(Long togetherId) {
    Together together = read(togetherId);
    if (together == null) {
      throw new IllegalArgumentException("해당 모집글이 존재하지 않습니다.");
    }
    // TODO: Together 엔티티에 상태 필드 추가 필요
    // together.setStatus(TogetherStatus.CLOSED);
    togetherRepository.save(together);
  }

  /**
   * 모집 재개
   */
  @Transactional
  public void reopenTogether(Long togetherId) {
    Together together = read(togetherId);
    if (together == null) {
      throw new IllegalArgumentException("해당 모집글이 존재하지 않습니다.");
    }
    // TODO: Together 엔티티에 상태 필드 추가 필요
    // together.setStatus(TogetherStatus.ACTIVE);
    togetherRepository.save(together);
  }

  /**
   * 활성 모집글만 조회 (상태 필드 추가 후 구현)
   */
  public List<Together> readActiveTogether() {
    // TODO: Together 엔티티에 상태 필드 추가 후 구현
    return readAll(); // 임시로 전체 조회
  }

  // ===== 비즈니스 로직 메서드 =====

  /**
   * 지역별 모집글 조회
   */
  public List<Together> readByRegion(List<Region> regions) {
    return togetherRepository.findByRegion(regions);
  }

  /**
   * 마감 임박 모집글 (며칠 내)
   */
  public List<Together> readByDeadlineSoon(int days) {
    LocalDate deadline = LocalDate.now().plusDays(days);
    // TODO: Repository에 마감일 기준 쿼리 메서드 추가 필요
    return togetherRepository.findAll().stream()
        .filter(t -> t.getMeetingDate() != null && 
                    t.getMeetingDate().isBefore(deadline.plusDays(1)) && 
                    t.getMeetingDate().isAfter(LocalDate.now().minusDays(1)))
        .collect(Collectors.toList());
  }

}
