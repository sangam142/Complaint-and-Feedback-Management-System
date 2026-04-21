package com.pesu.cfs.pattern.decorator;

import com.pesu.cfs.model.enums.Priority;

/**
 * Concrete Decorator: Prepends a priority badge to the display.
 */
public class PriorityDecorator extends ComplaintDisplayDecorator {

    private final Priority priority;

    public PriorityDecorator(ComplaintDisplay wrapped, Priority priority) {
        super(wrapped);
        this.priority = priority;
    }

    @Override
    public String getDisplayText() {
        String badge = switch (priority) {
            case CRITICAL -> "[🔴 CRITICAL] ";
            case HIGH     -> "[🟠 HIGH] ";
            case MEDIUM   -> "[🟡 MEDIUM] ";
            case LOW      -> "[🟢 LOW] ";
        };
        return badge + super.getDisplayText();
    }
}
