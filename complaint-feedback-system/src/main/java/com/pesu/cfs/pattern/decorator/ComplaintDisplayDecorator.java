package com.pesu.cfs.pattern.decorator;

/**
 * Abstract decorator: wraps a ComplaintDisplay and delegates to it.
 */
public abstract class ComplaintDisplayDecorator implements ComplaintDisplay {

    protected final ComplaintDisplay wrapped;

    public ComplaintDisplayDecorator(ComplaintDisplay wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getDisplayText() {
        return wrapped.getDisplayText();
    }
}
