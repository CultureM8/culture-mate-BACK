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

  public List<Together> readActiveTogether() {
    return togetherRepository.findByIsRecruiting(true);
  }

  public List<Together> readByRegion(List<Region> regions) {
    return togetherRepository.findByRegion(regions);
  }

  // 통합 검색 기능
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

  // 검증 로직
  public Together readIfAvailable(Long togetherId) {
    Together together = read(togetherId);
    if (together == null) {
      throw new IllegalArgumentException("해당 모집글이 존재하지 않습니다.");
    }
    if (together.getMeetingDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("이미 기한이 지난 모집입니다.");
    }
    Integer currentParticipants = getParticipants(togetherId).size();
    if (together.getMaxParticipants() <= currentParticipants) {
      throw new IllegalArgumentException("인원을 모두 모집했습니다.");
    }
    return together;
  }

  // ===== 참여자 관리 메서드 =====
  // 참여 여부 확인
  public boolean isParticipating(Long togetherId, Long memberId) {
    return participantsRepository.existsByTogetherIdAndParticipantId(togetherId, memberId);
  }

  // 참여자 목록 조회
  public List<Member> getParticipants(Long togetherId) {
    List<Participants> participantsList = participantsRepository.findByTogetherId(togetherId);
    return participantsList.stream()
      .map(Participants::getParticipant) //Participants 객체에서 Member객체를 호출
      .collect(Collectors.toList());
  }

  // 동행 참여 신청
  @Transactional
  public void joinTogether(Long togetherId, Long memberId) {
    Together together = readIfAvailable(togetherId);

    if (!together.isRecruiting()) {
      throw new IllegalArgumentException("마감된 모집입니다.");
    }
    if (isParticipating(togetherId, memberId)) {
      throw new IllegalStateException("이미 참여 중인 동행입니다.");
    }

    Member member = memberService.getById(memberId);
    Participants participation = Participants.builder()
        .together(together)
        .participant(member)
        .build();
    participantsRepository.save(participation);

    if(together.getMaxParticipants() <= getParticipants(togetherId).size()) {
      together.setIsRecruiting(false);
    }
  }

  // 동행 참여 취소
  @Transactional
  public void leaveTogether(Long togetherId, Long memberId) {
    Participants participation = participantsRepository
        .findByTogetherIdAndParticipantId(togetherId, memberId);
    
    if (participation == null) {
      throw new IllegalArgumentException("참여하지 않은 동행입니다.");
    }
    // meetingDate가 지났다면 동행한 것으로 간주, 삭제 불가.
    Together together = read(togetherId);
    if(together.getMeetingDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("종료된 모집입니다.");
    }
    participantsRepository.delete(participation);

    if(together.getMaxParticipants() > getParticipants(togetherId).size()) {
      together.setIsRecruiting(true);
    }
  }

  // ===== 상태 관리 메서드 =====

  @Transactional
  public void closeTogether(Long togetherId) {
    Together together = readIfAvailable(togetherId);
    together.setIsRecruiting(false);
  }

  @Transactional
  public void reopenTogether(Long togetherId) {
    Together together = readIfAvailable(togetherId);
    together.setIsRecruiting(true);
  }

}
