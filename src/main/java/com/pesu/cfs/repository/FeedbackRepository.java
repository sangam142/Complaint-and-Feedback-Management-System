package com.pesu.cfs.repository;

import com.pesu.cfs.model.Complaint;
import com.pesu.cfs.model.Feedback;
import com.pesu.cfs.model.User;
import com.pesu.cfs.model.enums.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUser(User user);
    List<Feedback> findByComplaint(Complaint complaint);
    List<Feedback> findByStatus(FeedbackStatus status);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.complaint = ?1 AND f.status = 'APPROVED'")
    Double findAverageRatingByComplaint(Complaint complaint);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.status = 'APPROVED'")
    Double findOverallAverageRating();
}
