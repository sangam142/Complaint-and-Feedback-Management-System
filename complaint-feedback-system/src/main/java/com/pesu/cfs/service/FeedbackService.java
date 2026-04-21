package com.pesu.cfs.service;

import com.pesu.cfs.dto.FeedbackDto;
import com.pesu.cfs.model.Feedback;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.FeedbackStatus;

import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    Feedback submitFeedback(FeedbackDto dto, User user);
    Optional<Feedback> findById(Long id);
    List<Feedback> findByUser(User user);
    List<Feedback> findByStatus(FeedbackStatus status);
    List<Feedback> findAll();
    void reviewFeedback(Long feedbackId, boolean approved);
    Double getOverallAverageRating();
}
