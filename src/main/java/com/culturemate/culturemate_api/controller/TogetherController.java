package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.event.Event;
import com.culturemate.culturemate_api.domain.member.Member;
import com.culturemate.culturemate_api.domain.together.Together;
import com.culturemate.culturemate_api.dto.TogetherRequestDto;
import com.culturemate.culturemate_api.dto.TogetherSearchDto;
import com.culturemate.culturemate_api.service.MemberService;
import com.culturemate.culturemate_api.service.TogetherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/together")
@RequiredArgsConstructor
public class TogetherController {
  private final TogetherService togetherService;
  private final MemberService memberService;

  @GetMapping
  public ResponseEntity<List<Together>> getAll() {
    return ResponseEntity.ok().body(togetherService.readAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Together> getById(@PathVariable Long id) {
    Together together = togetherService.read(id);
    return ResponseEntity.ok(together);
  }

  // 특정 회원이 호스트인 모집글 조회
  @GetMapping("/hosted-by/{hostId}")
  public ResponseEntity<List<Together>> getByHostId(@PathVariable Long hostId) {
    Member host = memberService.getById(hostId);
    List<Together> togethers = togetherService.readByHost(host);
    return ResponseEntity.ok(togethers);
  }
  // 특정 회원이 참여하는 모집글 조회
  @GetMapping("/with/{memberId}")
  public ResponseEntity<List<Together>> getByMemberId(@PathVariable Long memberId) {
    Member member = memberService.getById(memberId);
    List<Together> togethers = togetherService.readByMember(member);
    return ResponseEntity.ok(togethers);
  }
  // 모집글 통합 검색
  @GetMapping("/search")
  public ResponseEntity<List<Together>> search(@RequestParam TogetherSearchDto searchDto) {
    if (searchDto.isEmpty()) {
      List<Together> togethers = togetherService.readAll();
      return ResponseEntity.ok().body(togethers);
    }

    List<Together> togethers = togetherService.search(searchDto);
    return ResponseEntity.ok().body(togethers);
  }

  // 모집글 생성
  @PostMapping
  public ResponseEntity<Together> create(@RequestBody TogetherRequestDto togetherRequestDto) {
    Together together = new Together();
    // TODO: 구현 예정
    return ResponseEntity.ok().body(together);
  }

  // 모집글 수정
  // 모집글 삭제


}
