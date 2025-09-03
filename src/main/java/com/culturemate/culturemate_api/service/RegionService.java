package com.culturemate.culturemate_api.service;

import com.culturemate.culturemate_api.domain.Region;
import com.culturemate.culturemate_api.dto.RegionDto;
import com.culturemate.culturemate_api.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

  private final RegionRepository regionRepository;

  public Region findById(Long regionId) {
    return regionRepository.findById(regionId)
      .orElseThrow(() -> new IllegalArgumentException("지역이 존재하지 않습니다."));
  }

  public List<Region> findAll() {
    return regionRepository.findAll();
  }

  public List<Region> findByCondition(RegionDto regionDto) {
    return regionRepository.findRegionsByCondition(regionDto.getLevel1(), regionDto.getLevel2(), regionDto.getLevel3());
  }

  public Region findExact(RegionDto regionDto) {
    return regionRepository.findExactRegion(regionDto.getLevel1(), regionDto.getLevel2(), regionDto.getLevel3());
  }

  @Transactional
  public Region create(String level1, String level2, String level3) {
    // 이미 존재하면 기존 것을 반환 (초기화에서 중복 생성 방지)
    Region existingRegion = regionRepository.findExactRegion(level1, level2, level3);
    if (existingRegion != null) {
      System.out.println("이미 존재하는 지역: " + level1 + "-" + level2 + "-" + level3);
      return existingRegion;
    }
    
    Region newRegion = Region.builder()
      .level1(level1)
      .level2(level2)
      .level3(level3)
      .build();
    return regionRepository.save(newRegion);
  }

  @Transactional
  public void update(Region newRegion) {
    if(!regionRepository.existsById(newRegion.getId())) {
      throw new IllegalArgumentException("지역이 존재하지 않습니다.");
    }
    regionRepository.save(newRegion);
  }

  @Transactional
  public void delete(Long regionId) {
    regionRepository.deleteById(regionId);
  }

}