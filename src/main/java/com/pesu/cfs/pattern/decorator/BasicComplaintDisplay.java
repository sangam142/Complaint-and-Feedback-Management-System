package com.pesu.cfs.pattern.decorator;

import com.pesu.cfs.model.Complaint;

/**
 * Base component: returns the raw complaint description.
 */
public class BasicComplaintDisplay implements ComplaintDisplay {

    private final Complaint complaint;

    public BasicComplaintDisplay(Complaint complaint) {
        this.complaint = complaint;
    }

    @Override
    public String getDisplayText() {
        return complaint.getDescription();
    }
}
