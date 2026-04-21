package com.pesu.cfs.pattern.observer;

import com.pesu.cfs.model.Complaint;

/**
 * =====================================================
 * DESIGN PATTERN: Observer (Behavioral)
 * =====================================================
 * 
 * Purpose: Defines a one-to-many dependency so that when a
 * complaint's state changes, all registered observers are
 * notified automatically. This decouples the complaint
 * lifecycle logic from notification/logging side effects.
 * 
 * Design Principle Applied: DIP (Dependency Inversion)
 * — Services depend on the ComplaintObserver abstraction,
 *   not on concrete notification implementations.
 * 
 * Design Principle Applied: OCP (Open/Closed Principle)
 * — New observers (e.g., email, SMS, audit log) can be
 *   added without modifying the complaint service.
 */
public interface ComplaintObserver {

    void onComplaintSubmitted(Complaint complaint);
    void onComplaintAssigned(Complaint complaint);
    void onComplaintStatusChanged(Complaint complaint);
    void onComplaintResolved(Complaint complaint);
    void onComplaintEscalated(Complaint complaint);
    void onComplaintReopened(Complaint complaint);
}
