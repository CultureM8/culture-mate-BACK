package com.culturemate.culturemate_api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Getter
public class Image {

  //=== 필드 ===//
  @Id @GeneratedValue
  @Column(name = "image_id")
  private long id;

  @Enumerated(EnumType.STRING)
  private ImageTarget targetType;

  private Long targetId;

  private String path;

  private String contentType;

  private Instant createdAt;

  //=== 메서드 ===//
  @PrePersist
  public void onCreate() {
    this.createdAt = Instant.now();
  }

}
