package com.pesu.cfs.pattern.decorator;

import com.pesu.cfs.model.enums.ComplaintCategory;

/**
 * Concrete Decorator: Prepends a category tag to the display.
 */
public class CategoryDecorator extends ComplaintDisplayDecorator {

    private final ComplaintCategory category;

    public CategoryDecorator(ComplaintDisplay wrapped, ComplaintCategory category) {
        super(wrapped);
        this.category = category;
    }

    @Override
    public String getDisplayText() {
        return "[" + category.name() + "] " + super.getDisplayText();
    }
}
