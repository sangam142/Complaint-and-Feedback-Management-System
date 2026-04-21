package com.pesu.cfs.model;

import com.pesu.cfs.model.enums.AccountStatus;
import com.pesu.cfs.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    private String fullName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * State Diagram: Registered -> Active -> Suspended / Deactivated
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.REGISTERED;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastLogin;

    // Complaints filed by this user (Customer)
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Complaint> complaints = new ArrayList<>();

    // Complaints assigned to this user (Support Staff)
    @OneToMany(mappedBy = "assignedStaff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Complaint> assignedComplaints = new ArrayList<>();

    // Feedbacks given by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Feedback> feedbacks = new ArrayList<>();

    // Notifications for this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    // --- State transitions matching state diagram ---

    public void verifyUser() {
        if (this.accountStatus == AccountStatus.REGISTERED) {
            this.accountStatus = AccountStatus.ACTIVE;
        }
    }

    public void suspendAccount() {
        if (this.accountStatus == AccountStatus.ACTIVE) {
            this.accountStatus = AccountStatus.SUSPENDED;
        }
    }

    public void reinstateAccount() {
        if (this.accountStatus == AccountStatus.SUSPENDED) {
            this.accountStatus = AccountStatus.ACTIVE;
        }
    }

    public void deactivateAccount() {
        if (this.accountStatus == AccountStatus.ACTIVE) {
            this.accountStatus = AccountStatus.DEACTIVATED;
        }
    }
}
