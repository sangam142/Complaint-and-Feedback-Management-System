package com.pesu.cfs.model.enums;

/**
 * Matches Notification State Diagram:
 * Created -> Sent -> Delivered/Failed -> Read/Unread -> Archived
 */
public enum NotificationStatus {
    CREATED,
    SENT,
    DELIVERED,
    FAILED,
    READ,
    UNREAD,
    ARCHIVED
}
