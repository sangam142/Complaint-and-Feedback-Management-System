package com.pesu.cfs.pattern.decorator;

/**
 * =====================================================
 * DESIGN PATTERN: Decorator (Structural)
 * =====================================================
 * 
 * Purpose: Dynamically adds responsibilities to complaint
 * descriptions at runtime. For example, a complaint can be
 * decorated with priority tags, timestamps, or category
 * labels when generating reports or display summaries,
 * without modifying the Complaint entity itself.
 * 
 * Design Principle Applied: OCP (Open/Closed Principle)
 * — New decorations can be added without modifying the
 *   base ComplaintDisplay or existing decorators.
 * 
 * Design Principle Applied: LSP (Liskov Substitution)
 * — All decorators are substitutable for the base
 *   ComplaintDisplay interface.
 */
public interface ComplaintDisplay {
    String getDisplayText();
}
