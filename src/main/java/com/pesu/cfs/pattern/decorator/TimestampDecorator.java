package com.pesu.cfs.pattern.decorator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Concrete Decorator: Appends a timestamp to the display.
 */
public class TimestampDecorator extends ComplaintDisplayDecorator {

    private final LocalDateTime timestamp;

    public TimestampDecorator(ComplaintDisplay wrapped, LocalDateTime timestamp) {
        super(wrapped);
        this.timestamp = timestamp;
    }

    @Override
    public String getDisplayText() {
        String formatted = timestamp.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));
        return super.getDisplayText() + " [Filed: " + formatted + "]";
    }
}
