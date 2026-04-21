package com.pesu.cfs.model.enums;

/**
 * Matches User Account State Diagram:
 * Registered -> Active -> Suspended / Deactivated
 */
public enum AccountStatus {
    REGISTERED,
    ACTIVE,
    SUSPENDED,
    DEACTIVATED
}
