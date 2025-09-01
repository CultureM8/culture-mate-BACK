package com.culturemate.culturemate_api.repository;

import com.culturemate.culturemate_api.domain.member.MemberDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDetailRepository extends JpaRepository<MemberDetail, Long> {
}
