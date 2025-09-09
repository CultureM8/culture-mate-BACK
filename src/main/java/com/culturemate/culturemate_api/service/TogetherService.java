package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.domain.chatting.ChatRoom;
import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.event.EventType;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Participants;
import com.culturemate.culturemate_api.domain.together.ParticipationStatus;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.dto.*;

import java.time.ZoneId;
import com.culturemate.culturemate_api.exceptions.together.*;
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
@Transactional(readOnly = true)
public class TogetherService {

  private final TogetherRepository togetherRepository;
  private final ParticipantsRepository participantsRepository;
  private final MemberService memberService;
  private final RegionService regionService;
  private final EventService eventService;
  private final ImageService imageService;
  private final ChatRoomService chatRoomService;


  @Transactional
  public Together create(TogetherDto.Request requestDto) {
    Event event = eventService.findById(requestDto.getEventId());
    Member host = memberService.findById(requestDto.getHostId());
    Together together = Together.builder()
      .event(event)
      .host(host)
      .title(requestDto.getTitle())
      .region(regionService.findExact(requestDto.getRegionDto()))
      .address(requestDto.getAddress())
      .addressDetail(requestDto.getAddressDetail())
      .content(requestDto.getContent())
      .build();

    // Together를 DB에 저장
    Together savedTogether = togetherRepository.save(together);

    // 호스트를 참여자로 자동 추가
    Participants hostParticipation = Participants.builder()
      .together(savedTogether)
      .participant(host)
      .build();
    participantsRepository.save(hostParticipation);

    // 채팅방 생성 및 호스트 추가
    ChatRoom chatRoom = chatRoomService.createChatRoom(savedTogether.getTitle(), savedTogether);
    chatRoomService.addMemberToRoom(chatRoom.getId(), host.getId());

    return savedTogether;
  }

  public List<Together> findAll() {
    return togetherRepository.findAll();
  }

  public Together findById(Long togetherId) {
    return togetherRepository.findById(togetherId)
        .orElseThrow(() -> new TogetherNotFoundException(togetherId));
  }

  // 해당 멤버가 호스트인 모집글
  public List<Together> findByHost(Member host) {
    return togetherRepository.findByHost(host);
  }
  // 호스트이든 동행인이든 상관없이 참여하는 동행을 불러옴 (모든 상태)
  public List<Together> findByMemberAll(Member member) {
    return togetherRepository.findByParticipantAll(member);
  }
  
  // 특정 상태의 신청 동행만 조회
  public List<Together> findByMemberAndStatus(Member member, String status) {
    return togetherRepository.findByParticipantAndStatus(member, status);
  }

  // 통합 검색 기능
  public List<Together> search(TogetherSearchDto searchDto) {
    List<Region> regions = null;
    if (searchDto.hasRegion()) {
      regions = regionService.findByHierarchy(searchDto.getRegionDto());
    }

    EventType eventType = null;
    if (searchDto.hasEventType()) {
      eventType = EventType.valueOf(searchDto.getEventType().toUpperCase());
    }

    // 지역 조건에 따라 다른 Repository 메서드 사용
    List<Together> results;
    if (regions == null || regions.isEmpty()) {
      // 지역 조건 없는 검색
      results = togetherRepository.findBySearchWithoutRegion(
        searchDto.hasKeyword() ? searchDto.getKeyword() : null,
        searchDto.getStartDate(),
        searchDto.getEndDate(),
        eventType,
        searchDto.getEventId()
      );
    } else {
      // 지역 조건 있는 검색
      results = togetherRepository.findBySearch(
        searchDto.hasKeyword() ? searchDto.getKeyword() : null,
        regions,
        searchDto.getStartDate(),
        searchDto.getEndDate(),
        eventType,
        searchDto.getEventId()
      );
    }

    // isActive 필터링 (Service에서 처리)
    if (searchDto.hasActiveFilter()) {
      Boolean activeFilter = searchDto.getIsActive();
      results = results.stream()
        .filter(together -> isActive(together) == activeFilter)
        .collect(Collectors.toList());
    }

    return results;
  }

  // 수정
  @Transactional
  public Together update(Long id, TogetherDto.Request requestDto) {
    Together together = findById(id);
    Event event = eventService.findById(requestDto.getEventId());
    Region region = regionService.findExact(requestDto.getRegionDto());

    // 날짜 검증 - 과거 날짜 방지
    if (together.getMeetingDate().isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("모임 날짜는 오늘 이후여야 합니다");
    }

    // 참여자 수 검증 - 현재 참여자보다 적게 설정 방지
    Integer currentCount = together.getParticipantCount();
    if (together.getMaxParticipants() < currentCount) {
      throw new IllegalArgumentException("최대 참여자 수는 현재 참여자 수보다 적을 수 없습니다");
    }

    together.setEvent(event);
    together.setTitle(requestDto.getTitle());
    together.setRegion(region);
    together.setAddress(requestDto.getAddress());
    together.setAddressDetail(requestDto.getAddressDetail());
    together.setMeetingDate(requestDto.getMeetingDate());
    together.setMaxParticipants(requestDto.getMaxParticipants());
    together.setContent(requestDto.getContent());
    
    return together;
  }

  @Transactional
  public void delete(Long togetherId) {
    Together together = findById(togetherId);

    // 썸네일/메인 이미지 파일들 삭제
    imageService.deletePhysicalFiles(together.getThumbnailImagePath(),
                                    together.getMainImagePath());

    // Together 엔티티 삭제
    togetherRepository.delete(together);
  }


  // ===== 참여자 관리 메서드 =====
  // 참여 여부 확인
  public boolean isParticipating(Long togetherId, Long memberId) {
    return participantsRepository.existsByTogetherIdAndParticipantId(togetherId, memberId);
  }

  // 모든 참여자 목록 조회 (상태 무관)
  public List<Member> getAllParticipants(Long togetherId) {
    List<Participants> participantsList = participantsRepository.findAllByTogetherId(togetherId);
    return participantsList.stream()
      .map(Participants::getParticipant)
      .collect(Collectors.toList());
  }
  
  // 특정 상태 참여자 목록 조회
  public List<Member> getParticipantsByStatus(Long togetherId, String status) {
    List<Participants> participantsList = participantsRepository.findByTogetherIdAndStatus(togetherId, status);
    return participantsList.stream()
      .map(Participants::getParticipant)
      .collect(Collectors.toList());
  }

  // 동행 신청 (승인 대기 상태로 생성)
  @Transactional
  public void applyTogether(Long togetherId, Long memberId) {
    Together together = findById(togetherId);

    if (!isActive(together)) {
      throw new TogetherClosedException(togetherId);
    }
    if (isParticipating(togetherId, memberId)) {
      throw new TogetherAlreadyJoinedException(togetherId, memberId);
    }

    Member member = memberService.findById(memberId);
    Participants participation = Participants.builder()
        .together(together)
        .participant(member)
        .status(ParticipationStatus.PENDING) // 명시적으로 대기 상태 설정
        .build();
    participantsRepository.save(participation);
    
    // 신청 단계에서는 채팅방에 추가하지 않음 (승인 후에만 추가)
    // 필요시 알림 로직 추가 가능
  }

  // 동행 참여 거절
  @Transactional
  public void rejectParticipation(Long togetherId, Long participantId, Long requesterId) {
    Together together = findById(togetherId);
    if (!together.getHost().getId().equals(requesterId)) {
      throw new SecurityException("해당 모집글의 호스트가 아닙니다.");
    }

    Participants participation = participantsRepository.findByTogetherIdAndParticipantId(togetherId, participantId);
    if (participation == null) {
      throw new IllegalArgumentException("참여 신청을 찾을 수 없습니다.");
    }

    participation.setStatus(ParticipationStatus.REJECTED);
  }

  // 동행 참여 승인
  @Transactional
  public void approveParticipation(Long togetherId, Long participantId, Long requesterId) {
    Together together = findById(togetherId);
    if (!together.getHost().getId().equals(requesterId)) {
      throw new SecurityException("해당 모집글의 호스트가 아닙니다.");
    }

    // 승인 시점에도 모집 가능 상태 확인 (정원, 날짜, 호스트 설정)
    if (!isActive(together)) {
      throw new TogetherClosedException(togetherId);
    }

    Participants participation = participantsRepository.findByTogetherIdAndParticipantId(togetherId, participantId);
    if (participation == null) {
      throw new IllegalArgumentException("참여 신청을 찾을 수 없습니다.");
    }

    participation.setStatus(ParticipationStatus.APPROVED);

    // 승인 시 참여자 수 증가
    togetherRepository.updateParticipantCount(togetherId, 1);

    // 채팅방에 멤버 추가
    chatRoomService.addMemberToRoomByTogether(together, participantId);
  }

  // 동행 참여 취소
  @Transactional
  public void leaveTogether(Long togetherId, Long memberId) {
    Participants participation = participantsRepository
        .findByTogetherIdAndParticipantId(togetherId, memberId);
    
    if (participation == null) {
      throw new TogetherNotJoinedException(togetherId, memberId);
    }
    
    Together together = findById(togetherId);
    if(together.getMeetingDate().isBefore(LocalDate.now())) {
      throw new TogetherExpiredException(togetherId, together.getMeetingDate());
    }
    participantsRepository.delete(participation);
    togetherRepository.updateParticipantCount(togetherId, -1); // 참여자 수만 감소

    // 채팅방 나가기
    chatRoomService.removeMemberFromRoomByTogether(together, memberId);
  }

  // 호스트의 멤버 강제 퇴출
  @Transactional
  public void removeMember(Long togetherId, Long participantId, Long hostId) {
    Together together = findById(togetherId);
    
    // 호스트 권한 확인
    if (!together.getHost().getId().equals(hostId)) {
      throw new SecurityException("해당 모집글의 호스트가 아닙니다.");
    }
    
    // 참여자 존재 확인
    Participants participation = participantsRepository.findByTogetherIdAndParticipantId(togetherId, participantId);
    if (participation == null) {
      throw new IllegalArgumentException("해당 참여자를 찾을 수 없습니다.");
    }
    
    // 호스트는 자기 자신을 내보낼 수 없음
    if (hostId.equals(participantId)) {
      throw new IllegalArgumentException("호스트는 자신을 내보낼 수 없습니다.");
    }
    
    // 승인된 상태였다면 참여자 수 감소
    boolean wasApproved = participation.getStatus() == ParticipationStatus.APPROVED;
    
    // 참여 상태를 REJECTED로 변경
    participation.setStatus(ParticipationStatus.REJECTED);
    
    if (wasApproved) {
      togetherRepository.updateParticipantCount(togetherId, -1);
    }
    
    // 채팅방에서 제거
    chatRoomService.removeMemberFromRoomByTogether(together, participantId);
  }

  // ===== 상태 관리 메서드 =====

  // 실제 모집 가능 여부 확인 (종합 판단)
  public boolean isActive(Together together) {
    // 1. 호스트가 모집을 비활성화한 경우
    if (!together.isHostRecruitingEnabled()) {
      return false;
    }
    
    // 2. 날짜가 지난 경우
    if (together.getMeetingDate().isBefore(LocalDate.now())) {
      return false;
    }
    
    // 3. 정원이 다 찬 경우
    if (together.getParticipantCount() >= together.getMaxParticipants()) {
      return false;
    }
    
    return true;
  }
  
  // 오버로드 메서드
  public boolean isActive(Long togetherId) {
    Together together = findById(togetherId);
    return isActive(together);
  }

  @Transactional
  public void closeTogether(Long togetherId, Long requesterId) {
    Together together = findById(togetherId);
    if (!together.getHost().getId().equals(requesterId)) {
      throw new SecurityException("호스트만 모집을 종료할 수 있습니다.");
    }
    together.setHostRecruitingEnabled(false);
  }

  @Transactional
  public void reopenTogether(Long togetherId, Long requesterId) {
    Together together = findById(togetherId);
    if (!together.getHost().getId().equals(requesterId)) {
      throw new SecurityException("호스트만 모집을 재개할 수 있습니다.");
    }
    together.setHostRecruitingEnabled(true);
  }

  // DTO 생성 헬퍼 메서드
  public TogetherDto.Response toResponseDto(Together together) {
    return TogetherDto.Response.builder()
      .id(together.getId())
      .event(EventDto.ResponseCard.from(together.getEvent()))
      .host(MemberDto.ProfileResponse.from(together.getHost()))
      .title(together.getTitle())
      .region(RegionDto.Response.from(together.getRegion()))
      .address(together.getAddress())
      .addressDetail(together.getAddressDetail())
      .meetingDate(together.getMeetingDate())
      .maxParticipants(together.getMaxParticipants())
      .currentParticipants(together.getParticipantCount())
      .content(together.getContent())
      .active(isActive(together)) // 실제 isActive 계산
      .createdAt(together.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
      .updatedAt(together.getUpdatedAt() != null ? 
                 together.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime() : null)
      .build();
  }

}
