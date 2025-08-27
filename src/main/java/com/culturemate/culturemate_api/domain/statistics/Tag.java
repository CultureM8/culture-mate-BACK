package com.culturemate.culturemate_api.domain.statistics;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Tag {
  @Id
  private String tag;

  private int count;
}
