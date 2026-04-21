package com.pesu.cfs.model;

import com.pesu.cfs.model.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * State Diagram: Created -> Sent -> Delivered/Failed -> Read/Unread -> Archived
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.CREATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    // --- State transitions matching state diagram ---

    public void send() {
        if (this.status == NotificationStatus.CREATED) {
            this.status = NotificationStatus.SENT;
            this.sentAt = LocalDateTime.now();
        }
    }

    public void deliver() {
        if (this.status == NotificationStatus.SENT) {
            this.status = NotificationStatus.DELIVERED;
        }
    }

    public void markDeliveryFailed() {
        if (this.status == NotificationStatus.SENT) {
            this.status = NotificationStatus.FAILED;
        }
    }

    public void markRead() {
        if (this.status == NotificationStatus.DELIVERED || this.status == NotificationStatus.UNREAD) {
            this.status = NotificationStatus.READ;
            this.readAt = LocalDateTime.now();
        }
    }

    public void archive() {
        if (this.status == NotificationStatus.READ || this.status == NotificationStatus.UNREAD) {
            this.status = NotificationStatus.ARCHIVED;
        }
    }
}
