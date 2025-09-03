package com.culturemate.culturemate_api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {

  //=== 필드 ===//
  @Id @GeneratedValue
  private long id;

  private String level1; // 시/도
  private String level2; // 시/군/구
  private String level3; // 읍/면/동

}
