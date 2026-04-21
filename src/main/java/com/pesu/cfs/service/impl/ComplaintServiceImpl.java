package com.pesu.cfs.service.impl;

import com.pesu.cfs.dto.ComplaintDto;
import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.AccountStatus;
import com.pesu.cfs.model.enums.ComplaintStatus;
import com.pesu.cfs.model.enums.Role;
import com.pesu.cfs.pattern.observer.ComplaintObserver;
import com.pesu.cfs.pattern.strategy.AssignmentStrategy;
import com.pesu.cfs.repository.ComplaintRepository;
import com.pesu.cfs.repository.UserRepository;
import com.pesu.cfs.service.ComplaintService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final List<ComplaintObserver> observers;       // Observer pattern
    private final AssignmentStrategy assignmentStrategy;   // Strategy pattern

    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                UserRepository userRepository,
                                List<ComplaintObserver> observers,
                                @Qualifier("loadBalancedStrategy") AssignmentStrategy assignmentStrategy) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.observers = observers;
        this.assignmentStrategy = assignmentStrategy;
    }

    @Override
    public Complaint submitComplaint(ComplaintDto dto, User customer) {
        String complaintId = generateComplaintId();

        Complaint complaint = Complaint.builder()
                .complaintId(complaintId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .priority(dto.getPriority())
                .status(ComplaintStatus.OPEN)
                .customer(customer)
                .createdAt(LocalDateTime.now())
                .build();

        complaint = complaintRepository.save(complaint);

        // Observer pattern: notify all observers
        for (ComplaintObserver observer : observers) {
            observer.onComplaintSubmitted(complaint);
        }

        return complaint;
    }

    @Override
    public Optional<Complaint> findById(Long id) {
        return complaintRepository.findById(id);
    }

    @Override
    public Optional<Complaint> findByComplaintId(String complaintId) {
        return complaintRepository.findByComplaintId(complaintId);
    }

    @Override
    public List<Complaint> findByCustomer(User customer) {
        return complaintRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    @Override
    public List<Complaint> findByStaff(User staff) {
        return complaintRepository.findByAssignedStaffOrderByCreatedAtDesc(staff);
    }

    @Override
    public List<Complaint> findAll() {
        return complaintRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    public List<Complaint> findByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status);
    }

    /**
     * Manual assignment by admin — assigns a specific staff member.
     */
    @Override
    public void assignComplaint(Long complaintId, Long staffId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        complaint.setAssignedStaff(staff);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaintRepository.save(complaint);

        for (ComplaintObserver observer : observers) {
            observer.onComplaintAssigned(complaint);
        }
    }

    /**
     * Strategy pattern: auto-assign using the configured strategy.
     */
    @Override
    public void autoAssignComplaint(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        List<User> activeStaff = userRepository.findByRoleAndAccountStatus(
                Role.SUPPORT_STAFF, AccountStatus.ACTIVE);

        User assigned = assignmentStrategy.assignStaff(complaint, activeStaff);
        if (assigned == null) {
            throw new RuntimeException("No available support staff");
        }

        complaint.setAssignedStaff(assigned);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaintRepository.save(complaint);

        for (ComplaintObserver observer : observers) {
            observer.onComplaintAssigned(complaint);
        }
    }

    /**
     * State: Assigned -> InProgress (workStarted)
     */
    @Override
    public void startWork(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (complaint.getStatus() != ComplaintStatus.ASSIGNED &&
            complaint.getStatus() != ComplaintStatus.REOPENED) {
            throw new RuntimeException("Cannot start work on complaint with status: " + complaint.getStatus());
        }

        complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        complaintRepository.save(complaint);

        for (ComplaintObserver observer : observers) {
            observer.onComplaintStatusChanged(complaint);
        }
    }

    /**
     * State: InProgress — addResponse (self-loop on InProgress)
     */
    @Override
    public void addResponse(Long complaintId, String response) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        String existing = complaint.getResolution() != null ? complaint.getResolution() + "\n---\n" : "";
        complaint.setResolution(existing + "[" + LocalDateTime.now().toString() + "] " + response);
        complaintRepository.save(complaint);

        for (ComplaintObserver observer : observers) {
            observer.onComplaintStatusChanged(complaint);
        }
    }

    /**
     * State: InProgress -> Resolved (issueFixed)
     */
    @Override
    public void resolveComplaint(Long complaintId, String resolution) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(ComplaintStatus.RESOLVED);
        String existing = complaint.getResolution() != null ? complaint.getResolution() + "\n---\n" : "";
        complaint.setResolution(existing + "[RESOLVED] " + resolution);
        complaint.setResolvedAt(LocalDateTime.now());
        complaintRepository.save(complaint);

        for (ComplaintObserver observer : observers) {
            observer.onComplaintResolved(complaint);
        }
    }

    /**
     * State: InProgress -> Escalated (SLA exceeded)
     */
    @Override
    public void escalateComplaint(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(ComplaintStatus.ESCALATED);
        complaintRepository.save(complaint);

        for (ComplaintObserver observer : observers) {
            observer.onComplaintEscalated(complaint);
        }
    }

    /**
     * State: Resolved -> Closed (customerVerified)
     */
    @Override
    public void closeComplaint(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(ComplaintStatus.CLOSED);
        complaintRepository.save(complaint);

        for (ComplaintObserver observer : observers) {
            observer.onComplaintStatusChanged(complaint);
        }
    }

    /**
     * State: Resolved -> Reopened (notSatisfied)
     * Then Reopened -> InProgress (reprocess) via startWork()
     */
    @Override
    public void reopenComplaint(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(ComplaintStatus.REOPENED);
        complaintRepository.save(complaint);

        for (ComplaintObserver observer : observers) {
            observer.onComplaintReopened(complaint);
        }
    }

    @Override
    public Map<String, Long> getStatusCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (ComplaintStatus status : ComplaintStatus.values()) {
            counts.put(status.name(), complaintRepository.countByStatus(status));
        }
        return counts;
    }

    @Override
    public List<Object[]> getCategoryCounts() {
        return complaintRepository.countByCategory();
    }

    private String generateComplaintId() {
        long count = complaintRepository.count() + 1;
        return "CMP-" + Year.now().getValue() + "-" + String.format("%05d", count);
    }
}
