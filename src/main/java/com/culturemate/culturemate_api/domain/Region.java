package com.culturemate.culturemate_api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Region {

  @Id @GeneratedValue
  private long id;

  private String level1 = "전체"; // 시/도
  private String level2 = "전체"; // 시/군/구
  private String level3 = "전체"; // 읍/면/동

}
