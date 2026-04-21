package com.pesu.cfs.dto;

import com.pesu.cfs.model.enums.ComplaintCategory;
import com.pesu.cfs.model.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplaintDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private ComplaintCategory category;

    private Priority priority = Priority.MEDIUM;
}
