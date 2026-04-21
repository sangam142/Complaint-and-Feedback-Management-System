package com.pesu.cfs.pattern.strategy;

import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.User;

import java.util.List;

/**
 * =====================================================
 * DESIGN PATTERN: Strategy (Behavioral — bonus 4th pattern)
 * =====================================================
 * 
 * Purpose: Defines a family of algorithms for assigning
 * complaints to support staff. The admin can switch
 * strategies at runtime (round-robin, load-balanced, etc.)
 * without modifying the assignment logic.
 * 
 * Design Principle Applied: DIP (Dependency Inversion)
 * — The complaint service depends on this interface,
 *   not on any concrete assignment algorithm.
 * 
 * Design Principle Applied: SRP (Single Responsibility)
 * — Each strategy class has exactly one reason to change:
 *   its assignment algorithm.
 */
public interface AssignmentStrategy {
    User assignStaff(Complaint complaint, List<User> availableStaff);
    String getStrategyName();
}
