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

  public Region read(Long regionId) {
    return regionRepository.findById(regionId).orElse(null);
  }

  public List<Region> readAll() {
    return regionRepository.findAll();
  }

  public List<Region> readByCondition(RegionDto regionDto) {
    return regionRepository.findRegionsByCondition(regionDto.getLevel1(), regionDto.getLevel2(), regionDto.getLevel3());
  }

  @Transactional
  public Region create(Region region) {
    return regionRepository.save(region);
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