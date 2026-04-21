package com.pesu.cfs.pattern.strategy;

import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.ComplaintStatus;
import com.pesu.cfs.repository.ComplaintRepository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Assigns complaints to the staff member with the fewest active complaints.
 */
@Component("loadBalancedStrategy")
public class LoadBalancedStrategy implements AssignmentStrategy {

    private final ComplaintRepository complaintRepository;

    public LoadBalancedStrategy(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Override
    public User assignStaff(Complaint complaint, List<User> availableStaff) {
        if (availableStaff.isEmpty()) return null;

        return availableStaff.stream()
                .min(Comparator.comparingLong(staff -> {
                    List<Complaint> active = complaintRepository.findByStatusAndAssignedStaff(
                            ComplaintStatus.IN_PROGRESS, staff);
                    return active.size();
                }))
                .orElse(availableStaff.get(0));
    }

    @Override
    public String getStrategyName() {
        return "Load Balanced";
    }
}
