package com.pesu.cfs.model;

import com.pesu.cfs.model.enums.FeedbackStatus;
import com.pesu.cfs.model.enums.FeedbackType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType feedbackType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Min(1) @Max(5)
    private Integer rating;

    /**
     * State Diagram: Draft -> Submitted -> UnderReview -> Approved / Rejected
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FeedbackStatus status = FeedbackStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    // --- State transitions matching state diagram ---

    public void submit() {
        if (this.status == FeedbackStatus.DRAFT) {
            this.status = FeedbackStatus.SUBMITTED;
        }
    }

    public void startReview() {
        if (this.status == FeedbackStatus.SUBMITTED) {
            this.status = FeedbackStatus.UNDER_REVIEW;
        }
    }

    public void approve() {
        if (this.status == FeedbackStatus.UNDER_REVIEW) {
            this.status = FeedbackStatus.APPROVED;
            this.reviewedAt = LocalDateTime.now();
        }
    }

    public void reject() {
        if (this.status == FeedbackStatus.UNDER_REVIEW) {
            this.status = FeedbackStatus.REJECTED;
            this.reviewedAt = LocalDateTime.now();
        }
    }
}
