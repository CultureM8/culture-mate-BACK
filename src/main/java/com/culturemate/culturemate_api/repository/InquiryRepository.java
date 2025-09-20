package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.inquiry.Inquiry;
import com.culturemate.culturemate_api.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    
    // fetch join을 사용해 Member 정보도 함께 조회하도록 변경
    @Query("SELECT i FROM Inquiry i " +
      "JOIN FETCH i.author " +
      "LEFT JOIN FETCH i.images " +
      "WHERE i.author = :author " +
      "ORDER BY i.createdAt DESC")
    List<Inquiry> findByAuthorWithImages(Member author);

    // 관리자용 전체 조회 기능에도 fetch join 추가
    @Query("SELECT i FROM Inquiry i " +
      "JOIN FETCH i.author " +
      "LEFT JOIN FETCH i.images " +
      "ORDER BY i.createdAt DESC")
    List<Inquiry> findAllWithAuthorAndImages();
}
