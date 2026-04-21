package com.pesu.cfs.dto;

import com.pesu.cfs.model.enums.FeedbackType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackDto {

    @NotNull(message = "Feedback type is required")
    private FeedbackType feedbackType;

    private String content;

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    private Long complaintId;
}
