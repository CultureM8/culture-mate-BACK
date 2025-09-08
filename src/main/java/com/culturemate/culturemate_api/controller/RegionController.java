package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.dto.RegionDto;
import com.culturemate.culturemate_api.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

  private final RegionService regionService;

  /**
   * 새로운 지역 추가
   */
  @PostMapping
  public ResponseEntity<RegionDto.Response> createRegion(@RequestBody RegionDto.Request request) {
    Region region = regionService.create(
      request.getLevel1(), 
      request.getLevel2(), 
      request.getLevel3()
    );
    return ResponseEntity.ok(RegionDto.Response.from(region));
  }

  /**
   * 지역 검색 (조건에 맞는 지역과 하위 지역들 포함)
   */
  @GetMapping("/search")
  public ResponseEntity<List<RegionDto.Response>> searchRegions(
    @RequestParam(required = false) String level1,
    @RequestParam(required = false) String level2,
    @RequestParam(required = false) String level3
  ) {
    RegionDto.Request searchDto = RegionDto.Request.builder()
      .level1(level1)
      .level2(level2)
      .level3(level3)
      .build();
    
    List<Region> regions = regionService.findByHierarchy(searchDto);
    List<RegionDto.Response> response = regions.stream()
      .map(RegionDto.Response::from)
      .collect(Collectors.toList());
    
    return ResponseEntity.ok(response);
  }

  /**
   * 전체 지역 조회
   */
  @GetMapping("/all")
  public ResponseEntity<List<RegionDto.Response>> getAllRegions() {
    List<Region> regions = regionService.findAll();
    List<RegionDto.Response> response = regions.stream()
      .map(RegionDto.Response::from)
      .collect(Collectors.toList());
    
    return ResponseEntity.ok(response);
  }

  /**
   * 특정 지역 조회
   */
  @GetMapping("/{id}")
  public ResponseEntity<RegionDto.Response> getRegion(@PathVariable Long id) {
    Region region = regionService.findById(id);
    return ResponseEntity.ok(RegionDto.Response.from(region));
  }

  /**
   * 지역 수정
   */
  @PutMapping("/{id}")
  public ResponseEntity<RegionDto.Response> updateRegion(
    @PathVariable Long id,
    @RequestBody RegionDto.Request request
  ) {
    Region existingRegion = regionService.findById(id);
    
    // 기존 지역의 정보를 새로운 정보로 업데이트
    Region updatedRegion = Region.builder()
      .id(existingRegion.getId())
      .regionName(request.getLevel3() != null ? request.getLevel3() : 
                  request.getLevel2() != null ? request.getLevel2() : request.getLevel1())
      .parent(existingRegion.getParent())
      .build();
    
    regionService.update(updatedRegion);
    return ResponseEntity.ok(RegionDto.Response.from(updatedRegion));
  }

  /**
   * 지역 삭제
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
    regionService.delete(id);
    return ResponseEntity.ok().build();
  }
}