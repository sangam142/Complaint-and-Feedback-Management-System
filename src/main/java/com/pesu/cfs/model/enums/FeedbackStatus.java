package com.pesu.cfs.model.enums;

/**
 * Matches Feedback State Diagram:
 * Draft -> Submitted -> UnderReview -> Approved / Rejected
 */
public enum FeedbackStatus {
    DRAFT,
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED
}
