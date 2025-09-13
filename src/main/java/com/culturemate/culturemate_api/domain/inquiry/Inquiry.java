package com.culturemate.culturemate_api.domain.inquiry;

import com.culturemate.culturemate_api.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

    @Setter
    @Column(nullable = false, length = 100)
    private String title;

    @Setter
    @Column(nullable = false, length = 3000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryCategory category;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private InquiryAnswer answer;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.status = InquiryStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Helper method to link answer
    public void setAnswer(InquiryAnswer answer) {
        this.answer = answer;
        if (answer != null) {
            answer.setInquiry(this);
            this.status = InquiryStatus.ANSWERED;
        }
    }
}
