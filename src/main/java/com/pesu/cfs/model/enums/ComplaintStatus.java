package com.pesu.cfs.model.enums;

/**
 * Complaint State Diagram:
 * Open -> Assigned -> InProgress -> Resolved/Escalated
 * Resolved -> Closed (customerVerified) / Reopened (notSatisfied)
 * Reopened -> InProgress (reprocess)
 * Escalated -> InProgress (reassigned from Escalated handled via reassign)
 */
public enum ComplaintStatus {
    OPEN,
    ASSIGNED,
    IN_PROGRESS,
    ESCALATED,
    RESOLVED,
    CLOSED,
    REOPENED
}
