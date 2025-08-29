package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.dto.TogetherSearchFilter;
import com.culturemate.culturemate_api.repository.TogetherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class TogetherService {

  private final TogetherRepository togetherRepository;

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

//  public List<Together> readByMember(Member member) {
//
//  }

  public List<Together> readByEvent(Event event) {
    return togetherRepository.findByEvent(event);
  }

  public List<Together> readByHost(Member host) {
    return togetherRepository.findByHost(host);
  }

  // 호스트이든 동행인이든 상관없이 참여하는 동행을 불러옴
  public List<Together> readByMember(Member member) {
    return togetherRepository.findByParticipant(member);
  }

  public List<Together> readByTitle(String title) {
    return togetherRepository.findByTitleContaining(title);
  }

  public List<Together> readByFilter(TogetherSearchFilter filter) {

  }

  @Transactional
  public void update(Together newTogether) {
    Together together = read(newTogether.getId());
    if(together != null){
      throw new IllegalArgumentException("해당 모집글이 존재하지 않습니다.");
    }
    togetherRepository.save(newTogether);
  }

  @Transactional
  public void delete(Long togetherId) {
    togetherRepository.deleteById(togetherId);
  }

}
