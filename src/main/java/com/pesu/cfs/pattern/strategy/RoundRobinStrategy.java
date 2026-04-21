package com.pesu.cfs.pattern.strategy;

import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Assigns complaints to staff in a round-robin fashion.
 */
@Component("roundRobinStrategy")
public class RoundRobinStrategy implements AssignmentStrategy {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public User assignStaff(Complaint complaint, List<User> availableStaff) {
        if (availableStaff.isEmpty()) return null;
        int index = counter.getAndIncrement() % availableStaff.size();
        return availableStaff.get(index);
    }

    @Override
    public String getStrategyName() {
        return "Round Robin";
    }
}
