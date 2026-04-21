package com.pesu.cfs.pattern.factory;

import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.Notification;
import com.pesu.cfs.model.User;

/**
 * =====================================================
 * DESIGN PATTERN: Factory Method (Creational)
 * =====================================================
 * 
 * Purpose: Encapsulates notification creation logic so that the
 * client code (services) doesn't need to know how to construct
 * different types of notifications. Each factory method produces
 * a Notification with the correct title, message, and recipient
 * based on the complaint lifecycle event.
 * 
 * Design Principle Applied: OCP (Open/Closed Principle)
 * — New notification types can be added by adding new factory
 *   methods without modifying existing code.
 * 
 * Design Principle Applied: SRP (Single Responsibility)
 * — Notification construction logic is isolated here, not
 *   scattered across service classes.
 */
public class NotificationFactory {

    public static Notification createComplaintSubmitted(Complaint complaint, User customer) {
        return Notification.builder()
                .title("Complaint Submitted")
                .message("Your complaint '" + complaint.getTitle() + "' has been submitted successfully. " +
                         "Tracking ID: " + complaint.getComplaintId())
                .user(customer)
                .build();
    }

    public static Notification createComplaintAssigned(Complaint complaint, User staff) {
        return Notification.builder()
                .title("New Complaint Assigned")
                .message("Complaint '" + complaint.getTitle() + "' (" + complaint.getComplaintId() +
                         ") has been assigned to you. Priority: " + complaint.getPriority())
                .user(staff)
                .build();
    }

    public static Notification createStatusUpdate(Complaint complaint, User customer) {
        return Notification.builder()
                .title("Complaint Status Updated")
                .message("Your complaint '" + complaint.getTitle() + "' status changed to: " +
                         complaint.getStatus())
                .user(customer)
                .build();
    }

    public static Notification createComplaintResolved(Complaint complaint, User customer) {
        return Notification.builder()
                .title("Complaint Resolved")
                .message("Your complaint '" + complaint.getTitle() + "' has been resolved. " +
                         "Please verify and provide feedback.")
                .user(customer)
                .build();
    }

    public static Notification createComplaintEscalated(Complaint complaint, User admin) {
        return Notification.builder()
                .title("Complaint Escalated — SLA Exceeded")
                .message("Complaint '" + complaint.getTitle() + "' (" + complaint.getComplaintId() +
                         ") has been escalated due to SLA breach.")
                .user(admin)
                .build();
    }

    public static Notification createComplaintReopened(Complaint complaint, User staff) {
        return Notification.builder()
                .title("Complaint Reopened")
                .message("Complaint '" + complaint.getTitle() + "' has been reopened by the customer. " +
                         "Please reprocess.")
                .user(staff)
                .build();
    }

    public static Notification createFeedbackReceived(Complaint complaint, User staff) {
        return Notification.builder()
                .title("Feedback Received")
                .message("New feedback submitted for complaint '" + complaint.getTitle() + "'.")
                .user(staff)
                .build();
    }

    public static Notification createAccountAction(User user, String action) {
        return Notification.builder()
                .title("Account " + action)
                .message("Your account has been " + action.toLowerCase() + ". " +
                         "Contact admin if you have questions.")
                .user(user)
                .build();
    }
}
