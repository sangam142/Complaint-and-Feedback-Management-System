package com.pesu.cfs.service.impl;

import com.pesu.cfs.dto.FeedbackDto;
import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.Feedback;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.FeedbackStatus;
import com.pesu.cfs.pattern.factory.NotificationFactory;
import com.pesu.cfs.repository.ComplaintRepository;
import com.pesu.cfs.repository.FeedbackRepository;
import com.pesu.cfs.repository.NotificationRepository;
import com.pesu.cfs.service.FeedbackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ComplaintRepository complaintRepository;
    private final NotificationRepository notificationRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository,
                               ComplaintRepository complaintRepository,
                               NotificationRepository notificationRepository) {
        this.feedbackRepository = feedbackRepository;
        this.complaintRepository = complaintRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Feedback submitFeedback(FeedbackDto dto, User user) {
        Feedback feedback = Feedback.builder()
                .feedbackType(dto.getFeedbackType())
                .content(dto.getContent())
                .rating(dto.getRating())
                .user(user)
                .status(FeedbackStatus.SUBMITTED) // skip Draft, go straight to Submitted
                .build();

        // Link to complaint if provided
        if (dto.getComplaintId() != null) {
            Complaint complaint = complaintRepository.findById(dto.getComplaintId())
                    .orElseThrow(() -> new RuntimeException("Complaint not found"));
            feedback.setComplaint(complaint);

            // Factory pattern: notify assigned staff about feedback
            if (complaint.getAssignedStaff() != null) {
                var notification = NotificationFactory.createFeedbackReceived(complaint, complaint.getAssignedStaff());
                notification.send();
                notification.deliver();
                notificationRepository.save(notification);
            }
        }

        return feedbackRepository.save(feedback);
    }

    @Override
    public Optional<Feedback> findById(Long id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public List<Feedback> findByUser(User user) {
        return feedbackRepository.findByUser(user);
    }

    @Override
    public List<Feedback> findByStatus(FeedbackStatus status) {
        return feedbackRepository.findByStatus(status);
    }

    @Override
    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    /**
     * State: Submitted -> UnderReview -> Approved / Rejected
     */
    @Override
    public void reviewFeedback(Long feedbackId, boolean approved) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        feedback.startReview();
        if (approved) {
            feedback.approve();
        } else {
            feedback.reject();
        }
        feedbackRepository.save(feedback);
    }

    @Override
    public Double getOverallAverageRating() {
        return feedbackRepository.findOverallAverageRating();
    }
}
