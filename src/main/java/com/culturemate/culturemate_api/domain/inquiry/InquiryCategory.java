package com.culturemate.culturemate_api.domain.inquiry;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum InquiryCategory {
    QUESTION,     // 문의
    SUGGESTION,   // 건의
    ACCOUNT,      // 계정/회원정보
    SITE_USAGE,   // 사이트이용
    REPORT,       // 신고
    EVENT,        // 이벤트 관련
    ETC;           // 기타

    @JsonCreator
    public static InquiryCategory fromString(String value) {
        if (value == null) {
            return null;
        }
        for (InquiryCategory category : InquiryCategory.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown enum type " + value + ", Allowed values are " + java.util.Arrays.toString(values()));
    }
}